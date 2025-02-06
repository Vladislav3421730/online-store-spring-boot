package com.example.onlinestorespringboot.service.Impl;

import com.example.onlinestorespringboot.i18n.I18nUtil;
import com.example.onlinestorespringboot.model.User;
import com.example.onlinestorespringboot.repository.UserRepository;
import com.example.onlinestorespringboot.util.Messages;
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
    I18nUtil i18nUtil;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(i18nUtil.getMessage(Messages.USER_ERROR_EMAIL_NOT_FOUND, email)));
        return new UserDetailsWrapper(user);
    }

}
