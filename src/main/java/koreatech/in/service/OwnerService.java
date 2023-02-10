package koreatech.in.service;

import koreatech.in.dto.normal.user.owner.request.OwnerRegisterRequest;
import koreatech.in.dto.normal.user.owner.request.VerifyCodeRequest;
import koreatech.in.dto.normal.user.owner.request.VerifyEmailRequest;

public interface OwnerService {

    void requestVerification(VerifyEmailRequest verifyEmailRequest);

    void certificate(VerifyCodeRequest verifyCodeRequest);

    void register(OwnerRegisterRequest ownerRegisterRequest);
}
