package com.example.demo.controller;

import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

@Controller
public class LoadFileController {

  @Value("${upload.file.path}")
  private String pathUploadFile;

  @GetMapping(value = "loadFile")
  public void index(@RequestParam(value = "fileName") String fileName, HttpServletResponse response) {
    try {
      File file = new File(pathUploadFile + File.separator + fileName);
      if (file.exists()) {
        String contentType = null;
        // Kiểm tra loại tệp dựa trên phần mở rộng của tệp
        if (fileName.endsWith(".pdf")) {
          contentType = MediaType.APPLICATION_PDF_VALUE;
        } else if (fileName.endsWith(".docx")) {
          contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        } else if (fileName.endsWith(".pptx")) {
          contentType = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
        } else if (fileName.endsWith(".xlsx")) {
          contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        }
        if (contentType != null) {
          response.setContentType(contentType);
          response.setHeader("Content-Disposition", "inline; filename=" + fileName);
          try (InputStream inputStream = Files.newInputStream(file.toPath())) {
            IOUtils.copy(inputStream, response.getOutputStream());
            response.flushBuffer();
          }
        } else {
          response.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
        }
      } else {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      }
    } catch (IOException e) {
      e.printStackTrace();
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }

}
