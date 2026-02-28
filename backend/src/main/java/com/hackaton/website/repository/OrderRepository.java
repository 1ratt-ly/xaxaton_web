package com.hackaton.website.repository;

import com.hackaton.website.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    // Додаємо метод для фільтрації
    Page<Order> findByCountyNameContainingIgnoreCase(String countyName, Pageable pageable);
}