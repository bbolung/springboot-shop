package com.example.shop.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberFormDto {

    private String name;
    private String email;
    private String password;
    private String address;
}
