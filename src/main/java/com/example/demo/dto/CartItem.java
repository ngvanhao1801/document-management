package com.example.demo.dto;

import com.example.demo.entity.Product;
import lombok.Data;

@Data
public class CartItem {

  private Long id;

  private String name;

  private double unitPrice;

  private int quantity;

  private double totalPrice;

  private Product product;

}
