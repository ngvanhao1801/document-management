package com.example.demo.controller.admin;

import com.example.demo.entity.StudentInformation;
import com.example.demo.entity.User;
import com.example.demo.repository.StudentInformationRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.StudentInformationService;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class StudentController {

  private final StudentInformationRepository studentInformationRepository;

  private final UserRepository userRepository;

  private final StudentInformationService service;

  public StudentController(StudentInformationRepository studentInformationRepository,
                           UserRepository userRepository,
                           StudentInformationService service) {
    this.studentInformationRepository = studentInformationRepository;
    this.userRepository = userRepository;
    this.service = service;
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

  @ModelAttribute(value = "studentInformations")
  public List<StudentInformation> showStusentList(Model model) {
    List<StudentInformation> studentInformations = studentInformationRepository.findAll();
    model.addAttribute("studentInformations", studentInformations);

    return studentInformations;
  }

  @GetMapping(value = "/students")
  public String students(Model model, Principal principal) {
    StudentInformation studentInformation = new StudentInformation();
    model.addAttribute("studentInformation", studentInformation);

    return "admin/students";
  }

  @GetMapping("/deleteStudent/{id}")
  public String delStudent(@PathVariable("id") Long id, Model model) {
    studentInformationRepository.deleteById(id);

    model.addAttribute("message", "Delete successful!");

    return "redirect:/admin/students";
  }

  @GetMapping(value = "/editStudent/{id}")
  public String editStudent(@PathVariable("id") Long id, ModelMap model) {
    StudentInformation studentInformation = studentInformationRepository.findById(id).orElse(null);

    model.addAttribute("studentInformation", studentInformation);

    return "admin/edit-student";
  }

  @PostMapping(value = "/addStudent")
  public String addStudent(@Validated @ModelAttribute("student") StudentInformation studentInformation,
                           ModelMap model,
                           BindingResult bindingResult) {

    if (bindingResult.hasErrors()) {
      model.addAttribute("error", "failure");

      return "admin/students";
    }
    studentInformation.setBirthday(studentInformation.getBirthday());
    studentInformation.setGender(studentInformation.getGender());

    studentInformationRepository.save(studentInformation);
    model.addAttribute("message", "successful!");

    return "redirect:/admin/students";
  }

  @PostMapping("/students/import")
  public String importStudents(@RequestParam("file") MultipartFile file) {
    this.service.saveStudentToDatabase(file);

    return "redirect:/admin/students";
  }

  @InitBinder
  public void initBinder(WebDataBinder binder) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    sdf.setLenient(true);
    binder.registerCustomEditor(Date.class, new CustomDateEditor(sdf, true));
  }

}
