package com.eureka.ip.team1.urjung_main.auth.config;

import java.util.Collections;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.eureka.ip.team1.urjung_main.user.entity.User;
import com.eureka.ip.team1.urjung_main.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        Optional<User> optionalUser = userRepository.findByEmail(username);
        
        if( optionalUser.isPresent() ) {
            
            User user = optionalUser.get();
 
            UserDetails userDetails = CustomUserDetails.builder()
            		.user(user)
            		.build();
            
            return userDetails;
        }
        
        throw new UsernameNotFoundException("User not found");
    }

}
