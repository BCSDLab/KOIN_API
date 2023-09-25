package koreatech.in.service;

import java.sql.SQLException;
import koreatech.in.domain.User.EmailAddress;
import koreatech.in.domain.User.User;
import koreatech.in.domain.User.owner.CertificationCode;
import koreatech.in.domain.User.owner.Owner;
import koreatech.in.domain.User.owner.OwnerAttachment;
import koreatech.in.domain.User.owner.OwnerAttachments;
import koreatech.in.domain.User.owner.OwnerInCertification;
import koreatech.in.domain.User.owner.OwnerInVerification;
import koreatech.in.domain.User.owner.OwnerShop;
import koreatech.in.dto.normal.user.owner.request.OwnerFindPasswordRequest;
import koreatech.in.dto.normal.user.owner.request.OwnerRegisterRequest;
import koreatech.in.dto.normal.user.owner.request.OwnerUpdateRequest;
import koreatech.in.dto.normal.user.owner.request.VerifyCodeRequest;
import koreatech.in.dto.normal.user.owner.request.VerifyEmailRequest;
import koreatech.in.dto.normal.user.owner.response.OwnerResponse;
import koreatech.in.dto.normal.user.owner.response.VerifyCodeResponse;
import koreatech.in.exception.BaseException;
import koreatech.in.exception.ExceptionInformation;
import koreatech.in.mapstruct.OwnerConverter;
import koreatech.in.repository.RedisOwnerMapper;
import koreatech.in.repository.user.OwnerMapper;
import koreatech.in.repository.user.UserMapper;
import koreatech.in.util.SesMailSender;
import koreatech.in.util.SlackNotiSender;
import koreatech.in.util.StringRedisUtilObj;
import koreatech.in.util.jwt.TemporaryAccessJwtGenerator;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static koreatech.in.domain.Mail.MailForm.*;

import static koreatech.in.domain.RedisOwnerKeyPrefix.*;

@Service
public class OwnerServiceImpl implements OwnerService {
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
    private RedisOwnerMapper redisOwnerMapper;

    @Autowired
    private MailService mailService;

    @Override
    public void inputPasswordToChangePassword(OwnerFindPasswordRequest ownerFindPasswordRequest) {
        EmailAddress emailAddress = OwnerConverter.INSTANCE.toEmailAddress(ownerFindPasswordRequest);
        redisOwnerMapper.validateOwnerInRedis(emailAddress, ownerChangePasswordAuthPrefix);
    }
    public void certificateToChangePassword(VerifyCodeRequest verifyCodeRequest) {
        OwnerInCertification ownerInCertification = OwnerConverter.INSTANCE.toOwnerInCertification(verifyCodeRequest);
        redisOwnerMapper.changeAuthStatus(ownerInCertification, ownerInCertification.getEmail(), ownerChangePasswordAuthPrefix);
    }

    public void requestVerificationToChangePassword(VerifyEmailRequest verifyEmailRequest) {
        EmailAddress emailAddress = OwnerConverter.INSTANCE.toEmailAddress(verifyEmailRequest);
        validateEmailFromOwner(emailAddress);

        CertificationCode certificationCode = mailService.sendMailWithTimes(emailAddress, OWNER_FIND_PASSWORD_MAIL_FORM);

        OwnerInVerification ownerInVerification = OwnerInVerification.of(certificationCode, emailAddress);

        emailAddress.validateSendable();
        redisOwnerMapper.putRedisFor(ownerChangePasswordAuthPrefix.getKey(emailAddress.getEmailAddress()), ownerInVerification);

        slackNotiSender.noticeEmailVerification(ownerInVerification);
    }

    private void validateEmailFromOwner(EmailAddress emailAddress) {
        User user = userMapper.getUserByEmail(emailAddress.getEmailAddress());
        if (user == null || user.isStudent()) {
            throw new BaseException(ExceptionInformation.NOT_EXIST_EMAIL);
        }
    }

    @Override
    public void requestVerification(VerifyEmailRequest verifyEmailRequest) {
        EmailAddress emailAddress = OwnerConverter.INSTANCE.toEmailAddress(verifyEmailRequest);
        validateEmailUniqueness(emailAddress);

        CertificationCode certificationCode = mailService.sendMail(emailAddress, OWNER_REGISTRATION_MAIL_FORM);

        OwnerInVerification ownerInVerification = OwnerInVerification.of(certificationCode, emailAddress);

        emailAddress.validateSendable();
        redisOwnerMapper.putRedisFor(ownerAuthPrefix.getKey(emailAddress.getEmailAddress()), ownerInVerification);

        slackNotiSender.noticeEmailVerification(ownerInVerification);
    }

    @Override
    public VerifyCodeResponse certificate(VerifyCodeRequest verifyCodeRequest) {
        OwnerInCertification ownerInCertification = OwnerConverter.INSTANCE.toOwnerInCertification(verifyCodeRequest);
        redisOwnerMapper.changeAuthStatus(ownerInCertification, ownerInCertification.getEmail(), ownerAuthPrefix);
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
        redisOwnerMapper.validateOwnerInRedis(ownerEmailAddress, ownerAuthPrefix);

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

    private void removeRedisFrom(EmailAddress emailAddress) {
        stringRedisUtilObj.deleteData(ownerAuthPrefix.getKey(emailAddress.getEmailAddress()));
    }

    private void putRedisForRequestShop(OwnerShop ownerShop) {
        try {
            stringRedisUtilObj.setDataAsString(StringRedisUtilObj.makeOwnerShopKeyFor(ownerShop.getOwner_id()), ownerShop);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
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
