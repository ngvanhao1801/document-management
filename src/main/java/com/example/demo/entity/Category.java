package com.example.demo.entity;

import com.example.demo.dto.ChartDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@SqlResultSetMappings(
		value = {
				@SqlResultSetMapping(
						name = "chartCategoryDTO",
						classes = @ConstructorResult(
								targetClass = ChartDTO.class,
								columns = {
										@ColumnResult(name = "label", type = String.class),
										@ColumnResult(name = "value", type = Integer.class)
								}
						)
				)
		}
)

@NamedNativeQuery(
		name = "getProductOrderCategories",
		resultSetMapping = "chartCategoryDTO",
		query = "select c.category_name as label, count(d.id) as value from categories c\n" +
				"inner join folder f on f.category_id = c.category_id \n" +
				"inner join document d on d.folder_id = f.folder_id\n" +
				"where d.status_id = 3\n" +
				"group by c.category_id;"
)

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "categories")
public class Category implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long categoryId;

	private String categoryName;

	private String categoryImage;
}
