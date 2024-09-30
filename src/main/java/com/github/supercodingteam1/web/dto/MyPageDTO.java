package com.github.supercodingteam1.web.dto;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Setter
@Builder
public class MyPageDTO<T> {

    private Integer status;
    private String message;
    private T data;

    public void setData(T data) {
        this.data = (T) data;
    }
}
