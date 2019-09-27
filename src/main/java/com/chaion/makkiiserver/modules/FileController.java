package com.chaion.makkiiserver.modules;

import com.chaion.makkiiserver.repository.file.StorageException;
import com.chaion.makkiiserver.repository.file.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping
public class FileController {
    private static final Logger logger = LoggerFactory.getLogger(FileController.class);
    @Autowired
    StorageService storageService;

    @PostMapping("/image/upload")
    @ResponseBody
    public String upload(@RequestParam(value = "image") MultipartFile file) {
        String newFilename = UUID.randomUUID().toString();
        try {
            String targetPath = "/image/" + newFilename;
            storageService.store(file, newFilename);
            return targetPath;
        } catch (StorageException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to save file " + file.getName());
        }
    }

    @GetMapping(value = "/image/{filename:.+}", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public ResponseEntity<Resource> serveImage(@PathVariable String filename) {
        Resource file = null;
        try {
            file = storageService.loadAsResource(filename);
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + file.getFilename() + "\"").body(file);
        } catch (StorageException e) {
            logger.error("load file failed: ", e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, filename + " not found");
        }
    }
}