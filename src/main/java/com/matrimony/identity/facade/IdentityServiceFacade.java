package com.matrimony.identity.facade;

import com.matrimony.identity.data.LoginRequest;
import com.matrimony.identity.data.UserFilter;
import com.matrimony.identity.model.MatrimonyUser;
import com.matrimony.identity.data.UserRegistrationRequest;

import java.util.List;

public interface IdentityServiceFacade {

    MatrimonyUser register(UserRegistrationRequest registrationRequest);

    String verifyOtp(String userId, String otp, String password);

    MatrimonyUser login(LoginRequest loginRequest);

    MatrimonyUser getAuthenticatedUser();

    MatrimonyUser update(MatrimonyUser matrimonyUser);

    List<MatrimonyUser> search(UserFilter userFilter);

    MatrimonyUser loadUserById(String userId);
}
