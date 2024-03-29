package koreatech.in.service;

import koreatech.in.domain.Mail.MailForm;
import koreatech.in.domain.User.EmailAddress;
import koreatech.in.domain.User.owner.CertificationCode;
import koreatech.in.dto.normal.mail.Mail;
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

    @Autowired
    private VelocityEngine velocityEngine;
    @Autowired
    private SesMailSender sesMailSender;

    public CertificationCode sendMailWithTimes(EmailAddress emailAddress, MailForm form) {
        CertificationCode certificationCode = RandomGenerator.getCertificationCode();
        LocalDateTime now = LocalDateTime.now();
        String mailFormWithTimes = mailFormWithTimes(certificationCode,now,emailAddress, form.getPath());
        sendMailFor(emailAddress, mailFormWithTimes, form.getSubject());
        return certificationCode;
    }

    public CertificationCode sendMail(EmailAddress emailAddress, MailForm form) {
        CertificationCode certificationCode = RandomGenerator.getCertificationCode();
        String mailForm = mailFormFor(certificationCode, form.getPath());
        sendMailFor(emailAddress, mailForm, form.getSubject());
        return certificationCode;
    }

    private String mailFormWithTimes(CertificationCode certificationCode, LocalDateTime time,
                                        EmailAddress emailAddress, String formLocation) {
        Mail mail = Mail.builder()
                .certificationCode(certificationCode.getValue())
                .emailAddress(emailAddress.getEmailAddress())
                .time(time)
                .build();

        Map<String, Object> model = mail.convertToMapWithTimes();

        return VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, formLocation,
                StandardCharsets.UTF_8.name(), model);
    }

    private void sendMailFor(EmailAddress emailAddress, String mailForm, String purpose) {

        sesMailSender.sendMail(SesMailSender.COMPANY_NO_REPLY_EMAIL_ADDRESS, emailAddress.getEmailAddress(),
                purpose, mailForm);
    }

    private String mailFormFor(CertificationCode certificationCode, String formLocation) {

        Mail mail = Mail.builder()
                        .certificationCode(certificationCode.getValue())
                        .build();

        Map<String, Object> model = mail.convertToMap();

        return VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, formLocation,
                StandardCharsets.UTF_8.name(), model);
    }
}
