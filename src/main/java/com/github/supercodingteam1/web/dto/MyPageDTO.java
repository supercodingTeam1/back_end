package com.github.supercodingteam1.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MyPageDTO<T> {

    private Integer status;
    private String message;

    private T data;
}
