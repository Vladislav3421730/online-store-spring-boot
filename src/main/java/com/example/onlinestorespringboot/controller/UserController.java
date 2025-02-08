package com.example.onlinestorespringboot.controller;

import com.example.onlinestorespringboot.dto.AppErrorDto;
import com.example.onlinestorespringboot.dto.ResponseDto;
import com.example.onlinestorespringboot.dto.UserDto;
import com.example.onlinestorespringboot.i18n.I18nUtil;
import com.example.onlinestorespringboot.service.AdminService;
import com.example.onlinestorespringboot.service.UserService;
import com.example.onlinestorespringboot.util.Messages;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "User", description = "Endpoints for managing users")
@ApiResponses({
        @ApiResponse(
                responseCode = "401",
                description = "Unauthorized",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppErrorDto.class))
        ),
        @ApiResponse(
                responseCode = "403",
                description = "Forbidden",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppErrorDto.class))
        )
})
public class UserController {

    UserService userService;
    AdminService adminService;
    I18nUtil i18nUtil;

    @GetMapping
    @Operation(summary = "Get current user", description = "Retrieves details of the currently authenticated user.")
    @ApiResponse(
            responseCode = "200",
            description = "User was found successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))
    )
    public ResponseEntity<UserDto> getUser() {
        UserDto userDto = userService.getUser();
        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/all")
    @Operation(summary = "Find all users", description = "Retrieves a paginated list of all users.")
    @ApiResponse(
            responseCode = "200",
            description = "Find all users (pagination included)",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = UserDto.class)))
    )
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
    @Operation(summary = "Find user by ID", description = "Retrieves a user by their unique identifier.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User was found successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid id",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppErrorDto.class))
            )
    })
    public ResponseEntity<UserDto> findUserById(@PathVariable Long id) {
        UserDto user = userService.findById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/email")
    @Operation(summary = "Find user by email", description = "Retrieves a user by their email address.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User was found successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid email",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppErrorDto.class))
            )
    })
    public ResponseEntity<UserDto> findUserByEmail(@RequestParam("email") String email) {
        log.info("Trying to find user with email {}", email);
        UserDto user = userService.findByEmail(email);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/bun")
    @Operation(summary = "Ban or unban user", description = "Bans or unbans a user based on the provided user details.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User was buned/unbaned successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Can't bun/unbun yourself",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppErrorDto.class))
            )
    })
    public ResponseEntity<ResponseDto> bunUser(@RequestBody @Valid UserDto userDto) {
        adminService.bun(userDto);
        return ResponseEntity.ok(new ResponseDto(i18nUtil.getMessage(Messages.USER_SUCCESS_UPDATED)));
    }

    @PutMapping("/manager")
    @Operation(summary = "Assign or remove manager role", description = "Adds or removes the manager role from a user.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Role manager was added/removed successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Can't add/remove role manager from yourself",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppErrorDto.class))
            )
    })
    public ResponseEntity<ResponseDto> madeManager(@RequestBody @Valid UserDto userDto) {
        adminService.madeManager(userDto);
        return ResponseEntity.ok(new ResponseDto(i18nUtil.getMessage(Messages.USER_SUCCESS_UPDATED)));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Delete user by ID", description = "Deletes a user by their unique identifier.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User was deleted successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid id",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppErrorDto.class))
            )
    })
    public ResponseEntity<ResponseDto> deleteUser(@PathVariable Long id) {
        adminService.deleteById(id);
        return ResponseEntity.ok(new ResponseDto(i18nUtil.getMessage(Messages.USER_SUCCESS_DELETED, String.valueOf(id))));
    }
}

