package com.example.shop.service;

import com.example.shop.dto.ItemFormDto;
import com.example.shop.dto.ItemImgDto;
import com.example.shop.dto.ItemSearchDto;
import com.example.shop.dto.MainItemDto;
import com.example.shop.entity.Item;
import com.example.shop.entity.ItemImg;
import com.example.shop.repository.ItemImgRepository;
import com.example.shop.repository.ItemRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemImgRepository itemImgRepository;
    private final ItemImgService itemImgService;

    public Long saveItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception {

        //상품 등록
        Item item = itemFormDto.createItem();
        itemRepository.save(item);

        //이미지 등록
        for(MultipartFile multipartFile : itemImgFileList) {
            ItemImg itemImg = new ItemImg();

            itemImg.setItem(item);

            if(itemImgFileList.get(0).equals(multipartFile)){
                itemImg.setRepimgYn("Y");   //첫 번째 이미지를 대표 이미지로 설정
            }else {
                itemImg.setRepimgYn("N");
            }

            itemImgService.saveItemImg(itemImg, multipartFile);
        }

        return item.getId();
    }

    //상품 수정 페이지 (기존 데이터 조회)
    @Transactional(readOnly = true)     //JPA가 dirtyChecking(변경감지) 수행하지X
    public ItemFormDto getItemDtl(Long itemId){
        //상품 이미지 조회 -> ItemFormDto에 저장

        List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId);

        //ItemImg 엔티티 -> ItemImgDto 변환 = itemImgList
        List<ItemImgDto> itemImgDtoList = new ArrayList<>();
        for(ItemImg itemImg : itemImgList) {
            ItemImgDto itemImgDto = ItemImgDto.of(itemImg);
            itemImgDtoList.add(itemImgDto);
        }

        //상품 정보 조회 -> ItemFormDto에 저장
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("해당 데이터 존재하지 않습니다."));

        //Item 엔티티 -> ItemFromDto로 변환 = itemFormDto
        ItemFormDto itemFormDto = ItemFormDto.of(item);

        //상품 정보 담긴 itemFormDto에 상품 이미지 itmeImgDtoList도 추가
        itemFormDto.setItemImgDtoList(itemImgDtoList);

        return itemFormDto;
    }

    //상품 수정 등록
    public Long updateItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception {

        //상품 정보 조회
        Item item = itemRepository.findById(itemFormDto.getId())
                .orElseThrow(() -> new EntityNotFoundException());

        //상품 정보 수정
        item.updateItem(itemFormDto);

        //상품 이미지 수정
        List<Long> itemImfIds = itemFormDto.getItemImgId();

        log.info("=======> : {}", itemImfIds);

        for(int i=0; i<itemImfIds.size(); i++){
            itemImgService.updateItemImg(itemImfIds.get(i), itemImgFileList.get(i));
        }

        return item.getId();
    }

    @Transactional(readOnly = true)     //데이터 변경X 설정
    public Page<Item> getAdminItemPage(ItemSearchDto searchDto, Pageable pageable){
        return itemRepository.getAdminItemPage(searchDto, pageable);
    }

    @Transactional(readOnly = true)
    public Page<MainItemDto> getMainItemPage(ItemSearchDto searchDto, Pageable pageable){
        return itemRepository.getMainItemPage(searchDto, pageable);
    }
}
