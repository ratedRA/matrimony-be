package com.matrimony.identity.facade;

import com.matrimony.identity.model.MatrimonyUser;
import com.matrimony.identity.data.UserRegistrationRequest;

public interface IdentityServiceFacade {

    void register(UserRegistrationRequest registrationRequest);

    void verifyOtp(String userId, String otp);

    MatrimonyUser login();
}
