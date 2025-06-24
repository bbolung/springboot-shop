package com.example.shop.dto;

import com.example.shop.constant.ItemSellStatus;
import com.example.shop.entity.Item;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter@Setter
public class ItemFormDto {

    private Long id;    //상품 코드

    @NotBlank(message = "상품명은 필수 입력 값입니다.")
    private String itemNm;     //상품명

    @NotNull(message = "가격은 필수 입력 값입니다.")
    private Integer price;      //상품 가격

    @NotBlank(message = "재고는 필수 입력 값입니다.")
    private String stockNumber;  //재고 수량

    @NotNull(message = "상세설명은 필수 입력 값입니다.")
    private String itemDetail;  //상품 상세 설명

    private ItemSellStatus itemSellStatus;      //상품 판매 상태

    private List<ItemImgDto> itemImgDtoList = new ArrayList<>();    //이미지가 여러 장이니까 List로 받음

    private List<Long> itemImgId = new ArrayList<>();   //itemImgId값 따로 받음

    private static ModelMapper modelMapper = new ModelMapper();
    
    //ItemFormDto(자기자신) -> Item으로 변환
    public Item createItem(){
        return modelMapper.map(this, Item.class);
    }

    //Item -> ItemFormDto 변환
    public static ItemFormDto of(Item item) {
        return modelMapper.map(item, ItemFormDto.class);
    }
}
