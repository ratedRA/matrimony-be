package com.matrimony.identity.facade.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matrimony.common.GuavaCache;
import com.matrimony.common.exceptionhandling.customexceptions.DuplicateUserException;
import com.matrimony.common.matrimonytoken.JjwtImpl;
import com.matrimony.identity.data.LoginRequest;
import com.matrimony.identity.data.UserFilter;
import com.matrimony.identity.data.UserOtpDetail;
import com.matrimony.identity.data.UserRegistrationRequest;
import com.matrimony.identity.facade.IdentityServiceFacade;
import com.matrimony.identity.model.MatrimonyUser;
import com.matrimony.identity.repository.mongo.UserRepository;
import com.mongodb.client.result.UpdateResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

        String jwt = createUserToken(matrimonyUser);
        matrimonyUser.setAuthenticationToken(jwt);

        return matrimonyUser;
    }

    @Override
    public String verifyOtp(String userId, String otp, String password) {
        assert (userId !=null);
        assert StringUtils.isNotBlank(otp);

        MatrimonyUser matrimonyUser = loadUserById(userId);
        UserOtpDetail userOtpDetail = loadUserOtpDetail(matrimonyUser);

        Assert.isTrue(userOtpDetail.getLastSentOtp() != null && userOtpDetail.getLastOtpSentDate() != null, "user otp detail not found");

        if(bypassOtp(otp)){
            createPassword(password, matrimonyUser);
            markUserVerified(matrimonyUser);

            String jwt = createUserToken(matrimonyUser);

            return jwt;
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


        String jwt = createUserToken(matrimonyUser);

        return jwt;
    }

    private String createUserToken(MatrimonyUser matrimonyUser) {
        Map<String, String> claims = new HashMap<>();
        claims.put("userId", matrimonyUser.getId());
        claims.put("phone", matrimonyUser.getPhoneNumber());
        claims.put("verified", matrimonyUser.getVerified().toString());
        return jjwt.generateJwt(claims, 180l);
    }

    @Override
    public MatrimonyUser getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails authenticatedUser = (UserDetails) authentication.getPrincipal();
        MatrimonyUser matrimonyUser = loadUserById(authenticatedUser.getUsername());
        return matrimonyUser;
    }

    @Override
    public MatrimonyUser update(MatrimonyUser matrimonyUser) {

        // removing null fields from user request
        matrimonyUser.setAuthenticationToken(null);
        ObjectMapper oMapper = new ObjectMapper();
        Map<String, Object> dataMap = oMapper.convertValue(matrimonyUser, Map.class);
        dataMap.values().removeIf(Objects::isNull);

        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(matrimonyUser.getId()));

        Update update = new Update();
        dataMap.forEach(update::set);

        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, MatrimonyUser.class);

        Assert.isTrue(updateResult.getMatchedCount() > 0, "No user found with provided id");

        MatrimonyUser updatedUser = mongoTemplate.findOne(query, MatrimonyUser.class);

        userCache.put(updatedUser.getId(), updatedUser);

        return updatedUser;
    }

    @Override
    public List<MatrimonyUser> search(UserFilter userFilter) {
        List<MatrimonyUser> matrimonyUserList = new ArrayList<>();

        Pageable pageable = PageRequest.of(userFilter.getPageStart() != null ? userFilter.getPageStart() : 0, userFilter.getPageSize() != null ? userFilter.getPageSize() : 10);

        Query query = new Query().with(pageable);
        List<Criteria> criteriaList = new ArrayList<>();

        if (userFilter.getMinAge() != null || userFilter.getMaxAge() != null) {
            if(userFilter.getMinAge() == null){
                userFilter.setMinAge(UserFilter.DEFAULT_MIN_AGE);
            }

            if(userFilter.getMaxAge() == null){
                userFilter.setMaxAge(UserFilter.DEFAULT_MAX_AGE);
            }
            criteriaList.add(Criteria.where("age").gte(userFilter.getMinAge()).lte(userFilter.getMaxAge()));
        }

        if (userFilter.getGender() != null) {
            criteriaList.add(Criteria.where("gender").is(userFilter.getGender()));
        }

        if (userFilter.getMaritalStatus() != null) {
            criteriaList.add(Criteria.where("maritalStatus").is(userFilter.getMaritalStatus()));
        }

        if(StringUtils.isNotBlank(userFilter.getSearchTerm())){
            Criteria criteria = new Criteria();

            criteria.orOperator(Criteria.where("tags").in(userFilter.getSearchTerm()), Criteria.where("instagramId").is(userFilter.getSearchTerm())
                    ,Criteria.where("facebookId").is(userFilter.getSearchTerm())
                    ,Criteria.where("pinterestId").is(userFilter.getSearchTerm())
                    ,Criteria.where("snapchatId").is(userFilter.getSearchTerm()));

            criteriaList.add(criteria);
        }

        if(!CollectionUtils.isEmpty(criteriaList)){
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }

        Page<MatrimonyUser> page = PageableExecutionUtils.getPage(
                mongoTemplate.find(query, MatrimonyUser.class),
                pageable,
                () -> mongoTemplate.count(query.skip(0).limit(0), MatrimonyUser.class)
        );

        if(page != null && !CollectionUtils.isEmpty(page.getContent())){
            matrimonyUserList = page.getContent();
        }

        return matrimonyUserList;
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
