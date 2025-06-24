package com.example.shop.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;


@Service
@Slf4j
public class FileService {

    public String uploadFile(String uploadPath, String originalFileName,
                             byte[] fileData) throws Exception {

        UUID uuid = UUID.randomUUID();
        
        //sampleTest.jpg에서 .기준으로 추출
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));

        log.info("originalFileName.substring ===> {}", extension);
        
        String savedFileName = uuid.toString() + extension;      //중복파일 덮어씌기 방지
        
        String fileIploadFullUrl = uploadPath + "/" + savedFileName;    //upload경로명
        
        FileOutputStream fos = new FileOutputStream(fileIploadFullUrl);     //경로 생성
        fos.write(fileData);   //실제 저장
        fos.close();
        
        return savedFileName;
    }

    public void deleteFile(String filePath) throws Exception {

        File deleteFile = new File(filePath);

        if(deleteFile.exists()) {
            deleteFile.delete();
            log.info("파일을 삭제하였습니다. : {}", filePath);
        }else {
            log.info("파일이 존재하지 않습니다. : {}", filePath);
        }
    }
}
