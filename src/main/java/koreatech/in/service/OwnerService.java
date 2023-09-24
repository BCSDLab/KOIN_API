package koreatech.in.service;

import koreatech.in.dto.normal.user.owner.request.OwnerFindPasswordRequest;
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

    OwnerResponse getOwner();

    void deleteAttachment(Integer attachmentId);

    OwnerResponse update(OwnerUpdateRequest ownerUpdateRequest);

    void inputPasswordToChangePassword(OwnerFindPasswordRequest ownerFindPasswordRequest);

    void certificateToChangePassword(VerifyCodeRequest request);

    void requestVerificationToChangePassword(VerifyEmailRequest verifyEmailRequest);
}
