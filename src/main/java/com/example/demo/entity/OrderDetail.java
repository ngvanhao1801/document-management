package com.example.demo.entity;

import com.example.demo.dto.OrderDetailDTO;
import com.example.demo.dto.OrderDetailsInfoDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@SqlResultSetMappings(
		value = {
				@SqlResultSetMapping(
						name = "orderInfoDTO",
						classes = @ConstructorResult(
								targetClass = OrderDetailsInfoDTO.class,
								columns = {
										@ColumnResult(name = "id", type = Long.class),
										@ColumnResult(name = "total_price", type = Long.class),
										@ColumnResult(name = "size_vn", type = Integer.class),
										@ColumnResult(name = "product_name", type = String.class),
										@ColumnResult(name = "product_img", type = String.class)
								}
						)
				),
				@SqlResultSetMapping(
						name = "orderDetailDto",
						classes = @ConstructorResult(
								targetClass = OrderDetailDTO.class,
								columns = {
										@ColumnResult(name = "id", type = Long.class),
										@ColumnResult(name = "total_price", type = Long.class),
										@ColumnResult(name = "product_price", type = Long.class),
										@ColumnResult(name = "receiver_name", type = String.class),
										@ColumnResult(name = "receiver_phone", type = String.class),
										@ColumnResult(name = "receiver_address", type = String.class),
										@ColumnResult(name = "status", type = Integer.class),
										@ColumnResult(name = "size_vn", type = Integer.class),
										@ColumnResult(name = "product_name", type = String.class),
										@ColumnResult(name = "product_img", type = String.class)
								}
						)
				)
		}
)
@NamedNativeQuery(
		name = "getListOrderOfPersonByStatus",
		resultSetMapping = "orderInfoDTO",
		query = "SELECT od.id, od.total_price, od.size size_vn, p.name product_name, (p.images ->> '$[0]') as product_img " +
				"FROM orders od " +
				"INNER JOIN product p " +
				"ON od.product_id = p.id " +
				"WHERE od.status = ?1 " +
				"AND od.buyer =?2"
)
@NamedNativeQuery(
		name = "userGetDetailById",
		resultSetMapping = "orderDetailDto",
		query = "SELECT orders.id, orders.total_price, orders.size size_vn, product.name product_name, orders.price as product_price, " +
				"orders.receiver_name, orders.receiver_phone, orders.receiver_address, orders.status, " +
				"product.images ->> \"$[0]\" as product_img " +
				"FROM orders " +
				"INNER JOIN product " +
				"ON orders.product_id = product.id " +
				"WHERE orders.id = ?1 AND orders.buyer = ?2"
)

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "orderDetails")
public class OrderDetail implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long orderDetailId;
	private int quantity;
	private Double price;
	
	@ManyToOne
	@JoinColumn(name = "productId")
	private Product product;
	
	@ManyToOne
	@JoinColumn(name = "orderId")
	private Order order;
}
