package koreatech.in.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import koreatech.in.domain.User.User;
import koreatech.in.domain.User.UserType;
import koreatech.in.domain.User.owner.CertificationCode;
import koreatech.in.domain.User.owner.EmailAddress;
import koreatech.in.domain.User.owner.Owner;
import koreatech.in.domain.User.owner.OwnerInCertification;
import koreatech.in.domain.User.owner.OwnerInVerification;
import koreatech.in.domain.User.owner.OwnerShopAttachments;
import koreatech.in.dto.normal.user.owner.request.OwnerRegisterRequest;
import koreatech.in.dto.normal.user.owner.request.VerifyCodeRequest;
import koreatech.in.dto.normal.user.owner.request.VerifyEmailRequest;
import koreatech.in.exception.BaseException;
import koreatech.in.exception.ExceptionInformation;
import koreatech.in.mapstruct.OwnerConverter;
import koreatech.in.repository.user.OwnerMapper;
import koreatech.in.repository.user.UserMapper;
import koreatech.in.util.RandomGenerator;
import koreatech.in.util.SesMailSender;
import koreatech.in.util.StringRedisUtilStr;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.velocity.VelocityEngineUtils;

@Service
public class OwnerServiceImpl implements OwnerService {

    private static final String OWNER_CERTIFICATE_FORM_LOCATION = "mail/owner_certificate_number.vm";
    private static final String CERTIFICATION_CODE = "certification-code";
    private static final String redisOwnerAuthPrefix = "owner@";

    @Autowired
    private StringRedisUtilStr stringRedisUtilStr;

    @Autowired
    private SesMailSender sesMailSender;

    @Autowired
    private VelocityEngine velocityEngine;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired

    private UserMapper userMapper;

    @Autowired
    private OwnerMapper ownerMapper;

    @Override
    public void requestVerification(VerifyEmailRequest verifyEmailRequest) {

        EmailAddress emailAddress = OwnerConverter.INSTANCE.toEmailAddress(verifyEmailRequest);

        CertificationCode certificationCode = RandomGenerator.getCertificationCode();
        OwnerInVerification ownerInVerification = OwnerInVerification.from(certificationCode);

        putRedisFor(emailAddress.getEmailAddress(), ownerInVerification);
        sendMailFor(emailAddress, certificationCode);

    }

    @Override
    public void certificate(VerifyCodeRequest verifyCodeRequest) {
        OwnerInCertification ownerInCertification = OwnerConverter.INSTANCE.toOwnerInCertification(verifyCodeRequest);

        OwnerInVerification ownerInRedis = getOwnerInRedis(ownerInCertification.getEmail());

        ownerInRedis.validateFor(ownerInCertification);
        ownerInRedis.setIs_authed(true);

        putRedisFor(ownerInCertification.getEmail(), ownerInRedis);
    }

    @Transactional
    @Override
    public void register(OwnerRegisterRequest ownerRegisterRequest) {
        Owner owner = downcastFrom(OwnerConverter.INSTANCE.toUser(ownerRegisterRequest));

        validationAndDeleteInRedis(owner);

        encodePassword(owner);

        createInDB(owner);

        //TODO 23.02.05. 박한수 DB쪽 작업 추가하기
    }

    private static Owner downcastFrom(User user) {
        if(!(user instanceof Owner)) {
            throw new ClassCastException("OwnerConverter에서 User -> Owner로 변환 과정 중 잘못된 다운캐스팅이 발생했습니다.");
        }
        return (Owner) user;
    }

    private void createInDB(Owner owner) {

        owner.setUser_type(UserType.OWNER);
        owner.setIs_authed(true);

        try {
            insertUserAndUpdateId(owner);
            ownerMapper.insertOwner(owner);

            OwnerShopAttachments ownerShopAttachments = OwnerConverter.INSTANCE.toOwnerShopAttachments(owner);

            ownerMapper.insertOwnerShopAttachment(ownerShopAttachments);
        } catch (SQLException e) {
            throw new RuntimeException(e);
//TODO 23.02.06. 컨버터에서 Attachments 안에 뭐있고 뭐있고 이런식으로 하기
            
        }
    }

    private void insertUserAndUpdateId(Owner owner) throws SQLException {
        userMapper.insertUser(owner);
    }

    private void encodePassword(Owner owner) {
        owner.setPassword(passwordEncoder.encode(owner.getPassword()));
    }

    private void removeRedisFrom(String emailAddress) {
        stringRedisUtilStr.deleteData(emailAddress);
    }

    private void validationAndDeleteInRedis(Owner owner) {
        OwnerInVerification ownerInRedis = getOwnerInRedis(owner.getEmail());

        ownerInRedis.validateCertificationComplete();

        removeRedisFrom(owner.getEmail());
    }

    private OwnerInVerification getOwnerInRedis(String emailAddress) {
        Gson gson = new GsonBuilder().create();
        String json = stringRedisUtilStr.valOps.get(redisOwnerAuthPrefix + emailAddress);

        OwnerInVerification ownerInRedis = gson.fromJson(json, OwnerInVerification.class);
        validateRedis(ownerInRedis);

        return ownerInRedis;
    }

    private static void validateRedis(OwnerInVerification ownerInRedis) {
        if(ownerInRedis == null) {
            throw  new BaseException(ExceptionInformation.EMAIL_ADDRESS_SAVE_EXPIRED);
        }
        ownerInRedis.validateFields();
    }

    private void putRedisFor(String emailAddress, OwnerInVerification ownerInVerification) {
        Gson gson = new GsonBuilder().create();

        stringRedisUtilStr.valOps.set(redisOwnerAuthPrefix + emailAddress,
                gson.toJson(ownerInVerification), 2L, TimeUnit.HOURS);
    }

    private void sendMailFor(EmailAddress emailAddress, CertificationCode certificationCode) {
        sesMailSender.sendMail(SesMailSender.COMPANY_NO_REPLY_EMAIL_ADDRESS, emailAddress.getEmailAddress(),
                SesMailSender.OWNER_EMAIL_VERIFICATION_SUBJECT,
                mailFormFor(certificationCode));
    }

    private String mailFormFor(CertificationCode certificationCode) {

        Map<String, Object> model = new HashMap<>();
        model.put(CERTIFICATION_CODE, certificationCode.getValue());

        return VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, OWNER_CERTIFICATE_FORM_LOCATION,
                StandardCharsets.UTF_8.name(), model);
    }
}
