package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MailInfo {

	String from;

	String to;

	String subject;

	String body;

	String attachments;

	public MailInfo(String to, String subject, String body) {
		this.from = "Document Management <support@gmail.com>";
		this.to = to;
		this.subject = subject;
		this.body = body;
	}
}
