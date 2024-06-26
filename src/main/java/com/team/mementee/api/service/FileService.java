package com.team.mementee.api.service;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.team.mementee.api.domain.enumtype.MessageType;
import ezvcard.Ezvcard;
import ezvcard.VCard;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import static com.team.mementee.api.domain.enumtype.MessageType.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

    @NotNull
    public static ObjectMetadata createObjectMetaData(Long contentLength, String contentType) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(contentLength);
        metadata.setContentType(contentType);
        return metadata;
    }

    @NotNull
    public String generateFileName(String extension) {
        return UUID.randomUUID() + "." + extension;
    }

    @NotNull
    public String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    public MessageType getFileType(String contentType) {
        if (contentType.startsWith("image")) return IMAGE;
        if (contentType.startsWith("video")) return VIDEO;
        if ("application/pdf".equals(contentType)) return PDF;
        if ("application/zip".equals(contentType)) return ZIP;
        if ("text/vcard".equals(contentType)) return CONTACT;
        else return FILE;
    }

    public String extractFileNameFromUrl(String fileUrl) {
        try {
            URL url = new URL(fileUrl);
            String path = url.getPath();
            return path.substring(path.lastIndexOf('/') + 1);
        } catch (MalformedURLException e) {
            log.error("Error extracting file name from URL: {}", e.getMessage(), e);
            return null;

        }
    }

    public VCard readVCard(String filePath) throws IOException {
        String vcardString = new String(Files.readAllBytes(Paths.get(filePath)));
        return Ezvcard.parse(vcardString).first();
    }
}
