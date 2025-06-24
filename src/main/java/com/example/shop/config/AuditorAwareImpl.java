package com.example.shop.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Slf4j
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        log.info("Current user: {}", authentication.getName());     //확인용
        
        String userID = "";
        
        if(authentication != null){     //로그인 했음
            userID = authentication.getName();
        }
        
        return Optional.of(userID);
    }
}
