package com.example.shop.repository;

import com.example.shop.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository  extends JpaRepository<Cart, Long> {

    //로그인한 회원의 장바구니(Cart) DB에서 조회
    Cart findByMemberId(Long memberId);
}
