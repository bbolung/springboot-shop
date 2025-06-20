package com.example.shop.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    @GetMapping("/admin/item/new")
    public String itemForm(){
        return "/item/itemForm";
    }
}
