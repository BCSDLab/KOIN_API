package koreatech.in.validator;

import koreatech.in.annotation.OwnerRegistrationInfomation;
import koreatech.in.dto.normal.user.owner.request.OwnerRegisterRequest;
import org.apache.commons.beanutils.BeanUtils;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class OwnerValidator implements ConstraintValidator<OwnerRegistrationInfomation, OwnerRegisterRequest> {

    private String companyNumber;
    private String attachmentUrls;

    @Override
    public void initialize(OwnerRegistrationInfomation constraintAnnotation) {
        this.companyNumber = constraintAnnotation.companyNumber();
        this.attachmentUrls = constraintAnnotation.attachmentUrls();
    }

    @Override
    public boolean isValid(OwnerRegisterRequest ownerRegisterRequest, ConstraintValidatorContext constraintValidatorContext) {
        try {
            String companyNumberName = BeanUtils.getProperty(ownerRegisterRequest, companyNumber);
            String attachmentUrlsName = BeanUtils.getProperty(ownerRegisterRequest, attachmentUrls);

            return isValidOptionalRegisrationInfo(companyNumberName, attachmentUrlsName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isValidOptionalRegisrationInfo(String companyNumber, String attachmentUrls) {
        return !(companyNumber == null ^ attachmentUrls == null);
    }
}
