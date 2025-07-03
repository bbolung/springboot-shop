package com.example.shop.service;

import com.example.shop.dto.CartDetailDto;
import com.example.shop.dto.CartItemDto;
import com.example.shop.dto.CartOrderDto;
import com.example.shop.dto.OrderDto;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CartService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderService orderService;

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

    //장바구니 페이지의 사용자 vs 로그인한 사용자 동일?
    @Transactional(readOnly = true)
    public boolean  validateCartItem(Long cartItemId, String email) {
        Member member = memberRepository.findByEmail(email);

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException());

        Member savedMember = cartItem.getCart().getMember();

        if(!StringUtils.equals(savedMember.getEmail(), member.getEmail())){
            return false;
        }

        return true;
    }

    //장바구니 수량 변경
    public void updateCartItem(Long cartItemId, int count) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException());

        //cartItem의 스냅샷과 비교하여 값의 변경 감지(dirty checking) -> update구문 실행
        cartItem.setCount(count);

    }

    //장바구니 상품 삭제
    public void deleteCartItem(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException());

        cartItemRepository.delete(cartItem);
    }

    //장바구니 주문 상품 -> 주문 정보 List에 담아서 -> 주문 orders() 호출
    public Long OrderCartItem(List<CartOrderDto> cartOrderDtoList, String email) {

        List<OrderDto> orderDtoList = new ArrayList<>();

        for (CartOrderDto cartOrderDto : cartOrderDtoList) {
            log.info("carOrderDto : {}", cartOrderDto);

            CartItem cartItem = cartItemRepository.findById(cartOrderDto.getCartItemId())
                    .orElseThrow(() -> new EntityNotFoundException());

            OrderDto orderDto = new OrderDto();

            orderDto.setItemId(cartItem.getItem().getId());
            orderDto.setCount(cartItem.getCount());

            orderDtoList.add(orderDto);
        }

        Long orderId = orderService.orders(orderDtoList, email);

        //주문 완료되었으니까 장바구니 목록 비우기
        for(CartOrderDto cartOrderDto : cartOrderDtoList){
            CartItem cartItem = cartItemRepository.findById(cartOrderDto.getCartItemId())
                    .orElseThrow(() -> new EntityNotFoundException());

            cartItemRepository.delete(cartItem);
        }

        return orderId;
    }
}
