package com.example.demo.entity;

import com.example.demo.dto.ChartDTO;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@SqlResultSetMappings(
		value = {
				@SqlResultSetMapping(
						name = "chartCategoryDTO",
						classes = @ConstructorResult(
								targetClass = ChartDTO.class,
								columns = {
										@ColumnResult(name = "label",type = String.class),
										@ColumnResult(name = "value",type = Integer.class)
								}
						)
				)
		}
)

@NamedNativeQuery(
		name = "getProductOrderCategories",
		resultSetMapping = "chartCategoryDTO",
		query = "select c.category_name as label, count(od.quantity) as value from categories c " +
				"inner join products p on p.category_id = c.category_id " +
				"inner join order_details od on od.product_id = p.product_id " +
				"inner join orders o on od.order_id = o.order_id " +
				"where o.status = 2 " +
				"group by c.category_id "
)

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "categories")
public class Category implements Serializable{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long categoryId;
	private String categoryName;
	private String categoryImage;
}
