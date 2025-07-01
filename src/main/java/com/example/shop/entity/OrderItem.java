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
@ToString(exclude = "order")
public class OrderItem extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    //@ToString.Exclude
    private Order order;

    private int orderPrice;     //주문가격
    private int count;          //주문수량

    public static OrderItem createOrderItem(Item item, int count){

        OrderItem orderItem = new OrderItem();

        orderItem.setItem(item);
        orderItem.setCount(count);
        orderItem.setOrderPrice(item.getPrice());

        item.removeStock(count);

        return orderItem;
    }

    //주문할 때마다 총합
    public int getTotalPrice(){
        return this.getOrderPrice() * this.count;
    }

    //주문 취소 시 재고수량 원상복구
    public void cancel(){
        this.getItem().addStock(count);
    }
}
