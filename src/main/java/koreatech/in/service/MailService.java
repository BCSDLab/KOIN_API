package koreatech.in.service;

import koreatech.in.domain.User.EmailAddress;
import koreatech.in.domain.User.owner.CertificationCode;
import koreatech.in.dto.Mail;
import koreatech.in.util.RandomGenerator;
import koreatech.in.util.SesMailSender;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class MailService {

    private static final String CERTIFICATION_CODE = "certification-code";

    @Autowired
    private VelocityEngine velocityEngine;
    @Autowired
    private SesMailSender sesMailSender;

    public CertificationCode sendMailWithTimes(EmailAddress emailAddress, String formLocation, String purpose) {
        CertificationCode certificationCode = RandomGenerator.getCertificationCode();
        LocalDateTime now = LocalDateTime.now();
        String mailFormWithTimes = mailFormWithTimes(certificationCode,now,emailAddress,formLocation);
        sendMailFor(emailAddress, mailFormWithTimes, purpose);
        return certificationCode;
    }

    public CertificationCode sendMail(EmailAddress emailAddress, String formLocation, String purpose) {
        CertificationCode certificationCode = RandomGenerator.getCertificationCode();
        String mailForm = mailFormFor(certificationCode, formLocation);
        sendMailFor(emailAddress, mailForm, purpose);
        return certificationCode;
    }

    private String mailFormWithTimes(CertificationCode certificationCode, LocalDateTime time,
                                        EmailAddress emailAddress, String formLocation) {
        Map<String, Object> model = Mail.builder()
                .certificationCode(certificationCode.getValue())
                .emailAddress(emailAddress.getEmailAddress())
                .dateTime(time)
                .build();
        return VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, formLocation,
                StandardCharsets.UTF_8.name(), model);
    }

    private void sendMailFor(EmailAddress emailAddress, String mailForm, String purpose) {

        sesMailSender.sendMail(SesMailSender.COMPANY_NO_REPLY_EMAIL_ADDRESS, emailAddress.getEmailAddress(),
                purpose, mailForm);
    }

    private String mailFormFor(CertificationCode certificationCode, String formLocation) {

        Map<String, Object> model = Mail.builder()
                        .certificationCode(certificationCode.getValue())
                        .build();

        return VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, formLocation,
                StandardCharsets.UTF_8.name(), model);
    }
}
