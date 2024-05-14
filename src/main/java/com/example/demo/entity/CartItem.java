package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CartItem {

	private Long id;

	private String name;

	private double unitPrice;

	private int quantity;

	private double totalPrice;

	private Product product;
}
