package com.example.backend.Controller;

import com.example.backend.Dto.OrderDto;
import com.example.backend.Entity.Order;
import com.example.backend.Repository.CartRepository;
import com.example.backend.Repository.OrderRepository;
import com.example.backend.Service.JwtService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class OrderController {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    CartRepository cartRepository;

    @Autowired
    JwtService jwtService;

    @Transactional
    @PostMapping("/api/orders")
    public ResponseEntity pushOrder(@RequestBody OrderDto dto, @CookieValue(value = "token", required = false) String token) {

        if (!jwtService.isValid(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        Order newOrder = new Order();

        newOrder.setMemberId(jwtService.getId(token));
        newOrder.setName(dto.getName());
        newOrder.setAddress(dto.getAddress());
        newOrder.setCardNumber(dto.getCardNumber());
        newOrder.setPayment(dto.getPayment());
        newOrder.setItems(dto.getItems());

        orderRepository.save(newOrder);
        int memberId = jwtService.getId(token);
        cartRepository.deleteByMemberId(memberId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/api/orders")
    public ResponseEntity getOrder(@CookieValue(value = "token", required = false) String token) {

        if (!jwtService.isValid(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        int memberId = jwtService.getId(token);
        List<Order> orders = orderRepository.findByMemberIdOrderByIdDesc(memberId);

        return new ResponseEntity<>(orders, HttpStatus.OK);
    }
}
