package com.matrimony.common.springsecurity.userservice;

import com.matrimony.identity.model.MatrimonyUser;
import com.matrimony.identity.repository.mongo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class MatrimonyUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String phoneNo) throws UsernameNotFoundException {
        MatrimonyUser matrimonyUser = userRepository.findByPhone(phoneNo).get(0);
        User securityUser = new User(matrimonyUser.getUserId(), matrimonyUser.getPassword(), new ArrayList<>());
        return securityUser;
    }
}
