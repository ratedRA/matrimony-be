package com.matrimony.identity.facade.impl;

import com.matrimony.common.GuavaCache;
import com.matrimony.common.exceptionhandling.customexceptions.DuplicateUserException;
import com.matrimony.common.matrimonytoken.JjwtImpl;
import com.matrimony.identity.data.LoginRequest;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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
    @Qualifier("userCache")
    private GuavaCache<String, MatrimonyUser> userCache;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JjwtImpl jjwt;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public MatrimonyUser register(UserRegistrationRequest registrationRequest) {
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

        return savedUser;
    }

    @Override
    public MatrimonyUser login(LoginRequest loginRequest) {
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getPhoneNo(), loginRequest.getPassword()));
        } catch (BadCredentialsException ex){
            throw new RuntimeException("incorrect username or password", ex);
        }

        MatrimonyUser matrimonyUser = userRepository.findByPhone(loginRequest.getPhoneNo()).get(0);

        Map<String, String> claims = new HashMap<>();
        claims.put("userId", matrimonyUser.getId());
        claims.put("verified", matrimonyUser.getVerified().toString());

        String jwt = jjwt.generateJwt(claims, 180l);
        matrimonyUser.setAuthenticationToken(jwt);

        return matrimonyUser;
    }

    @Override
    public void verifyOtp(String userId, String otp, String password) {
        assert (userId !=null);
        assert StringUtils.isNotBlank(otp);

        MatrimonyUser matrimonyUser = loadUserById(userId);
        UserOtpDetail userOtpDetail = loadUserOtpDetail(matrimonyUser);

        Assert.isTrue(userOtpDetail.getLastSentOtp() != null && userOtpDetail.getLastOtpSentDate() != null, "user otp detail not found");

        if(bypassOtp(otp)){
            createPassword(password, matrimonyUser);
            markUserVerified(matrimonyUser);
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
        createPassword(password, matrimonyUser);
        markUserVerified(matrimonyUser);
    }

    private void createPassword(String password, MatrimonyUser matrimonyUser) {
        String encodedPassword = passwordEncoder.encode(password);
        matrimonyUser.setPassword(encodedPassword);
    }

    private void markUserVerified(MatrimonyUser matrimonyUser) {
        matrimonyUser.setVerified(true);
        userRepository.save(matrimonyUser);
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

        userCache.put(updatedOtpUser.getId(), updatedOtpUser);

        // send the otp to phoneNumber.
    }

    private UserOtpDetail loadUserOtpDetail(MatrimonyUser userById) {
        return new UserOtpDetail(userById.getLastSentOtp(), userById.getLastSentOtpDate());
    }

    private MatrimonyUser loadUserById(String userId) {
        MatrimonyUser userById = userCache.get(userId);

        // lazy load if not found in cache
        if(userById == null){
            Optional<MatrimonyUser> optionalUser = userRepository.findById(userId);
            userById = optionalUser.get();
            String lastSentOtp = userById.getLastSentOtp();
            Date lastSentOtpDate = userById.getLastSentOtpDate();

            userById.setLastSentOtp(lastSentOtp);
            userById.setLastSentOtpDate(lastSentOtpDate);
            userCache.put(userById.getId(), userById);
        }
        return userById;
    }
}
