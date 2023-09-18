package koreatech.in.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import koreatech.in.domain.User.EmailAddress;
import koreatech.in.domain.User.User;
import koreatech.in.domain.User.owner.CertificationCode;
import koreatech.in.domain.User.owner.Owner;
import koreatech.in.domain.User.owner.OwnerAttachment;
import koreatech.in.domain.User.owner.OwnerAttachments;
import koreatech.in.domain.User.owner.OwnerInCertification;
import koreatech.in.domain.User.owner.OwnerInVerification;
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
import koreatech.in.util.StringRedisUtilObj;
import koreatech.in.util.StringRedisUtilStr;
import koreatech.in.util.jwt.TemporaryAccessJwtGenerator;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OwnerServiceImpl implements OwnerService {

    private static final String OWNER_CERTIFICATE_FORM_LOCATION = "mail/owner_certificate_number.vm";
    private static final String OWNER_PASSWORD_CHANGE_CERTIFICATE_FORM_LOCATION = "mail/change_password_certificate_number.vm";
    private static final Long STORAGE_TIME = 2L;

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

    @Autowired
    private MailService mailService;

    public void requestVerificationToChangePassword(VerifyEmailRequest verifyEmailRequest) {
        EmailAddress emailAddress = OwnerConverter.INSTANCE.toEmailAddress(verifyEmailRequest);
        validateEmailFromOwner(emailAddress);

        String key = StringRedisUtilObj.makeOwnerPasswordChangeKeyFor(emailAddress.getEmailAddress());
        putRedis(emailAddress, key);

        CertificationCode certificationCode = mailService.sendMailWithTimes(emailAddress, OWNER_PASSWORD_CHANGE_CERTIFICATE_FORM_LOCATION,
                SesMailSender.OWNER_FIND_PASSWORD_EMAIL_VERIFICATION_SUBJECT);

        slackNotiSender.noticeEmailVerification(OwnerInVerification.of(certificationCode, emailAddress));
    }

    private void validateEmailFromOwner(EmailAddress emailAddress) {
        User user = Optional.ofNullable(userMapper.getUserByEmail(emailAddress.getEmailAddress()))
                .orElseThrow(() -> new BaseException(ExceptionInformation.NOT_EXIST_EMAIL));
        if (user.isStudent()) {
            throw new BaseException(ExceptionInformation.NOT_EXIST_EMAIL);
        }
    }

    private void putRedis(EmailAddress emailAddress, String key) {
        CertificationCode certificationCode = RandomGenerator.getCertificationCode();
        OwnerInVerification ownerInVerification = OwnerInVerification.of(certificationCode, emailAddress);

        emailAddress.validateSendable();
        try {
            stringRedisUtilObj.setDataAsString(key, ownerInVerification, STORAGE_TIME, TimeUnit.HOURS);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void requestVerification(VerifyEmailRequest verifyEmailRequest) {
        EmailAddress emailAddress = OwnerConverter.INSTANCE.toEmailAddress(verifyEmailRequest);
        validateEmailUniqueness(emailAddress);

        String key = StringRedisUtilObj.makeOwnerKeyFor(emailAddress.getEmailAddress());
        putRedis(emailAddress,key);

        CertificationCode certificationCode = mailService.sendMail(emailAddress, OWNER_CERTIFICATE_FORM_LOCATION,
                SesMailSender.OWNER_EMAIL_VERIFICATION_SUBJECT);

        slackNotiSender.noticeEmailVerification(OwnerInVerification.of(certificationCode, emailAddress));
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
        OwnerConverter ownerConverter = OwnerConverter.INSTANCE;

        Owner owner = ownerConverter.toNewOwner(ownerRegisterRequest);
        EmailAddress ownerEmailAddress = EmailAddress.from(owner.getEmail());

        validateRegistration(owner, ownerEmailAddress);
        encodePassword(owner);

        createInDBFor(owner);

        OwnerShop ownerShop = ownerConverter.toOwnerShop(owner.getId(), ownerRegisterRequest);
        putRedisForRequestShop(ownerShop);

        slackNotiSender.noticeRegisterComplete(owner);

        removeRedisFrom(ownerEmailAddress);
    }

    private void validateRegistration(Owner owner, EmailAddress ownerEmailAddress) {
        validateEmailUniqueness(ownerEmailAddress);
        validateCompanyRegistrationNumberUniqueness(owner.getCompany_registration_number());
        validateOwnerInRedis(ownerEmailAddress);
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

    private void validateCompanyRegistrationNumberUniqueness(String companyRegistrationNumber) {
        if (ownerMapper.isCompanyRegistrationNumberExist(companyRegistrationNumber)) {
            throw new BaseException(ExceptionInformation.COMPANY_REGISTRATION_NUMBER_DUPLICATE);
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
                    ownerInVerification, STORAGE_TIME, TimeUnit.HOURS);
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
