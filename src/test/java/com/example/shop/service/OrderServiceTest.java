package com.example.shop.service;

import com.example.shop.dto.OrderDto;
import com.example.shop.dto.OrderHisDto;
import com.example.shop.entity.Member;
import com.example.shop.entity.Order;
import com.example.shop.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
@WithMockUser(username = "test@email.com", roles = "ADMIN")      //로그인한 것처럼 사용 -> 인증 정보 문제 해결
@Transactional      //DB에 반영X
//@Rollback(false)  DB에 반영하고 싶을 때
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    @Transactional      //Order와 OrderItem의 Session문제 해결(하나의 사이클)
    public void testCreateOrder(){

        String email = "test@email.com";

        OrderDto orderDto = new OrderDto();

        orderDto.setCount(2);
        orderDto.setItemId(3L);

        Long order = orderService.order(orderDto, email);

        log.info("-------order------ : {}", order);

        Order savedOrder = orderRepository.findById(order)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        log.info("-------savedOrder------ : {}", savedOrder);

        savedOrder.getOrderItems().forEach(orderItem -> {log.info("Orderitem : {}", orderItem);});
    }

    @Test
    @Transactional
    @DisplayName("주문 이력 내역 코드 이해하기 위한 Test")
    public void getOrderListTest(){

        String email = "test@email.com";

        Pageable pageable = PageRequest.of(0, 5);
        Page<OrderHisDto> orderHisDtoList = orderService.getOrderList(email, pageable);

        orderHisDtoList.getContent().forEach(list -> log.info("list : {}", list));
        log.info("totalCount : {}", orderHisDtoList.getTotalElements());

    }

}