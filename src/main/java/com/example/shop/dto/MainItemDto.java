package com.example.shop.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter@Setter
@ToString
public class MainItemDto {
    
    private Long id;
    private String itemNm;
    private String itemDetail;
    private String imgUrl;
    private Integer price;

    //@AllArgsConstructor로 생성자 주입하면 @QueryProjection 사용할 수 없기에 생성자 직접 기입
    //Querydsl 조회 결과를 MainItemDto 객체로 전달 받음
    @QueryProjection
    public MainItemDto(Long id, String itemNm, String itemDetail,
                       String imgUrl, Integer price) {
        this.id = id;
        this.itemNm = itemNm;
        this.itemDetail = itemDetail;
        this.imgUrl = imgUrl;
        this.price = price;
    }
}
