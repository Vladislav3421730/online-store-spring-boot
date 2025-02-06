package com.example.onlinestorespringboot.controller;

import com.example.onlinestorespringboot.dto.ResponseDto;
import com.example.onlinestorespringboot.dto.UserDto;
import com.example.onlinestorespringboot.service.AdminService;
import com.example.onlinestorespringboot.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/user")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserController {

    UserService userService;
    AdminService adminService;

    @GetMapping
    public ResponseEntity<UserDto> getUser() {
        UserDto userDto = userService.getUser();
        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/all")
    public ResponseEntity<Page<UserDto>> findAllUsers(
            @RequestParam(value = "offset", required = false) Integer offset,
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @RequestParam(value = "sortBy", required = false) String sortBy) {
        if (offset == null) offset = 0;
        if (pageSize == null) pageSize = 10;
        if (sortBy == null || sortBy.isEmpty()) sortBy = "id";
        Page<UserDto> users = userService.findAll(PageRequest.of(offset, pageSize, Sort.by(sortBy)));
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> findUserById(@PathVariable Long id) {
        UserDto user = userService.findById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/email")
    public ResponseEntity<UserDto> findUserByEmail(@RequestParam("email") String email) {
        log.info("Trying to find user with email {}", email);
        UserDto user = userService.findByEmail(email);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/bun")
    public ResponseEntity<ResponseDto> bunUser(@RequestBody @Valid UserDto userDto) {
        adminService.bun(userDto);
        return ResponseEntity.ok(new ResponseDto("User was updated successfully"));
    }

    @PutMapping("/manager")
    public ResponseEntity<ResponseDto> madeManager(@RequestBody @Valid UserDto userDto) {
        adminService.madeManager(userDto);
        return ResponseEntity.ok(new ResponseDto("User was updated successfully"));

    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseDto> deleteUser(@PathVariable Long id) {
        adminService.deleteById(id);
        return ResponseEntity.ok(new ResponseDto("User was deleted successfully"));
    }


}
