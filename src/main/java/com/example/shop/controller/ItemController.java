package com.example.shop.controller;

import com.example.shop.dto.ItemFormDto;
import com.example.shop.dto.ItemSearchDto;
import com.example.shop.entity.Item;
import com.example.shop.service.ItemService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/admin/item/new")
    public String itemForm(Model model) {

        model.addAttribute("itemFormDto", new ItemFormDto());

        return "/item/itemForm";
    }

    @PostMapping("/admin/item/new")
    public String itemNew(@Valid ItemFormDto itemFormDto,
                          BindingResult bindingResult,
                          @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList,
                          Model model) {

        if(bindingResult.hasErrors()){
            return "/item/itemForm";
        }
        
        log.info("itemFormDto ====> {}", itemFormDto);  //Talend API Tester에서 테스트할 때 진입 확인용
        
        //대표 이미지 존재X = 이미지가 존재X
        if(itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null){
           model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력 값입니다.");

           return "/item/itemForm";
        }

        try{
            itemService.saveItem(itemFormDto, itemImgFileList);
        }catch (Exception e){
            model.addAttribute("errorMessage", "상품 등록 중 에러가 발생하였습니다.");
            return "/item/itemForm";
        }

        return "redirect:/";
    }

    //상품 수정 페이지 요청
    @GetMapping(value = "/admin/item/{itemid}")
    public String itemDtl(@PathVariable Long itemid, Model model) {

        try{
            ItemFormDto itemFormDto = itemService.getItemDtl(itemid);
            model.addAttribute("itemFormDto", itemFormDto);
        }catch (EntityNotFoundException e){
            model.addAttribute("errorMessage", "존재하지 않는 상품입니다.");
            model.addAttribute("itemFormDto", new ItemFormDto());
        }

        return "item/itemForm";
    }

    //상품 수정 저장 요청
    @PostMapping(value = "/admin/item/{itemid}")
    public String itemUpdate(@Valid ItemFormDto itemFormDto, BindingResult bindingResult,
                             @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList,
                             Model model) {

        if(bindingResult.hasErrors()){
            return "/item/itemForm";
        }

        if(itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null){
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력 값입니다.");
            return "/item/itemForm";
        }

        try{
            itemService.updateItem(itemFormDto, itemImgFileList);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "상품 수정 중 에러가 발생하였습니다.");
            return "/item/itemForm";
        }

        return "redirect:/";
    }

    @GetMapping(value = {"/admin/items", "/admin/items/{page}"})
    public String itemManage(ItemSearchDto itemSearchDto,
                             @PathVariable("page")Optional<Integer> page, Model model){

        //전달 받은 page 값이 있으면 그 값 사용, 없으면 1번 page당 3개씩 보여줌
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 3);

        Page<Item> items = itemService.getAdminItemPage(itemSearchDto, pageable);

        model.addAttribute("items", items);
        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("maxPage", 5);

        return "item/itemMng";
    }
    
    //상품 상세 페이지 이동 요청
    @GetMapping(value = "/item/{itemId}")
    public String itemDtl2(@PathVariable Long itemId, Model model){
        ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
        
        model.addAttribute("item", itemFormDto);
        
        return "item/itemDtl";
    }

}
