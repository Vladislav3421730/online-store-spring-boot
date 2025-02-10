package com.example.onlinestorespringboot.controller;

import com.example.onlinestorespringboot.dto.*;
import com.example.onlinestorespringboot.i18n.I18nUtil;
import com.example.onlinestorespringboot.service.ProductService;
import com.example.onlinestorespringboot.util.Messages;
import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Tag(name = "Products", description = "Endpoints for managing products (find, find by id, find by filter, save, delete, edit)")
public class ProductController {

    ProductService productService;
    Validator validator;
    I18nUtil i18nUtil;


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Save a new product with images", description = "Creates and saves a new product in the system")
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
            ),
            @ApiResponse(
                    responseCode = "200",
                    description = "Product was saved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))
            )
    })
    public ResponseEntity<ResponseDto> saveProduct(
            @RequestPart(value = "product") String data,
            @RequestPart(value = "file", required = false) List<MultipartFile> files) throws MethodArgumentNotValidException {

        CreateProductDto createProductDto = new Gson().fromJson(data, CreateProductDto.class);
        Set<ConstraintViolation<CreateProductDto>> violations = validator.validate(createProductDto);

        if (!violations.isEmpty()) {
            BindingResult bindingResult = new BeanPropertyBindingResult(createProductDto, "createProductDto");
            violations.forEach(violation -> {
                bindingResult.rejectValue(violation.getPropertyPath().toString(), "", violation.getMessage());
            });
            throw new MethodArgumentNotValidException(null, bindingResult);
        }
        productService.save(createProductDto, files);
        return ResponseEntity.ok(new ResponseDto(i18nUtil.getMessage(Messages.PRODUCT_SUCCESS_SAVED)));
    }

    @GetMapping
    @Operation(summary = "Find all products", description = "Retrieves all products with pagination")
    @ApiResponse(
            responseCode = "200",
            description = "Find all products (pagination included)",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = ProductDto.class)))
    )
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
    @Operation(summary = "Update a product", description = "Updates an existing product in the system")
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
            ),
            @ApiResponse(
                    responseCode = "200",
                    description = "Product was updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))
            )
    })
    public ResponseEntity<ProductDto> updateProduct(@RequestBody @Valid ProductDto productDto) {
        ProductDto updatedProductDto = productService.update(productDto);
        return ResponseEntity.ok(updatedProductDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a product", description = "Deletes a product by its ID")
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
            ),
            @ApiResponse(
                    responseCode = "200",
                    description = "Product was deleted successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))
            )
    })
    public ResponseEntity<ResponseDto> deleteProduct(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.ok(new ResponseDto(i18nUtil.getMessage(Messages.PRODUCT_SUCCESS_DELETED, String.valueOf(id))));
    }

    @GetMapping("/filter")
    @Operation(summary = "Find products by filters", description = "Retrieves products based on filter criteria with pagination")
    @ApiResponse(
            responseCode = "200",
            description = "Find all products by filters (pagination included)",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = ProductDto.class)))
    )
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
    @Operation(summary = "Search products by title", description = "Finds products by their title with pagination")
    @ApiResponse(
            responseCode = "200",
            description = "Find all products by title (pagination included)",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = ProductDto.class)))
    )
    public ResponseEntity<Page<ProductDto>> searchProducts(
            @RequestParam(value = "offset", required = false) Integer offset,
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "title", required = false) String title) {
        if (offset == null) offset = 0;
        if (pageSize == null) pageSize = 10;
        if (sortBy == null || sortBy.isEmpty()) sortBy = "id";
        Page<ProductDto> products = productService
                .findAllByTitle(title, PageRequest.of(offset, pageSize, Sort.by(sortBy)));
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Find product by ID", description = "Retrieves a product by its unique ID")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid id",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppErrorDto.class))
            ),
            @ApiResponse(
                    responseCode = "200",
                    description = "Product was founded successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDto.class))
            )
    })
    public ResponseEntity<ProductDto> findProductById(@PathVariable Long id) {
        ProductDto productDto = productService.findById(id);
        return ResponseEntity.ok(productDto);
    }
}

