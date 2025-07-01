package com.example.shop.dto;

import com.example.shop.constant.OrderStatus;
import com.example.shop.entity.Order;
import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Getter@Setter
public class OrderHisDto {

    private Long orderId;       //주문자 아이디
    private String orderDate;   //주문 날짜
    private OrderStatus orderStatus;    //주문 상태

    //생성자 -> order 객체 받아서 변수 값 세팅 + 주문 날짜 fotmat 수정
    public OrderHisDto(Order order) {
        this.orderId = order.getId();
        this.orderDate = order.getOrderDate()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        this.orderStatus = order.getOrderStatus();
    }

    //주문 리스트에 orderItemDto 객체 추가
    private List<OrderItemDto> orderItemDtoList = new ArrayList<>();
    
    public void addOrderItemDto(OrderItemDto orderItemDto) {
        this.orderItemDtoList.add(orderItemDto);
    }
}
