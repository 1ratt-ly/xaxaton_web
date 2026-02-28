package com.hackaton.website.controller;

import com.hackaton.website.dto.OrderRequest;
import com.hackaton.website.entity.Order;
import com.hackaton.website.repository.OrderRepository;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:5173") // Дозволяємо фронтенду стукатися сюди
public class OrderController {

    private final OrderRepository orderRepository;

    public OrderController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }


    @GetMapping
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // заглушка
    @PostMapping
    public Order createOrder(@RequestBody OrderRequest request) {
        Order order = new Order();
        order.setLatitude(request.getLatitude());
        order.setLongitude(request.getLongitude());
        order.setSubtotal(request.getSubtotal());

        // фейк податок
        order.setCompositeTaxRate(new BigDecimal("0.08875"));
        order.setTaxAmount(request.getSubtotal().multiply(new BigDecimal("0.08875")));
        order.setTotalAmount(request.getSubtotal().add(order.getTaxAmount()));
        order.setBreakdown("MOCK: State 4%, County 4.875%");

        return orderRepository.save(order);
    }
}