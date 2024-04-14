package com.example.backend.service.fileService;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ImgServiceImpl implements ImgService {

    @Override
    public UUID uploadFile(MultipartFile photo, String prefix) throws IOException {
        UUID id = UUID.randomUUID();
        try {
            // Construct the file path
            String filePath = "backend/files/images/" + prefix + "/";
            File directory = new File(filePath);

            // Create the directory if it doesn't exist
            if (!directory.exists()) {
                if (!directory.mkdirs()) {
                    throw new IOException("Failed to create the directory: " + directory);
                }
            }

            File output = new File(filePath + id + ".webp");

            // Ensure the output file is created successfully
            if (!output.createNewFile()) {
                throw new IOException("Failed to create the output file: " + output);
            }
            BufferedImage inputImage = ImageIO.read(photo.getInputStream());
            BufferedImage outputImage = new BufferedImage(inputImage.getWidth(), inputImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
            outputImage.getGraphics().drawImage(inputImage, 0, 0, null);
            ImageWriter writer = ImageIO.getImageWritersByMIMEType("image/webp").next();
            ImageWriteParam writeParam = writer.getDefaultWriteParam();
            writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            writeParam.setCompressionType("Lossy");
            if (photo.getSize() / 1024 > 400) {
                writeParam.setCompressionQuality(0.5f);
            } else {
                writeParam.setCompressionQuality(0.8f);
            }

            ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(output);
            writer.setOutput(imageOutputStream);
            writer.write(null, new javax.imageio.IIOImage(outputImage, null, null), writeParam);
            imageOutputStream.close();
            writer.dispose();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return id;
    }

    //    under code created by gpt
//    @Override
//    public UUID uploadFile(MultipartFile photo, String prefix) throws IOException {
//        UUID id = UUID.randomUUID();
//        try {
//
//            // Construct the file path
//            String filePath = "files/images/" + prefix + "/";
//            File directory = new File(filePath);
//
//            // Create the directory if it doesn't exist
//            if (!directory.exists()) {
//                if (!directory.mkdirs()) {
//                    throw new IOException("Failed to create the directory: " + directory);
//                }
//            }
//
//            File output = new File(filePath + id + ".webp");
//
//            // Ensure the output file is created successfully
//            if (!output.createNewFile()) {
//                throw new IOException("Failed to create the output file: " + output);
//            }
//
//            BufferedImage inputImage = ImageIO.read(photo.getInputStream());
//            BufferedImage outputImage = new BufferedImage(inputImage.getWidth(), inputImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
//            outputImage.getGraphics().drawImage(inputImage, 0, 0, null);
//            ImageWriter writer = ImageIO.getImageWritersByMIMEType("image/webp").next();
//            ImageWriteParam writeParam = writer.getDefaultWriteParam();
//            writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
//            writeParam.setCompressionType("Lossy");
//
//            if (photo.getSize() / 1024 > 400) {
//                writeParam.setCompressionQuality(0.5f);
//            } else {
//                writeParam.setCompressionQuality(0.8f);
//            }
//
//            // Ensure ImageOutputStream is created successfully
//            try (ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(output)) {
//                if (imageOutputStream == null) {
//                    throw new IOException("Failed to create ImageOutputStream.");
//                }
//
//                writer.setOutput(imageOutputStream);
//                writer.write(null, new IIOImage(outputImage, null, null), writeParam);
//            } finally {
//                writer.dispose();
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        return id;
//    }


    @Override
    public void getFile(String url, HttpServletResponse httpServletResponse) throws IOException {
        InputStream inputStream = new FileInputStream("backend/files/images/" + url + ".webp");
        inputStream.transferTo(httpServletResponse.getOutputStream());
        inputStream.close();
        httpServletResponse.getOutputStream().close();

    }

    @Override
    public HttpEntity<String> deleteFiles(String url) {
        Path path = Paths.get("backend/files/images/" + url + ".webp");
        try {
            Files.delete(path);
        } catch (IOException e) {
            return ResponseEntity.ok().body("File not found!");
        }
        return ResponseEntity.ok().body("Successfully deleted!");
    }

}
