package com.example.shop.entity;

import com.example.shop.constant.ItemSellStatus;
import com.example.shop.repository.ItemRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/*
    QueryDsl Test
 */

@SpringBootTest
@Slf4j
class ItemTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    EntityManager em;

    JPAQueryFactory queryFactory;

    @BeforeEach
    void setUp() {
        queryFactory = new JPAQueryFactory(em);
    }

    @Test
    public void testFindByItemNm(){
        List<Item> items = itemRepository.findByItemNm("과자");

        items.forEach(item -> log.info(item.toString()));
        //items.forEach(System.out::println); -> 메서드 참조

        log.info("----------------QueryDSL------------------");

        QItem qItem = QItem.item;

        List<Item> item2 = queryFactory
                //.selectFrom(qItem) -> select와 from 한 번에 작성O
                .select(qItem)
                .from(qItem)
                .where(qItem.itemNm.eq("과자"))
                .fetch();

        item2.forEach(item -> log.info(item.toString()));


    }

    //AND 조건 검색
    @Test
    public void testFindByItemNmAndPrice(){
        QItem qItem = QItem.item;

        List<Item> item = queryFactory
                .selectFrom(qItem)
                .where(
                        qItem.itemNm.eq("과자"),
                        qItem.price.gt(3000)
                )
                .fetch();

        log.info(item.toString());
    }

    //OR 조건 검색
    @Test
    public void testFindByItemNmOrItemDetail(){
        QItem qItem = QItem.item;

        List<Item> items = queryFactory
                .select(qItem)
                .from(qItem)
                .where(
                        qItem.itemNm.contains("과자")
                                .or(qItem.itemDetail.contains("입니다"))
                )
                .fetch();

        items.forEach(item -> log.info(item.toString()));
    }

    //Enum 조건 검색
    @Test
    public void testFindBySellStatus(){
        QItem qItem = QItem.item;

        List<Item> items = queryFactory
                .selectFrom(qItem)
                .where(qItem.itemSellStatus.eq(ItemSellStatus.SOLD_OUT))
                .fetch();

        items.forEach(item -> log.info(item.toString()));
    }

    //동적 조건 검색(BooleanBuilder 사용)
    @Test
    public void testDynamicSearch(){
        QItem qItem = QItem.item;
        BooleanBuilder builder = new BooleanBuilder();

        String searchNm = "자바";
        Integer minPrice = 3000;

        if(searchNm != null){
            builder.and(qItem.itemNm.contains(searchNm));
        }

        if (minPrice != null){
            builder.and(qItem.price.gt(minPrice));
        }

        List<Item> items = queryFactory
                .selectFrom(qItem)
                .where(builder)
                .fetch();

        items.forEach(item -> log.info(item.toString()));


    }

    //정렬
    @Test
    public void testPaging(){
        QItem qItem = QItem.item;

        List<Item> items = queryFactory
                .selectFrom(qItem)
                .where(qItem.price.gt(1000))
                .orderBy(qItem.price.asc())
                .fetch();

        log.info(items.toString());
    }

    //정렬 + 페이징 처리
    @Test
    public void testPagingAndSort(){
        QItem qItem = QItem.item;

        List<Item> items = queryFactory
                .selectFrom(qItem)
                //.where(qItem.price.gt(1000))
                .orderBy(qItem.id.asc())
                .offset(1)  //시작위치 1번 인덱스부터 (0부터 시작)
                .limit(3)   //최대 3개 가져오기
                .fetch();

        log.info(items.toString());
    }

    //그룹화, 집계함수(count, max, avg 등)
    @Test
    public void testAggregateFunction(){
        QItem qItem = QItem.item;

        List<Tuple> fetch = queryFactory
                .select(
                        qItem.itemSellStatus,
                        qItem.price.avg()
                )
                .from(qItem)
                .groupBy(qItem.itemSellStatus)
                .fetch();

        fetch.stream().forEach(item -> log.info(item.toString()));
    }

    //ItemImg 조회
    @Test
    public void testItemImg(){
        QItemImg qItemImg = QItemImg.itemImg;

        List<ItemImg> result = queryFactory
                .selectFrom(qItemImg)
                .where(qItemImg.repimgYn.eq("Y"))
                .fetch();

        result.forEach(item -> log.info(item.toString()));
    }

    //ItemImg, Item Join
    @Test
    public void testJoin(){
        QItem qItem = QItem.item;
        QItemImg qItemImg = QItemImg.itemImg;

        List<ItemImg> result = queryFactory
                .selectFrom(qItemImg)
                .join(qItemImg.item, qItem)
                .where(qItem.itemNm.contains("자바"))
                .fetch();

        /*
            item과 item_img 테이블 조인 (item_id 기준으로 연결)
            item에서 item_nm에 "자바"가 포함된 경우만 필터링
            필터링된 item_img 테이블의 모든 컬럼 조회

            select i.*
            from item_img i
            join item t on i.item_id = t.item_id
            where t.item_nm like '%자바%';
         */

        log.info(result.toString());
    }
}