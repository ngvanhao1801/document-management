package com.example.demo.controller.admin;

import com.example.demo.entity.Document;
import com.example.demo.entity.DocumentStatus;
import com.example.demo.entity.Folder;
import com.example.demo.entity.User;
import com.example.demo.repository.DocumentRepository;
import com.example.demo.repository.DocumentStatusRepository;
import com.example.demo.repository.FolderRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class DocumentController {

  private final DocumentRepository documentRepository;

  private final FolderRepository folderRepository;

  private final UserRepository userRepository;

  private final DocumentStatusRepository documentStatusRepository;

  private final HttpSession httpSession;

  @Value("${upload.path}")
  private String pathUploadImage;

  @Value("${upload.file.path}")
  private String pathUploadFile;

  public DocumentController(DocumentRepository documentRepository,
                            FolderRepository folderRepository,
                            UserRepository userRepository,
                            DocumentStatusRepository documentStatusRepository, HttpSession httpSession) {
    this.documentRepository = documentRepository;
    this.folderRepository = folderRepository;
    this.userRepository = userRepository;
    this.documentStatusRepository = documentStatusRepository;
    this.httpSession = httpSession;
  }

  @ModelAttribute(value = "user")
  public User user(Model model, Principal principal, User user) {

    if (principal != null) {
      model.addAttribute("user", new User());
      user = userRepository.findByEmail(principal.getName());
      model.addAttribute("user", user);
    }

    return user;
  }

  // show list document - table list
  @ModelAttribute("documents")
  public List<Document> showDocument(Model model) {
    List<Document> documents = documentRepository.findAll();
    model.addAttribute("documents", documents);

    return documents;
  }

  @GetMapping(value = "/documents")
  public String documents(Model model, Principal principal) {
    Document document = new Document();
    model.addAttribute("document", document);

    return "admin/documents";
  }

  // add document
  @PostMapping(value = "/addDocument")
  public String addDocument(@ModelAttribute("document") Document document,
                            ModelMap model,
                            @RequestParam("file") MultipartFile[] files,
                            HttpServletRequest httpServletRequest) {
    for (MultipartFile file : files) {
      try {
        String nameFile = file.getOriginalFilename();
        String filePath = null;
        if (file.getContentType().startsWith("image")) {
          filePath = pathUploadImage + "/" + nameFile;
        }
        if (file.getContentType().equals("application/pdf")) {
          filePath = pathUploadFile + "/" + nameFile;
        }
        if (filePath != null) {
          File convFile = new File(filePath);
          FileOutputStream fos = new FileOutputStream(convFile);
          fos.write(file.getBytes());
          fos.close();
        }

        // Lưu thông tin loại phương tiện vào cơ sở dữ liệu
        document.setMediaType(file.getContentType());

        // Lưu tên file vào đối tượng Document
        if (file.getContentType().startsWith("image")) {
          document.setDocumentImage(nameFile);
        }
        if (file.getContentType().equals("application/pdf")) {
          document.setDocumentFile(nameFile);
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    Document savedDocument = documentRepository.save(document);

    if (savedDocument != null) {
      model.addAttribute("message", "Update success");
      model.addAttribute("document", document);
    } else {
      model.addAttribute("message", "Update failure");
      model.addAttribute("document", document);
    }
    return "redirect:/admin/documents";
  }

  // show select option ở add folder
  @ModelAttribute("folderList")
  public List<Folder> showFolder(Model model) {
    List<Folder> folderList = folderRepository.findAll();
    model.addAttribute("folderList", folderList);

    return folderList;
  }

  @ModelAttribute("userList")
  public List<User> showUser(Model model) {
    List<User> userList = userRepository.findAll();
    model.addAttribute("userList", userList);

    return userList;
  }

  @ModelAttribute("statusList")
  public List<DocumentStatus> showStatus(Model model) {
    List<DocumentStatus> statusList = documentStatusRepository.findAll();
    httpSession.setAttribute("statusList", statusList);
    model.addAttribute("statusList", statusList);

    return statusList;
  }

  // get Edit brand
  @GetMapping(value = "/editDocument/{id}")
  public String editDocument(@PathVariable("id") Long id, ModelMap model) {
    Document document = documentRepository.findById(id).orElse(null);

    model.addAttribute("document", document);

    return "admin/editDocument";
  }

  // delete document
  @GetMapping("/deleteDocument/{id}")
  public String deleteDocument(@PathVariable("id") Long id, Model model) {
    documentRepository.deleteById(id);
    model.addAttribute("message", "Delete successful!");

    return "redirect:/admin/documents";
  }

  @InitBinder
  public void initBinder(WebDataBinder binder) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    sdf.setLenient(true);
    binder.registerCustomEditor(Date.class, new CustomDateEditor(sdf, true));
  }

}
