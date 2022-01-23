package com.matrimony.identity.facade;

import com.matrimony.identity.data.LoginRequest;
import com.matrimony.identity.model.MatrimonyUser;
import com.matrimony.identity.data.UserRegistrationRequest;

public interface IdentityServiceFacade {

    MatrimonyUser register(UserRegistrationRequest registrationRequest);

    void verifyOtp(String userId, String otp, String password);

    MatrimonyUser login(LoginRequest loginRequest);
}
