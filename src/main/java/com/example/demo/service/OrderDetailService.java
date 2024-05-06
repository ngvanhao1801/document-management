package com.example.demo.service;

import com.example.demo.entity.Order;
import com.example.demo.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class OrderDetailService {

	@Autowired
	OrderRepository repo;

	public List<Order> listAll() {
		return (List<Order>) repo.findAll();
	}

}
