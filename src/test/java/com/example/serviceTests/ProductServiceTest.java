package com.example.serviceTests;

import com.example.factory.ProductFactory;
import com.example.onlinestorespringboot.dto.CreateProductDto;
import com.example.onlinestorespringboot.dto.ProductDto;
import com.example.onlinestorespringboot.exception.ProductNotFoundException;
import com.example.onlinestorespringboot.i18n.I18nUtil;
import com.example.onlinestorespringboot.mapper.ProductMapper;
import com.example.onlinestorespringboot.model.Product;
import com.example.onlinestorespringboot.repository.ProductRepository;
import com.example.onlinestorespringboot.service.Impl.ProductServiceImpl;
import com.example.onlinestorespringboot.util.Messages;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private I18nUtil i18nUtil;

    @InjectMocks
    private ProductServiceImpl productService;

    private static Product firstProduct;
    private static Product secondProduct;
    private static ProductDto productDto;
    private static CreateProductDto createProductDto;
    private static ProductDto firstProductDto;
    private static ProductDto secondProductDto;

    @BeforeAll
    static void setup() {
        firstProduct = ProductFactory.createProduct(1L,"Test Product 1");

        secondProduct = ProductFactory.createProduct(2L,"Test product 2");

        productDto = ProductFactory.createProductDto(1L,"Test Product 1");

        createProductDto = new CreateProductDto();
        createProductDto.setTitle("Test Product");
        createProductDto.setDescription("Product Description");
        createProductDto.setCoast(BigDecimal.valueOf(100.0));

        firstProductDto = ProductFactory.createProductDto(2L,"Product 2");
        secondProductDto = ProductFactory.createProductDto(3L,"Product 3");
    }

    @Test
    @Order(1)
    @DisplayName("Test save product")
    void testSaveProduct() {
        when(productMapper.toNewEntity(createProductDto)).thenReturn(firstProduct);
        when(productRepository.save(firstProduct)).thenReturn(firstProduct);

        productService.save(createProductDto,List.of());

        verify(productMapper).toNewEntity(createProductDto);
        verify(productRepository).save(firstProduct);
    }

    @Test
    @Order(2)
    @DisplayName("Test find all products")
    void testFindAllProducts() {
        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Product> products = List.of(firstProduct, secondProduct);
        List<ProductDto> productDtos = List.of(firstProductDto, secondProductDto);

        Page<Product> productPage = new PageImpl<>(products, pageRequest, products.size());
        when(productRepository.findAll(pageRequest)).thenReturn(productPage);

        when(productMapper.toDTO(firstProduct)).thenReturn(firstProductDto);
        when(productMapper.toDTO(secondProduct)).thenReturn(secondProductDto);

        Page<ProductDto> result = productService.findAll(pageRequest);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(2, result.getContent().size());
        assertEquals("Product 2", result.getContent().get(0).getTitle());
        assertEquals("Product 3", result.getContent().get(1).getTitle());

        verify(productRepository).findAll(pageRequest);
        verify(productMapper).toDTO(firstProduct);
        verify(productMapper).toDTO(firstProduct);
    }

    @Test
    @Order(3)
    @DisplayName("Test find product by id")
    void testFindProductById() {
        when(productRepository.findById(firstProduct.getId())).thenReturn(Optional.of(firstProduct));
        when(productMapper.toDTO(firstProduct)).thenReturn(productDto);

        ProductDto fetchedProduct = productService.findById(firstProduct.getId());

        assertNotNull(fetchedProduct);
        assertEquals(firstProduct.getId(), fetchedProduct.getId());
        assertEquals(firstProduct.getTitle(), fetchedProduct.getTitle());

        verify(productRepository).findById(firstProduct.getId());
        verify(productMapper).toDTO(firstProduct);
    }

    @Test
    @Order(4)
    @DisplayName("Test find product by id - product not found")
    void testFindProductByIdNotFound() {
        when(productRepository.findById(firstProduct.getId())).thenReturn(Optional.empty());
        when(i18nUtil.getMessage(Messages.PRODUCT_ERROR_NOT_FOUND, String.valueOf(firstProduct.getId())))
                .thenReturn("Product not found");

        ProductNotFoundException exception = assertThrows(ProductNotFoundException.class, () -> productService.findById(firstProduct.getId()));

        assertEquals("Product not found", exception.getMessage());
        verify(productRepository).findById(firstProduct.getId());
    }

    @Test
    @Order(5)
    @DisplayName("Test update product - successful")
    void testUpdateProduct() {
        productDto.setTitle("New title");
        when(productRepository.existsById(productDto.getId())).thenReturn(true);
        when(productMapper.toEntity(productDto)).thenReturn(firstProduct);
        when(productRepository.save(firstProduct)).thenReturn(firstProduct);
        when(productMapper.toDTO(firstProduct)).thenReturn(productDto);

        ProductDto updatedProduct = productService.update(productDto);

        assertNotNull(updatedProduct);
        assertEquals(productDto.getId(), updatedProduct.getId());
        assertEquals(updatedProduct.getTitle(),"New title");
        verify(productRepository).existsById(productDto.getId());
        verify(productMapper).toEntity(productDto);
        verify(productRepository).save(firstProduct);
    }

    @Test
    @Order(6)
    @DisplayName("Test update product - product not found")
    void testUpdateProductNotFound() {
        when(productRepository.existsById(productDto.getId())).thenReturn(false);
        when(i18nUtil.getMessage(Messages.PRODUCT_ERROR_NOT_FOUND, String.valueOf(productDto.getId())))
                .thenReturn("Product not found");

        ProductNotFoundException exception = assertThrows(ProductNotFoundException.class, () -> productService.update(productDto));

        assertEquals("Product not found", exception.getMessage());
        verify(productRepository).existsById(productDto.getId());
    }

    @Test
    @Order(7)
    @DisplayName("Test delete product - successful")
    void testDeleteProduct() {
        when(productRepository.existsById(firstProduct.getId())).thenReturn(true);

        productService.delete(firstProduct.getId());

        verify(productRepository).existsById(firstProduct.getId());
        verify(productRepository).deleteProductWithOrderItems(firstProduct.getId());
    }

    @Test
    @Order(8)
    @DisplayName("Test delete product - product not found")
    void testDeleteProductNotFound() {
        when(productRepository.existsById(firstProduct.getId())).thenReturn(false);
        when(i18nUtil.getMessage(Messages.PRODUCT_ERROR_NOT_FOUND, String.valueOf(firstProduct.getId())))
                .thenReturn("Product not found");

        ProductNotFoundException exception = assertThrows(ProductNotFoundException.class, () -> productService.delete(firstProduct.getId()));

        assertEquals("Product not found", exception.getMessage());
        verify(productRepository).existsById(firstProduct.getId());
    }
}
