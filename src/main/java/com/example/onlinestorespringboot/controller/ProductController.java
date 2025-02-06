package com.example.onlinestorespringboot.controller;

import com.example.onlinestorespringboot.dto.CreateProductDto;
import com.example.onlinestorespringboot.dto.ProductDto;
import com.example.onlinestorespringboot.dto.ProductFilterDTO;
import com.example.onlinestorespringboot.dto.ResponseDto;
import com.example.onlinestorespringboot.i18n.I18nUtil;
import com.example.onlinestorespringboot.service.ProductService;
import com.example.onlinestorespringboot.util.Messages;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ProductController {

    ProductService productService;
    I18nUtil i18nUtil;

    @PostMapping
    public ResponseEntity<ResponseDto> saveProduct(@RequestBody @Valid CreateProductDto createProductDto) {
        productService.save(createProductDto);
        return ResponseEntity.ok(new ResponseDto(i18nUtil.getMessage(Messages.PRODUCT_SUCCESS_SAVED)));
    }

    @GetMapping
    public ResponseEntity<Page<ProductDto>> findAllProducts(
            @RequestParam(value = "offset", required = false) Integer offset,
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @RequestParam(value = "sortBy", required = false) String sortBy) {
        if (offset == null) offset = 0;
        if (pageSize == null) pageSize = 10;
        if (sortBy == null || sortBy.isEmpty()) sortBy = "id";
        Page<ProductDto> products = productService.findAll(PageRequest.of(offset, pageSize, Sort.by(sortBy)));
        return ResponseEntity.ok(products);
    }

    @PutMapping
    public ResponseEntity<ProductDto> updateProduct(@RequestBody @Valid ProductDto productDto) {
        ProductDto updatedProductDto = productService.update(productDto);
        return ResponseEntity.ok(updatedProductDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDto> deleteProduct(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.ok(new ResponseDto(i18nUtil.getMessage(Messages.PRODUCT_SUCCESS_DELETED, String.valueOf(id))));
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<ProductDto>> filterProducts(
            @RequestParam(value = "offset", required = false) Integer offset,
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestBody ProductFilterDTO productFilterDTO) {
        if (offset == null) offset = 0;
        if (pageSize == null) pageSize = 10;
        if (sortBy == null || sortBy.isEmpty()) sortBy = "id";
        Page<ProductDto> products = productService
                .findAllByFilter(productFilterDTO, PageRequest.of(offset, pageSize, Sort.by(sortBy)));
        return ResponseEntity.ok(products);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProductDto>> searchProducts(
            @RequestParam(value = "offset", required = false) Integer offset,
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "search", required = false) String searchParameter) {
        if (offset == null) offset = 0;
        if (pageSize == null) pageSize = 10;
        if (sortBy == null || sortBy.isEmpty()) sortBy = "id";
        Page<ProductDto> products = productService
                .findAllByTitle(searchParameter, PageRequest.of(offset, pageSize, Sort.by(sortBy)));
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> findProductById(@PathVariable Long id) {
        ProductDto productDto = productService.findById(id);
        return ResponseEntity.ok(productDto);
    }
}

