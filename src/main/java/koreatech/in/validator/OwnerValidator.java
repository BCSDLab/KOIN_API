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
        companyNumber = constraintAnnotation.companyNumber();
        attachmentUrls = constraintAnnotation.attachmentUrls();
    }

    @Override
    public boolean isValid(OwnerRegisterRequest o, ConstraintValidatorContext constraintValidatorContext) {
        try {
            String property = BeanUtils.getProperty(o, companyNumber);
            String property1 = BeanUtils.getProperty(o, attachmentUrls);

            return !(property == null ^ property1 == null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
