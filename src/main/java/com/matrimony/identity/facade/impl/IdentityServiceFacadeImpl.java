package com.matrimony.identity.facade.impl;

import com.matrimony.identity.data.MatrimonyUser;
import com.matrimony.identity.data.UserRegistrationRequest;
import com.matrimony.identity.facade.IdentityServiceFacade;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Random;

public class IdentityServiceFacadeImpl implements IdentityServiceFacade {
    private static final String INDIA_COUNTRY_CODE = "+91";

    @Override
    public void register(UserRegistrationRequest registrationRequest) {
        Assert.notNull(registrationRequest, "registration request is required");

        String phoneNumber = registrationRequest.getPhoneNumber();
        String countryCode = registrationRequest.getCountryCode();

        if(StringUtils.isEmpty(countryCode)){
            countryCode = INDIA_COUNTRY_CODE;
        }

        Assert.isTrue(phoneNumber != null && countryCode != null, "phone and country code is required");
        // save user
        
        String otp = generateOtp();
        // save the otp to db & cache.
        // send the otp to phoneNumber.
    }

    @Override
    public MatrimonyUser login() {
        return null;
    }

    private String generateOtp(){
        Random random = new Random();
        String randomOtp = String.format("%04d", random.nextInt(10000));

        return randomOtp;
    }
}
