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
        response.setContentType(MediaType.APPLICATION_PDF_VALUE); // Đặt loại nội dung của phản hồi là PDF
        response.setHeader("Content-Disposition", "inline; filename=" + fileName); // Hiển thị nội dung trên trình duyệt thay vì tải xuống
        try (InputStream inputStream = Files.newInputStream(file.toPath())) {
          IOUtils.copy(inputStream, response.getOutputStream()); // Sao chép dữ liệu từ file đến phản hồi
          response.flushBuffer();
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
