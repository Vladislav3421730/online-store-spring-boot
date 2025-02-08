package com.example.serviceTests;

import com.example.onlinestorespringboot.dto.*;
import com.example.onlinestorespringboot.exception.UserNotFoundException;
import com.example.onlinestorespringboot.i18n.I18nUtil;
import com.example.onlinestorespringboot.mapper.OderItemCartMapper;
import com.example.onlinestorespringboot.mapper.OrderMapper;
import com.example.onlinestorespringboot.mapper.ProductMapper;
import com.example.onlinestorespringboot.mapper.UserMapper;
import com.example.onlinestorespringboot.model.User;
import com.example.onlinestorespringboot.repository.AddressRepository;
import com.example.onlinestorespringboot.repository.UserRepository;
import com.example.onlinestorespringboot.service.Impl.UserServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private I18nUtil i18nUtil;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private OderItemCartMapper oderItemCartMapper;

    @Mock
    private ProductMapper productMapper;
    @Mock
    private OrderMapper orderMapper;

    @Mock
    PasswordEncoder passwordEncoder;


    @InjectMocks
    private UserServiceImpl userService;

    private static RegisterUserDto registerUserDto;
    private static UserDto userDto;
    private static User user;

    @BeforeAll
    static void setup() {
        registerUserDto = new RegisterUserDto();
        registerUserDto.setEmail("user@gmail.com");
        registerUserDto.setPassword("password");
        registerUserDto.setConfirmPassword("password");

        userDto = new UserDto();
        userDto.setEmail(registerUserDto.getEmail());
        userDto.setUsername("user");

        user = new User();
        user.setEmail(registerUserDto.getEmail());
        user.setPassword("encodedPassword");
    }

    @Test
    @Order(1)
    @DisplayName("Test find user by email")
    void testFindByEmail() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userMapper.toDTO(user)).thenReturn(userDto);

        UserDto foundUser = userService.findByEmail(user.getEmail());
        assertNotNull(foundUser);
        assertEquals(user.getEmail(), foundUser.getEmail());

        verify(userRepository).findByEmail(user.getEmail());
        verify(userMapper).toDTO(user);
    }

    @Test
    @Order(2)
    @DisplayName("Test find user by email - user not found")
    void testFindByEmailUserNotFound() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.findByEmail(user.getEmail()));
        verify(userRepository).findByEmail(user.getEmail());
    }

    @Test
    @Order(3)
    @DisplayName("Test save new user")
    void testSaveUser() {
        when(userMapper.toNewEntity(registerUserDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDTO(user)).thenReturn(userDto);

        UserDto savedUserDto = userService.saveUser(registerUserDto);
        assertNotNull(savedUserDto);
        assertEquals(savedUserDto.getEmail(), user.getEmail());

        verify(userMapper).toNewEntity(registerUserDto);
        verify(userRepository).save(user);
        verify(userMapper).toDTO(user);
    }

    @Test
    @Order(4)
    @DisplayName("Test get user by authentication")
    void testGetUser() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(Boolean.TRUE);
        when(authentication.getPrincipal()).thenReturn(user.getEmail());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userMapper.toDTO(user)).thenReturn(userDto);

        UserDto fetchedUser = userService.getUser();

        assertNotNull(fetchedUser);
        assertEquals(user.getEmail(), fetchedUser.getEmail());

        verify(userRepository).findByEmail(user.getEmail());
        verify(userMapper).toDTO(user);
    }

    @Test
    @Order(5)
    @DisplayName("Test get user by authentication with null authentication")
    void testGetUserWithNullAuthentication() {
        SecurityContextHolder.getContext().setAuthentication(null);
        assertThrows(UserNotFoundException.class, () -> userService.getUser());
    }


    @Test
    @Order(6)
    @DisplayName("Test get user by authentication - user not found")
    void testGetUserUserNotFound() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(Boolean.TRUE);
        when(authentication.getPrincipal()).thenReturn(user.getEmail());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUser());
        verify(userRepository).findByEmail(user.getEmail());
    }

    @Test
    @Order(7)
    @DisplayName("Test add product to cart")
    void testAddProductToCart() {
        ProductDto productDto = new ProductDto();
        productDto.setId(1L);
        productDto.setTitle("Test Product");

        UserDto userDto = new UserDto();
        userDto.setEmail("user@gmail.com");

        when(userMapper.toEntity(userDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);

        userService.addProductToCart(userDto, productDto);

        assertEquals(1, user.getCarts().size());

        verify(userRepository).save(user);
    }
}
