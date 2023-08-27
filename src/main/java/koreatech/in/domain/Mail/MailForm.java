package koreatech.in.domain.Mail;

public enum MailForm {
    OWNER_REGISTRATION_MAIL_FORM("코인 사장님 회원가입 이메일 인증", "mail/owner_certificate_number.vm"),
    OWNER_FIND_PASSWORD_MAIL_FORM("코인 사장님 비밀번호 찾기 이메일 인증", "mail/change_password_certificate_number.vm");

    private final String subject;
    private final String path;

    MailForm(String subject, String path) {
        this.subject = subject;
        this.path = path;
    }

    public String getSubject() {
        return subject;
    }

    public String getPath() {
        return path;
    }
}
