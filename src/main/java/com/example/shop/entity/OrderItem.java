package com.example.shop.entity;

import jakarta.persistence.*;
import lombok.Cleanup;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_item")
@Getter
@Setter
@ToString
public class OrderItem {

    @Id
    @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice;     //주문가격
    private int count;          //주문수량
    
    private LocalDateTime regTime;      //등록 시간
    
    private LocalDateTime updateTime;   //수정 시간
    
}
