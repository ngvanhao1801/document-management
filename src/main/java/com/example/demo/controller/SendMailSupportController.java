package com.example.demo.controller;

import com.example.demo.dto.MailInfo;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.SendMailService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;

@Controller
public class SendMailSupportController {

  private final SendMailService sendMailService;

  private final UserRepository userRepository;

  public SendMailSupportController(SendMailService sendMailService, UserRepository userRepository) {
    this.sendMailService = sendMailService;
    this.userRepository = userRepository;
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

  @PostMapping(value = "/sendEmail")
  public ModelAndView sendEmail(@RequestParam("name") String name,
                                @RequestParam("email") String email,
                                @RequestParam("subject") String subject,
                                @RequestParam("message") String message,
                                ModelMap model) {

    String adminEmail = "ngohao181102@gmail.com";
    String emailSubject = "Yêu cầu hỗ trợ từ " + name + ": " + subject;
    String emailBody = "Tên: " + name + "<br>Email: " + email + "<br>Nội dung: " + message;

    MailInfo mailInfo = new MailInfo(adminEmail, emailSubject, emailBody);
    sendMailService.queue(mailInfo);

    model.addAttribute("name", name);
    model.addAttribute("email", email);
    model.addAttribute("subject", subject);
    model.addAttribute("message", message);

    return new ModelAndView("web/send-mail-success", model);
  }

}
