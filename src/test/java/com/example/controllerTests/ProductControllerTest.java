package com.example.controllerTests;

import com.example.factory.ProductFactory;
import com.example.onlinestorespringboot.OnlineStoreSpringBootApplication;
import com.example.onlinestorespringboot.dto.CreateProductDto;
import com.example.onlinestorespringboot.dto.ProductDto;
import com.example.onlinestorespringboot.dto.ProductFilterDTO;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Testcontainers
@SpringBootTest(classes = OnlineStoreSpringBootApplication.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
public class ProductControllerTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("postgres")
            .withPassword("postgres")
            .withReuse(false);

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static CreateProductDto createProductDto;
    private static CreateProductDto createInvalidProductDto;
    private static ProductDto updatedProductDto;
    private static ProductFilterDTO productFilterDTO;


    @BeforeAll
    static void setup() {
        createProductDto = ProductFactory.createProductDto();
        createInvalidProductDto = ProductFactory.createInvalidProductDto();
        updatedProductDto = ProductFactory.createUpdatedProductDto();

        productFilterDTO = new ProductFilterDTO();
        productFilterDTO.setMinPrice(BigDecimal.valueOf(1000));
        productFilterDTO.setMinPrice(BigDecimal.valueOf(5000));
        productFilterDTO.setCategory("Бытовая техника");
    }

    @Autowired
    MockMvc mockMvc;

    @Test
    @Order(1)
    @DisplayName("Test save product")
    @WithMockUser(roles = "MANAGER")
    public void testSaveProduct() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createProductDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("The product was saved successfully")));
    }

    @Test
    @Order(2)
    @DisplayName("Test save product with invalid data")
    @WithMockUser(roles = "MANAGER")
    public void testSaveProductWithInvalidData() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createInvalidProductDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.description", notNullValue()));
    }

    @Test
    @Order(3)
    @DisplayName("Test save product with unauthorized user")
    public void testSaveProductWithUnauthorizedUser() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createProductDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message", notNullValue()))
                .andExpect(jsonPath("$.code", notNullValue()));
    }

    @Test
    @Order(4)
    @DisplayName("Test find all products")
    public void testFindAllProducts() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/products")
                        .param("pageSize", "15"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(15));
    }

    @Test
    @Order(5)
    @DisplayName("Test find product by id")
    public void testFindProductById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/products/{id}", 1L))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L))
                .andExpectAll(
                        jsonPath("$.title", is("Ноутбук Lenovo")),
                        jsonPath("$.description", is("Мощный ноутбук с процессором Intel i7, 16GB RAM и SSD 512GB")),
                        jsonPath("$.category", is("Электроника")),
                        jsonPath("$.amount", is(15))
                );
    }

    @Test
    @Order(5)
    @DisplayName("Test find product by invalid id")
    public void testFindProductByInvalidId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/products/{id}", Long.MIN_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", notNullValue()))
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    @Order(6)
    @DisplayName("Test find products by title")
    public void testSearchProducts() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/products/search")
                        .param("title", "Samsung")
                        .param("offset", "0")
                        .param("pageSize", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(3));
    }

    @Test
    @Order(7)
    @DisplayName("Test find products by filter")
    public void testFilterProducts() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/api/products/filter")
                        .param("offset", "0")
                        .param("pageSize", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productFilterDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(9));
    }

    @Test
    @Order(8)
    @DisplayName("Test update product")
    @WithMockUser(roles = "MANAGER")
    public void testUpdateProduct() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.put("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProductDto)))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.id", is(1)),
                        jsonPath("$.title", is("New Laptop")),
                        jsonPath("$.description", is("Laptop with 16GB RAM")),
                        jsonPath("$.category", is("Electronics")),
                        jsonPath("$.amount", is(5)),
                        jsonPath("$.coast", is(200.3))
                );
    }

    @Test
    @Order(9)
    @DisplayName("Test delete product")
    @WithMockUser(roles = "MANAGER")
    public void testDeleteProduct() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/products/{id}", 1L))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", notNullValue()));
    }


}
