package com.example.shop.service;

import com.example.shop.dto.OrderDto;
import com.example.shop.dto.OrderHisDto;
import com.example.shop.dto.OrderItemDto;
import com.example.shop.entity.*;
import com.example.shop.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final ItemImgRepository itemImgRepository;

    //상품 상세 페이지에서 주문
    //orderDto (맥주, 2병), email(1번 테이블)
    public Long order(OrderDto orderDto, String eamil) {

        //해당 맥주 있는지 확인(상품 정보 확인)
        Item item = itemRepository.findById(orderDto.getItemId())
                .orElseThrow(() -> new EntityNotFoundException());

        //email (1번 테이블)
        Member member = memberRepository.findByEmail(eamil);

        //가게에 존재하는 새 주문서
        List<OrderItem> orderItemList = new ArrayList<>();

        //주문 받음 (맥주, 2병)
        OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());

        orderItemList.add(orderItem);
        
        //주문서에 받은 주문 적음(포스기 입력)
        Order order = Order.createOrder(member, orderItemList);
        
        //주방에 주문 전달됨 = DB에 저장(Order, OrderItem 테이블 저장)
        orderRepository.save(order);

        return order.getId();
    }
    
    //주문 이력 조회
    @Transactional(readOnly = true)
    public Page<OrderHisDto> getOrderList(String email, Pageable pageable) {
        
        //email로 전체 주문서, 주문서 개수 가져옴
        List<Order> orders = orderRepository.findOrders(email, pageable);
        Long totalCount = orderRepository.countOrders(email);

        log.info("주문서들 >>> : {}", orders);
        log.info("총 주문 개수 >>> : {}", totalCount);
        
        //한 번에 주문할 때 여러 상품 주문O -> orderHisDto의 List로 담음
        List<OrderHisDto> orderHisDtoList = new ArrayList<>();
        
        //email로 조회한 주문 상품 내역을 하나씩 저장
        for (Order order : orders) {
            //생성자에 의해 객체 생성됨(order 값 할당) = 주문 내역 목록 하나 생성
            OrderHisDto orderHisDto = new OrderHisDto(order);

            log.info("주문 내역 목록 >>> : {}", orderHisDto);

            //Order 엔티티의 OrdeItems(여러 개의 주문 상품 정보) = orderItems에 저장
            List<OrderItem> orderItems = order.getOrderItems();

            log.info("orderItems >>> {}", orderItems);

            for (OrderItem orderItem : orderItems) {
                //상품 대표 이미지 추출(구매 이력에 보여주기 위해)
                ItemImg itemImg = itemImgRepository
                        .findByItemIdAndRepimgYn(orderItem.getItem().getId(), "Y");

                //OrderItemDto 생성자 사용하여 값(orderItem, ImgUrl) 대입한 객체 생성
                OrderItemDto orderItemDto = new OrderItemDto(
                        orderItem, itemImg.getImgUrl());

                orderHisDto.addOrderItemDto(orderItemDto);
            }
            orderHisDtoList.add(orderHisDto);

        }
        return new PageImpl<>(orderHisDtoList, pageable, totalCount);
    }

    //orderId(주문번호) DB 존재 확인, email(로그인 사용자) vs order의 email 동일 확인(동일 사용자)
    public boolean validateOrder(Long orderId, String email) {

        Member curmember = memberRepository.findByEmail(email);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException());

        Member savedMember = order.getMember();

        if(!StringUtils.equals(curmember.getEmail(), savedMember.getEmail())){
            return false;
        }

        return true;
    }

    //주문 취소
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException());
        order.cancelOrder();
    }

    //장바구니 상품 주문
    public Long orders(List<OrderDto> orderDtoList, String email) {

        Member member = memberRepository.findByEmail(email);

        List<OrderItem> orderItemList = new ArrayList<>();

        for(OrderDto orderDto : orderDtoList){
            Item item = itemRepository.findById(orderDto.getItemId())
                    .orElseThrow(() -> new EntityNotFoundException());

            OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());

            orderItemList.add(orderItem);
        }

        Order order = Order.createOrder(member, orderItemList);

        orderRepository.save(order);

        return order.getId();
    }
}
