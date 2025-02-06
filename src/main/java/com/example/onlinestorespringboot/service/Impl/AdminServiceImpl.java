package com.example.onlinestorespringboot.service.Impl;


import com.example.onlinestorespringboot.dto.UserDto;
import com.example.onlinestorespringboot.exception.UserNotFoundException;
import com.example.onlinestorespringboot.exception.UserUpdatingException;
import com.example.onlinestorespringboot.mapper.UserMapper;
import com.example.onlinestorespringboot.model.User;
import com.example.onlinestorespringboot.model.enums.Role;
import com.example.onlinestorespringboot.repository.UserRepository;
import com.example.onlinestorespringboot.service.AdminService;
import com.example.onlinestorespringboot.service.UserService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AdminServiceImpl implements AdminService {

    UserRepository userRepository;
    UserService userService;
    UserMapper userMapper;

    @Override
    @Transactional
    public void bun(UserDto userDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = (String) authentication.getPrincipal();
        if (email.equals(userDto.getEmail())) {
            log.error("User try bun himself");
            throw new UserUpdatingException("You can't bun/unbun yourself");
        }
        User user = userMapper.toEntity(userDto);
        log.info("{} {}", user.isBun() ? "ban user" : "unban", user.getEmail());
        user.setBun(!userDto.isBun());
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void madeManager(UserDto userDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = (String) authentication.getPrincipal();
        if (email.equals(userDto.getEmail())) {
            log.error("User try add/remove role manager to himself");
            throw new UserUpdatingException("You can't add/remove role manager from yourself");
        }
        User user = userMapper.toEntity(userDto);
        if (!user.getRoleSet().add(Role.ROLE_MANAGER)) {
            log.info("User {} already has ROLE_MANAGER. Removing the role.", user.getUsername());
            user.getRoleSet().remove(Role.ROLE_MANAGER);
        } else {
            log.info("Adding ROLE_MANAGER to user {}.", user.getUsername());
        }
        userRepository.save(user);
        log.info("User {} has been updated successfully.", user.getUsername());
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        UserDto user = userService.findById(id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = (String) authentication.getPrincipal();
        if (email.equals(user.getEmail())) {
            log.error("User try delete himself");
            throw new UserUpdatingException("You can't delete yourself");
        }
        log.info("Try delete user with id {}", id);
        userRepository.deleteById(id);
        log.info("User was deleted successfully");
    }

}
