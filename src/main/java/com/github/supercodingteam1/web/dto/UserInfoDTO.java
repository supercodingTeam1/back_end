package com.github.supercodingteam1.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.supercodingteam1.config.auth.dto.TokenDTO;
import com.github.supercodingteam1.repository.entity.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDTO {

    private Integer user_id;
    private String name;
    private List<String> roles;

}
