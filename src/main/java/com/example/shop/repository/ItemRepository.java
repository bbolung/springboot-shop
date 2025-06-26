package com.example.shop.repository;

import com.example.shop.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

                                                    //클래스이름, 기본키 타입
public interface ItemRepository extends JpaRepository<Item, Long>,
                                        QuerydslPredicateExecutor<Item> {

    List<Item> findByItemNm(String itemNm);         //itemNm 일치하는 경우만 조회

    List<Item> findByItemNmLike(String itemNm);     //itemNm이 포함되는 경우 모두 조회
    
    List<Item> findByPriceLessThan(int price);      //price보다 작은 경우 조회

    //JPQL : entity 객체 이용
    @Query("select i from Item i where i.itemDetail like %:itemDetail% order by i.price desc")
    List<Item> findByItemDetail(@Param("itemDetail") String itemDetail);

    //nativeQuery : DB table 이용
    @Query(value = "select * from item where item_detail " +
            "like %:itemDetail% order by price desc", nativeQuery = true)
    List<Item> findByItemDetailByNative(@Param("itemDetail") String itemDetail);
}
