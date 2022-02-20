package com.matrimony.common.springsecurity.userservice;

import com.matrimony.identity.model.MatrimonyUser;
import com.matrimony.identity.repository.mongo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

@Service
public class MatrimonyUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String phoneNo) throws UsernameNotFoundException {
        List<MatrimonyUser> byPhone = userRepository.findByPhone(phoneNo);
        Assert.notEmpty(byPhone, "no user found with given phone");
        User securityUser = new User(byPhone.get(0).getUserId(), byPhone.get(0).getPassword(), new ArrayList<>());
        return securityUser;
    }
}
