package com.example.demo.entity;

import com.example.demo.dto.ChartDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SqlResultSetMapping(
    name = "ChartMapping",
    classes = @ConstructorResult(
        targetClass = ChartDTO.class,
        columns = {
            @ColumnResult(name = "label", type = String.class),
            @ColumnResult(name = "value", type = Integer.class)
        }
    )
)

@NamedNativeQuery(
    name = "getDocumentFavourite",
    resultSetMapping = "ChartMapping",
    query = "SELECT d.document_name AS label, d.views AS value\n" +
        "FROM document d\n" +
        "WHERE d.status_id = 3\n" +
        "GROUP BY d.document_name\n" +
        "ORDER BY d.views DESC \n" +
        "LIMIT 5"
)

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "document")
public class Document implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String documentName;

  @Column(name = "description",columnDefinition = "TEXT")
  private String description;

  @ManyToOne
  @JoinColumn(name = "userId")
  private User user;

  @Temporal(TemporalType.DATE)
  private Date uploadDate;

  @ManyToOne
  @JoinColumn(name = "folderId")
  private Folder folder;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "statusId")
  private DocumentStatus documentStatus;

  private String documentImage;

  private String documentFile;

  private String mediaType;

  private int favorites;

  @Transient
  public boolean favorite;

  private int version;

  private int views;

  @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Feedback> feedbacks = new ArrayList<>();

}
