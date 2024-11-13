package com.logant.BookingSystem.Security.config.user;



import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.logant.BookingSystem.Repository.UserRepository;

import static org.hibernate.query.sqm.tree.SqmNode.log;

@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private final UserRepository userInfoRepo;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Trying to load user by email: { "+username +"}");
        UserDetails res = userInfoRepo
                .findByEmailId(username)
                .map(MyUserDetails::new)
                .orElseThrow(()-> new UsernameNotFoundException("UserEmail: "+ username +" does not exist"));
        return res;
    }
}
