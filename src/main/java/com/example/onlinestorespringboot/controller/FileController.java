package com.example.onlinestorespringboot.controller;


import com.example.onlinestorespringboot.dto.AppErrorDto;
import com.example.onlinestorespringboot.exception.ImageNotFoundException;
import com.example.onlinestorespringboot.i18n.I18nUtil;
import com.example.onlinestorespringboot.util.Messages;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/uploads")
@RequiredArgsConstructor
@Setter
@Tag(name = "File", description = "controller for displaying images from directory uploads")
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Image successfully founded",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Resource.class))
        ),
        @ApiResponse(
                responseCode = "404",
                description = "Image not found",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppErrorDto.class))
        )
})
public class FileController {

    private final I18nUtil i18nUtil;

    @Value("${file.path}")
    private String path;

    @GetMapping("/{fileName}")
    @Operation(summary = "display file by his name")
    public ResponseEntity<Resource> getFile(@PathVariable String fileName) {
        Path filePath = Paths.get(path).resolve(fileName).normalize();
        Resource resource = new FileSystemResource(filePath);
        if (!resource.exists()) {
            throw new ImageNotFoundException(i18nUtil.getMessage(Messages.IMAGE_ERROR_NOT_FOUND, fileName));
        }
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }
}
