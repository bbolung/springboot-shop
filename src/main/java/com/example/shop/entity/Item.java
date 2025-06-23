package com.example.shop.entity;

import com.example.shop.constant.ItemSellStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "item")
@Getter
@ToString
@Setter
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;            //상품코드

    @Column(nullable = false, length = 50)  //Not Null, 최대 50까지 입력 가능
    private String itemNm;      //상품명

    @Column(nullable = false, name = "price")
    private int price;          //가격

    @Column(nullable = false)
    private int stockNumber;    //재고수량

    @Lob
    @Column(nullable = false)
    private String itemDetail;  //상품 상세 설명

    @Enumerated(EnumType.STRING)    //항상 STRING 사용
    private ItemSellStatus itemSellStatus;     //상품 판매 상태

    private LocalDateTime regTime;      //등록 시간

    private LocalDateTime updateTime;   //수정 시간

}
