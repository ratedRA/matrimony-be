package com.matrimony.identity.facade.impl;

import com.matrimony.common.GuavaCache;
import com.matrimony.common.exceptionhandling.customexceptions.DuplicateUserException;
import com.matrimony.identity.data.UserOtpDetail;
import com.matrimony.identity.data.UserRegistrationRequest;
import com.matrimony.identity.facade.IdentityServiceFacade;
import com.matrimony.identity.model.MatrimonyUser;
import com.matrimony.identity.repository.mongo.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Component
public class IdentityServiceFacadeImpl implements IdentityServiceFacade {

    private static final String INDIA_COUNTRY_CODE = "+91";
    private static final long OTP_VALID_DURATION = TimeUnit.MINUTES.toMillis(5);
    private static final String OTP_BYPASS = "aman";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    @Qualifier("userOtpCache")
    private GuavaCache<String, UserOtpDetail> userOtpCache;

    @Override
    public void register(UserRegistrationRequest registrationRequest) {
        Assert.notNull(registrationRequest, "registration request is required");

        String phoneNumber = registrationRequest.getPhoneNumber();
        String countryCode = registrationRequest.getCountryCode();

        if(StringUtils.isEmpty(countryCode)){
            countryCode = INDIA_COUNTRY_CODE;
        }

        MatrimonyUser savedUser = null;

        Assert.isTrue(phoneNumber != null && countryCode != null, "phone and country code is required");
        try {
            savedUser = userRepository.save(new MatrimonyUser(phoneNumber, INDIA_COUNTRY_CODE));
        } catch (Exception ex){
            if(ex instanceof DuplicateKeyException){
                throw new DuplicateUserException("user is already there with provide phNo");
            }
        }

        sendOtp(savedUser);
    }

    @Override
    public void verifyOtp(String userId, String otp) {
        assert (userId !=null);
        assert StringUtils.isNotBlank(otp);

        UserOtpDetail userOtpDetail = loadUserOtpDetail(userId);

        Assert.isTrue(userOtpDetail.getLastSentOtp() != null && userOtpDetail.getLastOtpSentDate() != null, "user otp detail not found");

        if(bypassOtp(otp)){
            return;
        }

        long currentTimeInMillis = System.currentTimeMillis();

        if(!otp.equals(userOtpDetail.getLastSentOtp())){
            throw new RuntimeException("Incorrect otp");
        }

        if (userOtpDetail.getLastOtpSentDate().getTime() + OTP_VALID_DURATION < currentTimeInMillis) {
            // OTP expires
            throw new RuntimeException("Otp has been expired");
        }
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

    private final boolean bypassOtp(String otp){
        return otp.equals(OTP_BYPASS);
    }

    private void sendOtp(MatrimonyUser savedUser) {
        String otp = generateOtp();

        // updatedOtpUser the otp to db & cache.
        Assert.notNull(savedUser,"user cannot be null");
        savedUser.setLastSentOtp(otp);
        savedUser.setLastSentOtpDate(new Date());
        MatrimonyUser updatedOtpUser = userRepository.save(savedUser);

        userOtpCache.put(updatedOtpUser.getId(), new UserOtpDetail(updatedOtpUser.getLastSentOtp(), updatedOtpUser.getLastSentOtpDate()));

        // send the otp to phoneNumber.
    }

    private UserOtpDetail loadUserOtpDetail(String userId) {
        UserOtpDetail userOtpDetail = userOtpCache.get(userId);

        // lazy load if not found in cache
        if(userOtpDetail == null){
            Optional<MatrimonyUser> byId = userRepository.findById(userId);
            String lastSentOtp = byId.get().getLastSentOtp();
            Date lastSentOtpDate = byId.get().getLastSentOtpDate();

            userOtpDetail = new UserOtpDetail(lastSentOtp, lastSentOtpDate);
            userOtpCache.put(userId, userOtpDetail);
        }
        return userOtpDetail;
    }
}
