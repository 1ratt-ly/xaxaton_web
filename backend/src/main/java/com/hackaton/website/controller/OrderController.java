package com.hackaton.website.controller;

import com.hackaton.website.dto.ImportJobResponse;
import com.hackaton.website.dto.OrderRequest;
import com.hackaton.website.dto.OrderResponse;
import com.hackaton.website.entity.Order;
import com.hackaton.website.repository.OrderRepository;
import com.hackaton.website.service.csv.ImportJobService;
import com.hackaton.website.service.csv.ImportJobStatus;
import com.hackaton.website.service.tax.TaxEngineService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderRepository orderRepository;
    private final TaxEngineService taxEngineService;
    private final ImportJobService importJobService;

    public OrderController(OrderRepository orderRepository,
                           TaxEngineService taxEngineService,
                           ImportJobService importJobService) {
        this.orderRepository = orderRepository;
        this.taxEngineService = taxEngineService;
        this.importJobService = importJobService;
    }


    @GetMapping
    public List<OrderResponse> getOrders(
            @RequestParam(required = false) String countyName, // <-- Додано фільтр
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "1000") int size
    ) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));

        Page<Order> orderPage;
        if (countyName != null && !countyName.isBlank()) {
            orderPage = orderRepository.findByCountyNameContainingIgnoreCase(countyName.trim(), pageable);
        } else {
            orderPage = orderRepository.findAll(pageable);
        }

        return orderPage.getContent().stream()
                .map(OrderResponse::from)
                .toList();
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse create(@Valid @RequestBody OrderRequest req) {
        Order saved = taxEngineService.createAndSaveOrder(
                req.latitude(),
                req.longitude(),
                req.subtotal(),
                null
        );
        return OrderResponse.from(saved);
    }

    @PostMapping("/import")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ImportJobResponse importCsv(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("file is empty");
        }
        ImportJobStatus status = importJobService.startAsync(file);
        return ImportJobResponse.from(status);
    }

    @GetMapping("/import/{jobId}")
    public ImportJobResponse importStatus(@PathVariable UUID jobId) {
        ImportJobStatus s = importJobService.get(jobId);
        if (s == null) throw new IllegalArgumentException("job not found: " + jobId);
        return ImportJobResponse.from(s);
    }
}