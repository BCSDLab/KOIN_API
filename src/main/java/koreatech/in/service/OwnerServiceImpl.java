package koreatech.in.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import koreatech.in.domain.User.owner.CertificationCode;
import koreatech.in.domain.User.owner.EmailAddress;
import koreatech.in.domain.User.owner.Owner;
import koreatech.in.domain.User.owner.OwnerInCertification;
import koreatech.in.domain.User.owner.OwnerInVerification;
import koreatech.in.dto.normal.user.owner.request.OwnerRegisterRequest;
import koreatech.in.dto.normal.user.owner.request.VerifyCodeRequest;
import koreatech.in.dto.normal.user.owner.request.VerifyEmailRequest;
import koreatech.in.exception.BaseException;
import koreatech.in.exception.ExceptionInformation;
import koreatech.in.mapstruct.OwnerConverter;
import koreatech.in.util.RandomGenerator;
import koreatech.in.util.SesMailSender;
import koreatech.in.util.StringRedisUtilStr;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Override
    @Transactional
    public void register(OwnerRegisterRequest ownerRegisterRequest) {
        Owner owner = OwnerConverter.INSTANCE.toOwner(ownerRegisterRequest);

        validationAndDeleteInRedis(owner);



        //TODO 23.02.05. 박한수 DB쪽 작업 추가하기
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
