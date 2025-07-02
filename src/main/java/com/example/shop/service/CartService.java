package com.example.shop.service;

import com.example.shop.dto.CartDetailDto;
import com.example.shop.dto.CartItemDto;
import com.example.shop.entity.Cart;
import com.example.shop.entity.CartItem;
import com.example.shop.entity.Item;
import com.example.shop.entity.Member;
import com.example.shop.repository.CartItemRepository;
import com.example.shop.repository.CartRepository;
import com.example.shop.repository.ItemRepository;
import com.example.shop.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    //상품 장바구니에 추가
    public Long addCart(CartItemDto cartItemDto, String email) {

        //해당 상품의 정보 DB 조회
        Item item = itemRepository.findById(cartItemDto.getItemId())
                .orElseThrow(() -> new EntityNotFoundException());

        //해당 사용자의 정보 DB 조회 (장바구니 주인)
        Member member = memberRepository.findByEmail(email);

        //해당 사용자의 장바구니 DB에서 조회
        Cart cart = cartRepository.findByMemberId(member.getId());

        //DB에 사용자의 장바구니 없는 경우 (장바구니 처음 사용) -> Cart 생성
        if(cart == null){
            cart = Cart.create(member);
            cartRepository.save(cart);
        }

        //장바구니 있는 경우, 장바구니 안의 상품과 지금 담은 상품 동일? (DB 조회)
        CartItem savedCartItem = cartItemRepository.findByCartIdAndItemId(cart.getId(), cartItemDto.getItemId());

        //동일 상품 -> 장바구니의 상품 개수만 증가
        if(savedCartItem != null){
            savedCartItem.addCount(cartItemDto.getCount());
            return savedCartItem.getId();
        }else {     //장바구니에 동일 상품X -> 장바구니 상품 객체 생성 + DB 저장
            CartItem cartItem = CartItem.createCartItem(cart, item, cartItemDto.getCount());
            cartItemRepository.save(cartItem);
            return cartItem.getId();
        }
    }

    //장바구니 상품 목록 조회
    @Transactional(readOnly = true)
    public List<CartDetailDto> getCartList(String email) {

        List<CartDetailDto> cartDetailDtoList = new ArrayList<>();

        Member member = memberRepository.findByEmail(email);

        Cart cart = cartRepository.findByMemberId(member.getId());

        if(cart == null){
            return cartDetailDtoList;
        }
        cartDetailDtoList = cartItemRepository.findCartDetailDtoList(cart.getId());

        return cartDetailDtoList;
    }
}
