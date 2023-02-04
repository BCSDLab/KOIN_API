package koreatech.in.service;

import koreatech.in.dto.normal.owner.request.VerifyCodeRequest;
import koreatech.in.dto.normal.owner.request.VerifyEmailRequest;

public interface OwnerService {

    void requestVerification(VerifyEmailRequest verifyEmailRequest);

    void certificate(VerifyCodeRequest verifyCodeRequest);
}
