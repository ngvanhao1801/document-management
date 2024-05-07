package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "pending_document")
public class PendingDocument {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "documentId")
	private Document document;

	@ManyToOne
	@JoinColumn(name = "userId")
	private User user;

	@Temporal(TemporalType.DATE)
	private Date uploadDate;

	private int status;

}
