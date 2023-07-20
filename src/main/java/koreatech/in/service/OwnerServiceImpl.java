package koreatech.in.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import koreatech.in.domain.User.EmailAddress;
import koreatech.in.domain.User.owner.CertificationCode;
import koreatech.in.domain.User.owner.Owner;
import koreatech.in.domain.User.owner.OwnerAttachment;
import koreatech.in.domain.User.owner.OwnerAttachments;
import koreatech.in.domain.User.owner.OwnerInCertification;
import koreatech.in.domain.User.owner.OwnerInVerification;
import koreatech.in.domain.User.owner.OwnerPartition;
import koreatech.in.domain.User.owner.OwnerShop;
import koreatech.in.dto.normal.user.owner.request.OwnerRegisterRequest;
import koreatech.in.dto.normal.user.owner.request.OwnerUpdateRequest;
import koreatech.in.dto.normal.user.owner.request.VerifyCodeRequest;
import koreatech.in.dto.normal.user.owner.request.VerifyEmailRequest;
import koreatech.in.dto.normal.user.owner.response.OwnerResponse;
import koreatech.in.dto.normal.user.owner.response.VerifyCodeResponse;
import koreatech.in.exception.BaseException;
import koreatech.in.exception.ExceptionInformation;
import koreatech.in.mapstruct.OwnerConverter;
import koreatech.in.repository.user.OwnerMapper;
import koreatech.in.repository.user.UserMapper;
import koreatech.in.util.RandomGenerator;
import koreatech.in.util.SesMailSender;
import koreatech.in.util.SlackNotiSender;
import koreatech.in.util.StringRedisUtilStr;
import koreatech.in.util.StringRedisUtilObj;
import koreatech.in.util.jwt.TemporaryAccessJwtGenerator;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.velocity.VelocityEngineUtils;

@Service
public class OwnerServiceImpl implements OwnerService {

    private static final String OWNER_CERTIFICATE_FORM_LOCATION = "mail/owner_certificate_number.vm";
    private static final String OWNER_PASSWORD_CHANGE_CERTIFICATE_FORM_LOCATION = "mail/change_password_certificate_number.vm";
    private static final String CERTIFICATION_CODE = "certification-code";
    private static final String EMAIL_ADDRESS = "email-address";
    private static final String YEAR = "year";
    private static final String MONTH = "month";
    private static final String DAY_OF_MONTH = "day-of-month";
    private static final String HOUR = "hour";
    private static final String MINUTE = "minute";

    @Autowired
    private StringRedisUtilObj stringRedisUtilObj;

    @Autowired
    private SesMailSender sesMailSender;

    @Autowired
    private VelocityEngine velocityEngine;

    @Autowired
    private JwtValidator jwtValidator;

    @Autowired
    private TemporaryAccessJwtGenerator temporaryAccessJwtGenerator;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SlackNotiSender slackNotiSender;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OwnerMapper ownerMapper;

    @Override
    public void requestVerificationToChangePassword(VerifyEmailRequest verifyEmailRequest) {
        EmailAddress emailAddress = OwnerConverter.INSTANCE.toEmailAddress(verifyEmailRequest);
        validateEmailFromOwner(emailAddress);

        CertificationCode certificationCode = RandomGenerator.getCertificationCode();
        OwnerInVerification ownerInVerification = OwnerInVerification.of(certificationCode, emailAddress);

        emailAddress.validateSendable();

        putRedisToChangePassword(emailAddress.getEmailAddress(), ownerInVerification);
        sendMailWithTimes(emailAddress, certificationCode);

        slackNotiSender.noticeEmailVerification(ownerInVerification);
    }

