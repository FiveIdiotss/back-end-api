package com.mementee.api.service;

import com.mementee.s3.S3Service;
import ezvcard.Ezvcard;
import ezvcard.VCard;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {
    private final S3Service s3Service;

    public VCard readVCard(String filePath) throws IOException {
        String vcardString = new String(Files.readAllBytes(Paths.get(filePath)));
        return Ezvcard.parse(vcardString).first();
    }
}
