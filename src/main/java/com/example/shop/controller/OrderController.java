package com.example.shop.controller;

import com.example.shop.dto.OrderDto;
import com.example.shop.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/order")
    public ResponseEntity<?> order(@RequestBody @Valid OrderDto orderDto,
                                BindingResult bindingResult, Principal principal) {

        if (bindingResult.hasErrors()) {
            StringBuilder sb = new StringBuilder();

            List<FieldError> fieldErrors = bindingResult.getFieldErrors();

            for (FieldError fieldError : fieldErrors) {
                sb.append(fieldError.getDefaultMessage());
            }

            //log.info("sb >>>>>>>>>>>>: {}" , sb.toString()); -> 최대 주문 수량은 999개 입니다. (화면에 보이는 에러 메시지)

            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
        }

        String email = principal.getName();
        Long orderId = 0L;

        try{
            orderId = orderService.order(orderDto, email);
        }catch (Exception e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<Long>(orderId, HttpStatus.OK);
    }

}
