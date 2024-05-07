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
@Table(name = "version")
public class Version {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long versionId;

	private int versionNumber;

	@Temporal(TemporalType.DATE)
	private Date uploadDate;

	@Column(name = "fileUrl", columnDefinition = "TEXT")
	private String fileUrl;

	@ManyToOne
	@JoinColumn(name = "document_id")
	private Document document;

}
