package com.example.demo.entity;

import com.example.demo.dto.ChartDTO;
import com.example.demo.dto.ProductInfoDTO;
import com.example.demo.dto.ShortProductInfoDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@SqlResultSetMappings(
		value ={
				@SqlResultSetMapping(
						name ="productInfoDto",
						classes =@ConstructorResult(
								targetClass = ProductInfoDTO.class,
								columns ={
										@ColumnResult(name = "id", type = String.class),
										@ColumnResult(name = "name", type = String.class),
										@ColumnResult(name = "slug", type = String.class),
										@ColumnResult(name = "price", type = Long.class),
										@ColumnResult(name = "views",type = Integer.class),
										@ColumnResult(name = "images", type = String.class),
										@ColumnResult(name = "total_sold", type = Integer.class)
								}
						)
				),
				@SqlResultSetMapping(
						name = "shortProductInfoDTO",
						classes = @ConstructorResult(
								targetClass = ShortProductInfoDTO.class,
								columns = {
										@ColumnResult(name = "id", type = String.class),
										@ColumnResult(name = "name", type = String.class)
								}
						)
				),
				@SqlResultSetMapping(
						name = "chartProductDTO",
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
		name = "getAllProduct",
		resultSetMapping = "shortProductInfoDTO",
		query = "SELECT p.id, p.name FROM products p"
)

@NamedNativeQuery(
		name = "getProductOrders",
		resultSetMapping = "chartProductDTO",
		query = "select p.product_name as label, sum(od.quantity) as value from products p " +
				"inner join order_details od on p.product_id = od.product_id " +
				"inner join orders o on o.order_id = od.order_id " +
				"where o.status = 2 and date_format(o.order_date,'%m') = ?1 " +
				"and date_format(o.order_date,'%Y') = ?2 " +
				"group by p.product_id order by sum(od.quantity) desc "
)

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "products")
public class Product implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long productId;
	private String productName;

	private String author;
	private String sizeBook;
	private String typeBook;
	private String codeBook;
	private String supplierName;
	private String publishingCompany;
	private String agePeople;
	private int pageNumber;
	private int yearPublish;
	private int weightBook;

	private int quantity;
	private double price;
	private double entryPrice;
	private int discount;
	private String productImage;

	@Column(name = "description",columnDefinition = "TEXT")
	private String description;

	@Temporal(TemporalType.DATE)
	private Date enteredDate;
	private Boolean status;
	public boolean favorite;

	@ManyToOne
	@JoinColumn(name = "categoryId")
	private Category category;

}
