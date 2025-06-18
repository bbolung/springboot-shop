package com.example.shop.repository;

import com.example.shop.constant.ItemSellStatus;
import com.example.shop.entity.Item;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Test
    @DisplayName("상품 저장 테스트")
    public void createItemTest(){

        Item item = new Item();

        item.setItemNm("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("테스트 상품 상세 설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        item.setRegTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());

        Item savedItem = itemRepository.save(item);

//        log.info("savedItem : {}", savedItem.toString());
    }

    public void createItemList(){

        for(int i=1; i<=10; i++){

            Item item = new Item();

            item.setItemNm("테스트 상품" + i);
            item.setPrice(10000 + i);
            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setStockNumber(100);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());

            Item savedItem = itemRepository.save(item);
        }
    }

    @Test
    @DisplayName("상품명 조회 테스트")
    public void findByItemTest(){
        this.createItemList();

        List<Item> itemList = itemRepository.findByItemNm("테스트 상품1");
        itemList.forEach(item -> log.info("item: {}", item.toString()));
    }

    @Test
    @DisplayName("상품명 LIKE 조회 테스트")
    public void findByItemLikeTest(){
        this.createItemList();

        List<Item> itemList = itemRepository.findByItemNmLike("%테스트 상품1%");
        itemList.forEach(item -> log.info("item: {}", item.toString()));
    }

    @Test
    @DisplayName("가격 조회 테스트")
    public void findByPriceLessThanTest(){
        this.createItemList();

        List<Item> itemList = itemRepository.findByPriceLessThan(10005);
        itemList.forEach(item -> log.info("item: {}", item.toString()));
    }

    @Test
    @DisplayName("@Query를 이용한 상품 조회 테스트")
    public void findByItemDetailTest(){
        this.createItemList();

        List<Item> itemList = itemRepository.findByItemDetail("설명1");
        itemList.forEach(item -> log.info("item: {}", item.toString()));
    }

    @Test
    @DisplayName("@Query Native를 이용한 상품 조회 테스트")
    public void findByItemDetailByNativeTest(){
        this.createItemList();

        List<Item> itemList = itemRepository.findByItemDetailByNative("설명1");
        itemList.forEach(item -> log.info("item: {}", item.toString()));
    }
}