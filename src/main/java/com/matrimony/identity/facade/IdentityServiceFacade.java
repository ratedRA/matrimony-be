package com.matrimony.identity.facade;

import com.matrimony.identity.data.MatrimonyUser;
import com.matrimony.identity.data.UserRegistrationRequest;

public interface IdentityServiceFacade {

    void register(UserRegistrationRequest registrationRequest);

    MatrimonyUser login();
}
