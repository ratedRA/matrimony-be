package com.matrimony.identity.facade.impl;

import com.matrimony.common.exceptionhandling.customexceptions.DuplicateUserException;
import com.matrimony.identity.model.MatrimonyUser;
import com.matrimony.identity.data.UserRegistrationRequest;
import com.matrimony.identity.facade.IdentityServiceFacade;
import com.matrimony.identity.repository.mongo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Random;

@Component
public class IdentityServiceFacadeImpl implements IdentityServiceFacade {
    private static final String INDIA_COUNTRY_CODE = "+91";

    @Autowired
    private UserRepository userRepository;

    @Override
    public void register(UserRegistrationRequest registrationRequest) {
        Assert.notNull(registrationRequest, "registration request is required");

        String phoneNumber = registrationRequest.getPhoneNumber();
        String countryCode = registrationRequest.getCountryCode();

        if(StringUtils.isEmpty(countryCode)){
            countryCode = INDIA_COUNTRY_CODE;
        }

        Assert.isTrue(phoneNumber != null && countryCode != null, "phone and country code is required");
        try {
            MatrimonyUser savedUser = userRepository.save(new MatrimonyUser(phoneNumber, INDIA_COUNTRY_CODE));
        } catch (Exception ex){
            if(ex instanceof DuplicateKeyException){
                throw new DuplicateUserException("user is already there with provide phNo");
            }
        }

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
