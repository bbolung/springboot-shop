package com.example.shop.entity;

import com.example.shop.constant.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@ToString
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    //회원이 여러 개 주문 가능
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();  //여러 개 전달받을 수 O

    private LocalDateTime orderDate;    //주문일

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;    //주문상태

    //주문 상품 정보들 저장
    public void addOrderitem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }
    
    //주문서(order) 생성
    public static Order createOrder(Member member, List<OrderItem> orderItemList) {
        Order order = new Order();
        order.setMember(member);

        for(OrderItem orderItem : orderItemList) {
            order.addOrderitem(orderItem);
        }

        order.setOrderStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());

        return order;
    }
    
    //주문서의 총 주문 금액
    public int getTotalPrice(){
        int totalPrice = 0;

        //orderItems에 담긴 orderItem 각각의 합계를 더해서 총 주문 금액 totalPrice
        for(OrderItem orderItem : orderItems){
            totalPrice += orderItem.getTotalPrice();
        }
        
        return totalPrice;
    }


}
