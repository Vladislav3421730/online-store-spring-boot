package com.example.onlinestorespringboot.service.Impl;

import com.example.onlinestorespringboot.model.User;
import com.example.onlinestorespringboot.repository.UserRepository;
import com.example.onlinestorespringboot.wrapper.UserDetailsWrapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserDetailsServiceImpl implements UserDetailsService {

    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User with %s wasn't found", email)));
        return new UserDetailsWrapper(user);
    }
}
