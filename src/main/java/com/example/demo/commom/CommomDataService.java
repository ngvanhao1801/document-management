package com.example.demo.commom;

import com.example.demo.entity.CartItem;
import com.example.demo.entity.Order;
import com.example.demo.entity.User;
import com.example.demo.repository.DocumentRepository;
import com.example.demo.repository.FavoriteRepository;
import com.example.demo.repository.FolderRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

@Service
public class CommomDataService {
	
	@Autowired
	FavoriteRepository favoriteRepository;
	
	@Autowired
	ShoppingCartService shoppingCartService;
	
	@Autowired
	ProductRepository productRepository;

	private final DocumentRepository documentRepository;

	private final FolderRepository folderRepository;

	@Autowired
	public JavaMailSender emailSender;
	
	@Autowired
	TemplateEngine templateEngine;

	public CommomDataService(DocumentRepository documentRepository, FolderRepository folderRepository) {
		this.documentRepository = documentRepository;
		this.folderRepository = folderRepository;
	}

	public void commonData(Model model, User user) {
		listFolderByDocumentName(model);
		Integer totalSave = 0;
		// get count yêu thích
		if (user != null) {
			totalSave = favoriteRepository.selectCountSave(user.getUserId());
		}

		Integer totalCartItems = shoppingCartService.getCount();

		model.addAttribute("totalSave", totalSave);

		model.addAttribute("totalCartItems", totalCartItems);

		Collection<CartItem> cartItems = shoppingCartService.getCartItems();
		model.addAttribute("cartItems", cartItems);

	}
	
	// count product by category
	public void listFolderByDocumentName(Model model) {

		List<Object[]> countDocumentByFolder = documentRepository.listFolderByDocumentName();
		model.addAttribute("countDocumentByFolder", countDocumentByFolder);
	}
	
	//sendEmail by order success
	public void sendSimpleEmail(String email, String subject, String contentEmail, Collection<CartItem> cartItems,
			double totalPrice, Order orderFinal) throws MessagingException {
		Locale locale = LocaleContextHolder.getLocale();

		// Prepare the evaluation context
		Context ctx = new Context(locale);
		ctx.setVariable("cartItems", cartItems);
		ctx.setVariable("totalPrice", totalPrice);
		ctx.setVariable("orderFinal", orderFinal);
		// Prepare message using a Spring helper
		MimeMessage mimeMessage = emailSender.createMimeMessage();
		MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, "UTF-8");
		mimeMessageHelper.setSubject(subject);
		mimeMessageHelper.setTo(email);
		// Create the HTML body
		String htmlContent = "";
		htmlContent = templateEngine.process("mail/email_en.html", ctx);
		mimeMessageHelper.setText(htmlContent, true);

		// Send Message!
		emailSender.send(mimeMessage);

	}
}
