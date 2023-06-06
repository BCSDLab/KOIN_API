package koreatech.in.domain.User.owner;

import java.util.List;

public class OwnerPartition extends Owner {
    @Override
    public void setCompany_registration_number(String company_registration_number) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAttachments(List<OwnerAttachment> attachments) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasRegistrationInformation() {
        return false;
    }
}
