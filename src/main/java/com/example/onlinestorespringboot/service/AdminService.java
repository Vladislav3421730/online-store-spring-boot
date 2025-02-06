package com.example.onlinestorespringboot.service;

import com.example.onlinestorespringboot.dto.UserDto;

public interface AdminService {

    void bun(UserDto userDto);

    void madeManager(UserDto userDto);

    void deleteById(Long id);

}
