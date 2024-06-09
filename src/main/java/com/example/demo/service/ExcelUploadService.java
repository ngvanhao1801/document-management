package com.example.demo.service;

import com.example.demo.entity.StudentInformation;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ExcelUploadService {

  public static boolean isValidExcelFile(MultipartFile file) {
    return Objects.equals(file.getContentType(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
  }

  public static List<StudentInformation> getStudentsDataFromExcel(InputStream inputStream) {
    List<StudentInformation> students = new ArrayList<>();
    try (XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
      if (workbook.getNumberOfSheets() == 0) {
        throw new IOException("Tệp Excel không có sheet nào.");
      }

      XSSFSheet sheet = workbook.getSheetAt(0); // Đọc sheet đầu tiên

      if (sheet == null) {
        throw new NullPointerException("Sheet không tồn tại trong tệp Excel.");
      }

      int rowIndex = 0;
      for (Row row : sheet) {
        if (rowIndex == 0) {
          rowIndex++;
          continue;
        }
        Iterator<Cell> cellIterator = row.iterator();
        int cellIndex = 0;
        StudentInformation studentInformation = new StudentInformation();
        while (cellIterator.hasNext()) {
          Cell cell = cellIterator.next();
          switch (cellIndex) {
            case 0 -> studentInformation.setAddress(getCellValueAsString(cell));
            case 1 -> studentInformation.setBirthday(getCellValueAsDate(cell));
            case 2 -> studentInformation.setClassroom(getCellValueAsString(cell));
            case 3 -> studentInformation.setEmail(getCellValueAsString(cell));
            case 4 -> studentInformation.setGender(getCellValueAsString(cell));
            case 5 -> studentInformation.setMajors(getCellValueAsString(cell));
            case 6 -> studentInformation.setName(getCellValueAsString(cell));
            case 7 -> studentInformation.setStudentCode((long) getCellValueAsNumber(cell));
            case 8 -> studentInformation.setYearOfAdmission((int) getCellValueAsNumber(cell));
            case 9 -> studentInformation.setStatusStudent(getCellValueAsString(cell));
            default -> {
            }
          }
          cellIndex++;
        }
        students.add(studentInformation);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return students;
  }

  private static String getCellValueAsString(Cell cell) {
    if (cell.getCellType() == CellType.STRING) {
      return cell.getStringCellValue();
    } else if (cell.getCellType() == CellType.NUMERIC) {
      return String.valueOf(cell.getNumericCellValue());
    } else if (cell.getCellType() == CellType.BOOLEAN) {
      return String.valueOf(cell.getBooleanCellValue());
    }
    return null;
  }

  private static double getCellValueAsNumber(Cell cell) {
    if (cell.getCellType() == CellType.NUMERIC) {
      return cell.getNumericCellValue();
    } else if (cell.getCellType() == CellType.STRING) {
      try {
        return Double.parseDouble(cell.getStringCellValue());
      } catch (NumberFormatException e) {
        e.printStackTrace();
      }
    }
    return 0;
  }

  private static Date getCellValueAsDate(Cell cell) {
    if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
      return cell.getDateCellValue();
    } else if (cell.getCellType() == CellType.STRING) {
      try {
        return new SimpleDateFormat("yyyy-MM-dd").parse(cell.getStringCellValue());
      } catch (ParseException e) {
        e.printStackTrace();
      }
    }
    return null;
  }
}