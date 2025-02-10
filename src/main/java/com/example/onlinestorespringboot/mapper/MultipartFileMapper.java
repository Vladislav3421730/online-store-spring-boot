package com.example.onlinestorespringboot.mapper;


import com.example.onlinestorespringboot.dto.CreateImageDto;
import com.example.onlinestorespringboot.util.FileSavingUtils;
import org.springframework.web.multipart.MultipartFile;

public class MultipartFileMapper {

    public static CreateImageDto map(MultipartFile file) {
        return CreateImageDto.builder()
                .type(file.getContentType())
                .fileName(file.getOriginalFilename())
                .filePath(FileSavingUtils.saveFileToStatic(file))
                .build();
    }

}
