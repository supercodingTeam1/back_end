package com.github.supercodingteam1.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.supercodingteam1.config.auth.dto.TokenDTO;
import com.github.supercodingteam1.repository.entity.user.User;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Integer userId;
    private String userName;

    @JsonIgnore
    private String email;
    @JsonIgnore
    private TokenDTO token;
    @JsonIgnore
    private String phoneNum;
    @JsonIgnore
    private String userAddress;
    @JsonIgnore
    private String userGender;
    @JsonIgnore
    private String userImg;


    private List<String> user_role;



    public static UserDTO of(User user){
        List<String> collect = user.getUser_role().stream().map(userRole -> userRole.getRoleName().toString()).collect(Collectors.toList());

        return  UserDTO.builder().
                userId(user.getUserId())
                .userName(user.getUserName())
                .email(user.getEmail())
                .phoneNum(user.getPhoneNum())
                .userAddress(user.getUserAddress())
                .userGender(user.getUserGender())
                .user_role(collect)
                .userImg(user.getUserImg())
                .build();
    }


}
