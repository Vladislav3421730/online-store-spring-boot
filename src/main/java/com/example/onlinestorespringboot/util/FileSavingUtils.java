package com.example.onlinestorespringboot.util;

import com.example.onlinestorespringboot.exception.FileSavingException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@UtilityClass
@Slf4j
public class FileSavingUtils {

    private final static String PATH_TO_DIR = "uploads";

    public String saveFileToStatic(MultipartFile file)  {
        try {
            Path uploadPath = Paths.get(PATH_TO_DIR);

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return "/" + PATH_TO_DIR + "/" + fileName;
        } catch (IOException e) {
            log.error("Error with saving file {}", e.getMessage());
            throw new FileSavingException(e.getMessage());
        }
    }
}
