package koreatech.in.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import koreatech.in.domain.User.owner.CertificationCode;
import koreatech.in.domain.User.owner.EmailAddress;
import koreatech.in.domain.User.owner.OwnerInVerification;
import koreatech.in.dto.normal.owner.request.VerifyCodeRequest;
import koreatech.in.dto.normal.owner.request.VerifyEmailRequest;
import koreatech.in.mapstruct.OwnerConverter;
import koreatech.in.util.RandomGenerator;
import koreatech.in.util.SesMailSender;
import koreatech.in.util.StringRedisUtilStr;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    }

    private void putRedisFor(String emailAddress, OwnerInVerification ownerInVerification) {
        Gson gson = new GsonBuilder().create();

        stringRedisUtilStr.valOps.set(redisOwnerAuthPrefix + emailAddress.getEmailAddress(),
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
