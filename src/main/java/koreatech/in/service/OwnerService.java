package koreatech.in.service;

import koreatech.in.dto.normal.user.owner.request.OwnerRegisterRequest;
import koreatech.in.dto.normal.user.owner.request.OwnerUpdateRequest;
import koreatech.in.dto.normal.user.owner.request.VerifyCodeRequest;
import koreatech.in.dto.normal.user.owner.request.VerifyEmailRequest;
import koreatech.in.dto.normal.user.owner.response.OwnerResponse;
import koreatech.in.dto.normal.user.owner.response.VerifyCodeResponse;

public interface OwnerService {

    void requestVerification(VerifyEmailRequest verifyEmailRequest);

    VerifyCodeResponse certificate(VerifyCodeRequest verifyCodeRequest);

    void register(OwnerRegisterRequest ownerRegisterRequest);

    void registerWithShop(OwnerRegisterRequest request);

    OwnerResponse getOwner();

    void deleteAttachment(Integer attachmentId);

    OwnerResponse update(OwnerUpdateRequest ownerUpdateRequest);

    void requestVerificationToChangePassword(VerifyEmailRequest verifyEmailRequest);

}