    private void putRedisToChangePassword(String emailAddress, OwnerInVerification ownerInVerification) {
        try {
            stringRedisUtilObj.setDataAsString(StringRedisUtilObj.makeOwnerPasswordChangeKeyFor(emailAddress),
                    ownerInVerification, 2L, TimeUnit.HOURS);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    private void validateEmailFromOwner(EmailAddress emailAddress) {
        if (!userMapper.isOwnerEmail(emailAddress)) {
            throw new BaseException(ExceptionInformation.NOT_OWNER_EMAIL);
        }
    }

    private void sendMailWithTimes(EmailAddress emailAddress, CertificationCode certificationCode) {
        sesMailSender.sendMail(SesMailSender.COMPANY_NO_REPLY_EMAIL_ADDRESS, emailAddress.getEmailAddress(),
                SesMailSender.OWNER_EMAIL_VERIFICATION_SUBJECT, mailFormWithTimes(certificationCode,emailAddress.getEmailAddress()));

    }

    private String mailFormWithTimes(CertificationCode certificationCode, String emailAddress) {
        LocalDateTime now = LocalDateTime.now();
        Map<String, Object> model = new HashMap<>();
        model.put(CERTIFICATION_CODE, certificationCode.getValue());
        model.put(EMAIL_ADDRESS, emailAddress);
        model.put(YEAR,now.getYear());
        model.put(MONTH, now.getMonthValue());
        model.put(DAY_OF_MONTH, now.getDayOfMonth());
        model.put(HOUR, now.getHour());
        model.put(MINUTE, now.getMinute());

        return VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, OWNER_PASSWORD_CHANGE_CERTIFICATE_FORM_LOCATION,
                StandardCharsets.UTF_8.name(), model);
    }

    @Override
    public void requestVerification(VerifyEmailRequest verifyEmailRequest) {

        EmailAddress emailAddress = OwnerConverter.INSTANCE.toEmailAddress(verifyEmailRequest);
        validateEmailUniqueness(emailAddress);

        CertificationCode certificationCode = RandomGenerator.getCertificationCode();
        OwnerInVerification ownerInVerification = OwnerInVerification.of(certificationCode, emailAddress);

        emailAddress.validateSendable();

        putRedisFor(emailAddress.getEmailAddress(), ownerInVerification);
        sendMailFor(emailAddress, certificationCode);

        slackNotiSender.noticeEmailVerification(ownerInVerification);
    }

    @Override
    public VerifyCodeResponse certificate(VerifyCodeRequest verifyCodeRequest) {
        OwnerInCertification ownerInCertification = OwnerConverter.INSTANCE.toOwnerInCertification(verifyCodeRequest);

        OwnerInVerification ownerInRedis = getOwnerInRedis(ownerInCertification.getEmail());

        ownerInRedis.validateFor(ownerInCertification);
        ownerInRedis.setIs_authed(true);

        putRedisFor(ownerInCertification.getEmail(), ownerInRedis);
        String temporaryAccessToken = temporaryAccessJwtGenerator.generateToken(null);
        return OwnerConverter.INSTANCE.toVerifyCodeResponse(temporaryAccessToken);
    }

    @Transactional
    @Override
    public void register(OwnerRegisterRequest ownerRegisterRequest) {
        // TODO 23.02.12. 박한수 사업자등록번호 중복되는 경우 예외 처리 필요.

        OwnerConverter ownerConverter = OwnerConverter.INSTANCE;

        Owner owner = ownerConverter.toNewOwner(ownerRegisterRequest);

        EmailAddress ownerEmailAddress = EmailAddress.from(owner.getEmail());

        validateEmailUniqueness(ownerEmailAddress);
        validateOwnerInRedis(ownerEmailAddress);

        encodePassword(owner);

        createInDBFor(owner);

        OwnerShop ownerShop = ownerConverter.toOwnerShop(owner.getId(), ownerRegisterRequest);
        putRedisForRequestShop(ownerShop);

        slackNotiSender.noticeRegisterComplete(owner);

        removeRedisFrom(ownerEmailAddress);
    }

    @Override
    public OwnerResponse getOwner() {
        Integer userId = jwtValidator.validate().getId();

        Owner ownerInDB = ownerMapper.getOwnerById(userId.longValue());

        return OwnerConverter.INSTANCE.toOwnerResponse(ownerInDB);
    }

    @Override
    public void deleteAttachment(Integer attachmentId) {
        Integer userId = jwtValidator.validate().getId();
        OwnerAttachment attachmentInDB = ownerMapper.getOwnerAttachmentById(attachmentId.longValue());

        validateInDelete(userId, attachmentInDB);

        ownerMapper.deleteOwnerAttachmentLogically(attachmentId.longValue());
    }

    @Transactional
    @Override
    public OwnerResponse update(OwnerUpdateRequest ownerUpdateRequest) {
        Owner owner = OwnerConverter.INSTANCE.toOwner(ownerUpdateRequest);
        Owner ownerInToken = getOwnerInToken();

        owner.setId(ownerInToken.getId());
        updateDBFor(owner, ownerInToken);

        return OwnerConverter.INSTANCE.toOwnerResponse(ownerInToken);
    }

    private void updateDBFor(Owner owner, Owner ownerInToken) {
        OwnerAttachments ownerAttachments = ownerAttachmentsFillWithOwnerId(owner);
        OwnerAttachments ownerAttachmentsInDB = OwnerAttachments.from(ownerInToken.getAttachments());

        OwnerAttachments updatedAttachments = updateAttachment(ownerAttachments, ownerAttachmentsInDB);

        ownerInToken.setAttachments(updatedAttachments.getAttachments());
    }

    private static OwnerAttachments ownerAttachmentsFillWithOwnerId(Owner owner) {
        return OwnerConverter.INSTANCE.toOwnerAttachments(owner);
    }

    private OwnerAttachments updateAttachment(OwnerAttachments ownerAttachments,
                                              OwnerAttachments ownerAttachmentsInDB) {
        OwnerAttachments result = ownerAttachmentsInDB.intersectionWith(ownerAttachments);

        OwnerAttachments toAdd = ownerAttachments.removeDuplicatesFrom(ownerAttachmentsInDB);
        OwnerAttachments toDelete = ownerAttachmentsInDB.removeDuplicatesFrom(ownerAttachments);

        if (!toAdd.isEmpty()) {
            toAdd.getAttachments().forEach(ownerAttachment -> ownerMapper.insertOwnerAttachment(ownerAttachment));
            result.addAllFrom(toAdd);
        }
        if (!toDelete.isEmpty()) {
            ownerMapper.deleteOwnerAttachmentsLogically(toDelete);
        }
        return result;
    }

    private Owner getOwnerInToken() {
        Integer userId = jwtValidator.validateAndGetUserId();
        Owner ownerInDB = ownerMapper.getOwnerById(userId.longValue());

        if (ownerInDB == null) {
            throw new BaseException(ExceptionInformation.BAD_ACCESS);
        }

        return ownerInDB;
    }

    private static void validateInDelete(Integer userId, OwnerAttachment attachmentInDB) {
        if (attachmentInDB == null) {
            throw new BaseException(ExceptionInformation.OWNER_ATTACHMENT_NOT_FOUND);
        }

        if (!attachmentInDB.getOwnerId().equals(userId)) {
            throw new BaseException(ExceptionInformation.FORBIDDEN);
        }
    }

    private void validateEmailUniqueness(EmailAddress emailAddress) {
        if (userMapper.isEmailAlreadyExist(emailAddress).equals(true)) {
            throw new BaseException(ExceptionInformation.EMAIL_DUPLICATED);
        }
    }

    private static void validateRedis(OwnerInVerification ownerInRedis) {
        if (ownerInRedis == null) {
            throw new BaseException(ExceptionInformation.EMAIL_ADDRESS_SAVE_EXPIRED);
        }
        ownerInRedis.validateFields();
    }

    private void validateOwnerInRedis(EmailAddress emailAddress) {
        OwnerInVerification ownerInRedis = getOwnerInRedis(emailAddress.getEmailAddress());
        ownerInRedis.validateCertificationComplete();
    }

    private void removeRedisFrom(EmailAddress emailAddress) {
        stringRedisUtilObj.deleteData(StringRedisUtilObj.makeOwnerKeyFor(emailAddress.getEmailAddress()));
    }

    private void putRedisFor(String emailAddress, OwnerInVerification ownerInVerification) {
        try {
            stringRedisUtilObj.setDataAsString(StringRedisUtilObj.makeOwnerKeyFor(emailAddress),
                    ownerInVerification, 2L, TimeUnit.HOURS);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    private void putRedisForRequestShop(OwnerShop ownerShop) {
        try {
            stringRedisUtilObj.setDataAsString(StringRedisUtilObj.makeOwnerShopKeyFor(ownerShop.getOwner_id()), ownerShop);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    private OwnerInVerification getOwnerInRedis(String emailAddress) {
        Object json;
        try {
            json = stringRedisUtilObj.getDataAsString(StringRedisUtilStr.makeOwnerKeyFor(emailAddress), OwnerInVerification.class);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        OwnerInVerification ownerInRedis = (OwnerInVerification) json;
        validateRedis(ownerInRedis);

        return ownerInRedis;
    }

    private void sendMailFor(EmailAddress emailAddress, CertificationCode certificationCode) {

        sesMailSender.sendMail(SesMailSender.COMPANY_NO_REPLY_EMAIL_ADDRESS, emailAddress.getEmailAddress(),
                SesMailSender.OWNER_EMAIL_VERIFICATION_SUBJECT, mailFormFor(certificationCode));
    }

    private String mailFormFor(CertificationCode certificationCode) {

        Map<String, Object> model = new HashMap<>();
        model.put(CERTIFICATION_CODE, certificationCode.getValue());

        return VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, OWNER_CERTIFICATE_FORM_LOCATION,
                StandardCharsets.UTF_8.name(), model);
    }

    private void encodePassword(Owner owner) {
        owner.setPassword(passwordEncoder.encode(owner.getPassword()));
    }

    private void createInDBFor(Owner owner) {
        owner.enrichAuthComplete();

        try {
            insertUserAndUpdateId(owner);

            ownerMapper.insertOwner(owner);

            if (owner.hasRegistrationInformation()) {
                ownerMapper.insertOwnerAttachments(ownerAttachmentsFillWithOwnerId(owner));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void insertUserAndUpdateId(Owner owner) throws SQLException {
        userMapper.insertUser(owner);
    }
}
