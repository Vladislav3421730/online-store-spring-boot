package com.example.controllerTests;

import com.example.factory.ProductFactory;
import com.example.onlinestorespringboot.OnlineStoreSpringBootApplication;
import com.example.onlinestorespringboot.dto.AddressDto;
import com.example.onlinestorespringboot.dto.LoginUserDto;
import com.example.onlinestorespringboot.dto.OrderRequestDto;
import com.example.onlinestorespringboot.dto.ProductDto;
import com.example.onlinestorespringboot.service.ProductService;
import com.example.utils.TokenUtils;
import org.junit.ClassRule;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.*;

import java.io.File;
import java.time.Duration;

@Testcontainers
@SpringBootTest(classes = OnlineStoreSpringBootApplication.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
public class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoSpyBean
    private ProductService productService;

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("postgres")
            .withPassword("postgres")
            .withReuse(false);

    @ClassRule
    public static DockerComposeContainer environment =
            new DockerComposeContainer<>(new File("src/test/resources/compose-test.yaml"))
                    .withExposedService("redis", 6379,
                            Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(30)));

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static LoginUserDto loginUserDto;
    private static ProductDto productDtoWithZeroAmount;
    private static OrderRequestDto orderRequestDto;

    @BeforeAll
    static void setup() {
        environment.start();

        AddressDto addressDto = AddressDto.builder()
                .id(1L)
                .town("Москва")
                .region("Московская область")
                .exactAddress("ул. Тверская, д. 1")
                .postalCode("125009")
                .build();

        orderRequestDto = new OrderRequestDto();
        orderRequestDto.setTotalCoast(500.0D);
        orderRequestDto.setAddress(addressDto);
        productDtoWithZeroAmount = ProductFactory.createProductDtoWithZeroAmount();
        loginUserDto = new LoginUserDto("user@gmail.com", "q1w2e3");
    }

    @AfterAll
    static void teardown() {
        environment.stop();
    }

    @Test
    @Order(1)
    @DisplayName("Test add product to user's cart")
    void testAddProductToCart() throws Exception {

        String accessToken = TokenUtils.getAccessTokenFromRequest(mockMvc, loginUserDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/cart/add/{id}", 1L)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", notNullValue()));
    }

    @Test
    @Order(2)
    @DisplayName("Test increment product in user cart")
    void testIncrementProductInUserCart() throws Exception {

        String accessToken = TokenUtils.getAccessTokenFromRequest(mockMvc, loginUserDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/cart/increment/{index}", 0L)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", notNullValue()));
    }

    @Test
    @Order(3)
    @DisplayName("Test increment product in user cart with wrong index")
    void testIncrementProductInUserCartWithWrongIndex() throws Exception {

        String accessToken = TokenUtils.getAccessTokenFromRequest(mockMvc, loginUserDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/cart/increment/{index}", Integer.MAX_VALUE)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", notNullValue()))
                .andExpect(jsonPath("$.code", is(404)));
    }

    @Test
    @Order(4)
    @DisplayName("Test decrement product in user cart")
    void testDecrementProductInUserCart() throws Exception {

        String accessToken = TokenUtils.getAccessTokenFromRequest(mockMvc, loginUserDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/cart/decrement/{index}", 0L)

                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", notNullValue()));
    }

    @Test
    @Order(5)
    @DisplayName("Test delete product in user cart")
    void testDeleteProductInUserCart() throws Exception {

        String accessToken = TokenUtils.getAccessTokenFromRequest(mockMvc, loginUserDto);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/cart/delete/{index}", 0L)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", notNullValue()));
    }

    @Test
    @Order(6)
    @DisplayName("Test make order")
    void testMakeOrder() throws Exception {

        String accessToken = TokenUtils.getAccessTokenFromRequest(mockMvc, loginUserDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequestDto))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", notNullValue()));
    }

}
