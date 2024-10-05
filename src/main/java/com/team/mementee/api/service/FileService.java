package com.team.mementee.api.service;

import com.team.mementee.api.domain.enumtype.MessageType;
import ezvcard.Ezvcard;
import ezvcard.VCard;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

import static com.team.mementee.api.domain.enumtype.MessageType.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

    @NotNull
    public String generateFileName(String extension) {
        return UUID.randomUUID() + "." + extension;
    }

    @NotNull
    public String getExtension(MultipartFile file) {
        String fileName = Objects.requireNonNull(file.getOriginalFilename());
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    public MessageType extractFileType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null) return FILE;

        return switch (contentType) {
            case "application/pdf" -> PDF;
            case "application/zip" -> ZIP;
            case "text/vcard" -> CONTACT;
            default -> {
                if (contentType.startsWith("image")) yield IMAGE;
                if (contentType.startsWith("video")) yield VIDEO;
                yield FILE;
            }
        };
    }

    public VCard readVCard(String filePath) throws IOException {
        String vcardString = new String(Files.readAllBytes(Paths.get(filePath)));
        return Ezvcard.parse(vcardString).first();
    }
}