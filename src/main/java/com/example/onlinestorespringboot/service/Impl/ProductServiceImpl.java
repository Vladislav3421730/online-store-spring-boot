package com.example.onlinestorespringboot.service.Impl;

import com.example.onlinestorespringboot.dto.CreateImageDto;
import com.example.onlinestorespringboot.dto.CreateProductDto;
import com.example.onlinestorespringboot.dto.ProductDto;
import com.example.onlinestorespringboot.dto.ProductFilterDTO;
import com.example.onlinestorespringboot.exception.ProductNotFoundException;
import com.example.onlinestorespringboot.i18n.I18nUtil;
import com.example.onlinestorespringboot.mapper.ImageMapper;
import com.example.onlinestorespringboot.mapper.MultipartFileMapper;
import com.example.onlinestorespringboot.mapper.ProductMapper;
import com.example.onlinestorespringboot.model.Image;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ProductServiceImpl implements ProductService {

    ProductRepository productRepository;
    ProductMapper productMapper;
    ImageMapper imageMapper;
    I18nUtil i18nUtil;

    @Override
    public void save(CreateProductDto createProductDTO, List<MultipartFile> files) {
        log.info("Save product {}", createProductDTO);
        Product product = productMapper.toNewEntity(createProductDTO);
        if (files != null) {
            List<CreateImageDto> images = files.stream()
                    .filter(file -> !file.isEmpty())
                    .map(MultipartFileMapper::map)
                    .toList();

            product.setImageList(new ArrayList<>());
            for (CreateImageDto imageDto : images) {
                Image image = imageMapper.toNewEntity(imageDto);
                product.addImageToList(image);
            }
        }
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
        if (!productRepository.existsById(productDto.getId())) {
            log.error("Products with id {} not found", productDto.getId());
            throw new ProductNotFoundException(i18nUtil.getMessage(Messages.PRODUCT_ERROR_NOT_FOUND, String.valueOf(productDto.getId())));
        }
        log.info("Updating product with id: {}", productDto.getId());
        Product product = productMapper.toEntity(productDto);
        Product updatedProduct = productRepository.save(product);
        log.info("Product updated successfully with id: {}", productDto.getId());
        return productMapper.toDTO(updatedProduct);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            log.error("Products with id {} not found", id);
            throw new ProductNotFoundException(i18nUtil.getMessage(Messages.PRODUCT_ERROR_NOT_FOUND, String.valueOf(id)));
        }
        log.info("Deleting product with id: {}", id);
        productRepository.deleteProductWithOrderItems(id);
        log.info("Product with id {} deleted successfully", id);
    }

}
