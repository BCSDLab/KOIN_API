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
        String mailFormWithTimes = mailFormForWithTimes(certificationCode,now,emailAddress,formLocation);
        sendMailFor(emailAddress, certificationCode, mailFormWithTimes, purpose);
        return certificationCode;
    }

    private String mailFormForWithTimes(CertificationCode certificationCode, LocalDateTime time,
                                        EmailAddress emailAddress, String formLocation) {
        Map<String, Object> model = Mail.builder()
                .certificationCode(certificationCode.getValue())
                .emailAddress(emailAddress.getEmailAddress())
                .dateTime(time)
                .build();
        return VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, formLocation,
                StandardCharsets.UTF_8.name(), model);
    }

    private void sendMailFor(EmailAddress emailAddress, CertificationCode certificationCode,
                             String mailForm, String purpose) {

        sesMailSender.sendMail(SesMailSender.COMPANY_NO_REPLY_EMAIL_ADDRESS, emailAddress.getEmailAddress(),
                purpose, mailForm);
    }
/*
    private String mailFormFor(CertificationCode certificationCode) {

        Map<String, Object> model = new HashMap<>();
        model.put(CERTIFICATION_CODE, certificationCode.getValue());

        return VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, OWNER_CERTIFICATE_FORM_LOCATION,
                StandardCharsets.UTF_8.name(), model);
    }

 */
}
