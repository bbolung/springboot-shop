package com.example.shop.service;

import com.example.shop.dto.CartItemDto;
import com.example.shop.entity.CartItem;
import com.example.shop.repository.CartItemRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@WithMockUser(username = "test@email.com")
@Slf4j
class CartServiceTest {

    @Autowired
    private CartService cartService;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Test
    public void testAddCart() {
        //given
        String email = "test@email.com";

        CartItemDto cartItemDto = new CartItemDto();

        cartItemDto.setItemId(1L);
        cartItemDto.setCount(3);

        //when
        Long result = cartService.addCart(cartItemDto, email);

        //then
        log.info("result >>>>> {}", result);

        //result 상세 정보 확인용
        //then
        CartItem cartItem = cartItemRepository.findById(result)
                .orElseThrow(() -> new EntityNotFoundException());

        //test용 id와 장바구니 ItemId 동일한지?
        assertEquals(cartItem.getItem().getId(), 1L);

        log.info("cartItemId >>>>> {}", cartItem.getItem().getId());
        log.info("cartItem >>>>> {}", cartItem.getItem());
    }
}