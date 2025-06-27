package com.example.shop.dto;

import com.example.shop.constant.ItemSellStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter@Setter
public class ItemSearchDto {

    private String searchDateType;      //날짜 조회

    private ItemSellStatus itemSellStatus;      //판매 상태 조회

    private String searchBy;    //검색 기준 드롭 다운 선택 : 상품명(itemNm), 상품 등록자(createBy)

    private String searchQuery = "";    //사용자가 입력한 실제 검색어 (상품명, 상품 등록자 ID)
}
