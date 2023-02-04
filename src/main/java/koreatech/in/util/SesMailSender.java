package koreatech.in.util;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceAsync;
import com.amazonaws.services.simpleemail.model.*;
import org.springframework.beans.factory.annotation.Autowired;

public class SesMailSender {

    public static final String COMPANY_NO_REPLY_EMAIL_ADDRESS = "no-reply@bcsdlab.com";
    public static final String OWNER_EMAIL_VERIFICATION_SUBJECT = "코인 사장님 회원가입 이메일 인증";

    @Autowired
    private AmazonSimpleEmailServiceAsync amazonSimpleEmailServiceAsync;

    public void sendMail(String FROM, String TO, String SUBJECT, String HTMLBODY) {
//        FROM = "sender@example.com";
//        TO = "recipient@example.com";
//        SUBJECT = "Amazon SES test (AWS SDK for Java)";
//        HTMLBODY = "<h1>Amazon SES test (AWS SDK for Java)</h1>"
//                + "<p>This email was sent with <a href='https://aws.amazon.com/ses/'>"
//                + "Amazon SES</a> using the <a href='https://aws.amazon.com/sdk-for-java/'>"
//                + "AWS SDK for Java</a>";

        SendEmailRequest request = new SendEmailRequest()
                .withDestination(
                        new Destination().withToAddresses(TO) // 받는 사람
                )
                .withMessage(new Message()
                        .withBody(new Body()
                                .withHtml(new Content()
                                        .withCharset("UTF-8").withData(HTMLBODY)) // HTML 양식의 본문
                        )
                        .withSubject(new Content()
                                .withCharset("UTF-8").withData(SUBJECT)) // 제목
                )
                .withSource(FROM);  // Verify된 Email
        if (amazonSimpleEmailServiceAsync == null)
            System.out.println("is null");
        amazonSimpleEmailServiceAsync.sendEmailAsync(request);
    }
}
