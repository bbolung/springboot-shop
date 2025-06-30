package com.example.shop.repository;

import com.example.shop.entity.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    /*
        (join 후 조건 email의 orders 테이블 데이터 전체 조회)
        (orders 테이블에 email이 없기에 Join 필수!)
        select o.*
        from orders o
        join member m
        on o.member_id = m.member_id
        where m.email = "test@email.com"
        order by order_date desc;
     */
    
    //JPQL
    @Query("select o from Order o " +
            "where o.member.email = :email " +
            "order by o.orderDate desc")
    List<Order> findOrders(@Param("email") String email, Pageable pageable);        //해당 email의 주문서 조회 + 페이징 처리

    /*
        select count(o.order_id)
        from orders o
        join member m
        on o.member_id = m.member_id
        where m.email = "test@email.com";
     */
    
    @Query("select count(o) from Order o " +
            "where o.member.email = :email")
    Long countOrders(@Param("email") String email);     //해당 email의 주문 개수
}
