package com.example.onlinestorespringboot.service.Impl;

import com.example.onlinestorespringboot.dto.CreateProductDto;
import com.example.onlinestorespringboot.dto.ProductDto;
import com.example.onlinestorespringboot.dto.ProductFilterDTO;
import com.example.onlinestorespringboot.exception.ProductNotFoundException;
import com.example.onlinestorespringboot.i18n.I18nUtil;
import com.example.onlinestorespringboot.mapper.ProductMapper;
import com.example.onlinestorespringboot.model.Product;
import com.example.onlinestorespringboot.repository.ProductRepository;
import com.example.onlinestorespringboot.service.ProductService;
import com.example.onlinestorespringboot.util.Messages;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ProductServiceImpl implements ProductService {

    ProductRepository productRepository;
    ProductMapper productMapper;
    I18nUtil i18nUtil;

    @Override
    public void save(CreateProductDto createProductDTO) {
        Product product = productMapper.toNewEntity(createProductDTO);
        productRepository.save(product);
    }

    @Override
    public Page<ProductDto> findAll(PageRequest pageRequest) {
        log.info("Fetching all products");
        return productRepository.findAll(pageRequest)
                .map(productMapper::toDTO);
    }

    @Override
    public ProductDto findById(Long id) {
        log.info("Fetching product with id: {}", id);
        Product product = productRepository.findById(id).orElseThrow(() ->
                new ProductNotFoundException(i18nUtil.getMessage(Messages.PRODUCT_ERROR_NOT_FOUND, String.valueOf(id))));
        return productMapper.toDTO(product);
    }

    @Override
    public Page<ProductDto> findAllByTitle(String title, PageRequest pageRequest) {
        log.info("Searching products with title: {}", title);
        return productRepository.findAllByTitleContainingIgnoreCase(title, pageRequest)
                .map(productMapper::toDTO);
    }

    @Override
    public Page<ProductDto> findAllByFilter(ProductFilterDTO productFilterDTO, PageRequest pageRequest) {
        log.info("Fetching products with filter: {}", productFilterDTO);
        return productRepository.findAllByFilter(productFilterDTO, pageRequest)
                .map(productMapper::toDTO);
    }

    @Override
    @Transactional
    public ProductDto update(ProductDto productDto) {
        log.info("Updating product with id: {}", productDto.getId());
        Product product = productMapper.toEntity(productDto);
        Product updatedProduct = productRepository.save(product);
        log.info("Product updated successfully with id: {}", productDto.getId());
        return productMapper.toDTO(updatedProduct);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (productRepository.existsById(id)) {
            log.info("Deleting product with id: {}", id);
            productRepository.deleteProductWithOrderItems(id);
            log.info("Product with id {} deleted successfully", id);
        } else
            throw new ProductNotFoundException(i18nUtil.getMessage(Messages.PRODUCT_ERROR_NOT_FOUND, String.valueOf(id)));

    }

}
