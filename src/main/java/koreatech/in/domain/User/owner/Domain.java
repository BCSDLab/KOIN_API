package koreatech.in.domain.User.owner;

import java.util.Hashtable;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import koreatech.in.exception.BaseException;
import koreatech.in.exception.ExceptionInformation;
import lombok.Getter;

@Getter
public class Domain {

    private final String value;

    private Domain(String value) {
        this.value = value;
    }

    private static void validates(String domainAddress) {
        if(!isExist(domainAddress)) {
            throw new BaseException(ExceptionInformation.EMAIL_DOMAIN_INVALID);
        }
    }

    private static String domainFrom(String fullAddress) {
        return fullAddress.substring(EmailAddress.getSeparateIndex(fullAddress) + EmailAddress.domainSeparator.length());
    }

    public static Domain from(String fullAddress) {

        EmailAddress.validates(fullAddress);
        validates(domainFrom(fullAddress));

        return new Domain(domainFrom(fullAddress));
    }

    private static boolean isExist(String domain) {
        try {
            Hashtable<String, String> environmentSetting = new Hashtable<>();

            environmentSetting.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
            //DNS Lookup By MX
            //https://www.rgagnon.com/javadetails/java-0452.html
            Attribute attribute = ((DirContext) new InitialDirContext(environmentSetting)).getAttributes(domain,
                    new String[]{"MX"}).get("MX");

            return attribute != null;

        } catch (NamingException exception) {
            throw new BaseException(ExceptionInformation.EMAIL_DOMAIN_INVALID);
        }
    }
}