package com.github.supercodingteam1.web.dto;


import lombok.*;

import java.util.List;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResTokenDTO {


  private String user_token;
  private String user_refreshtoken;
  private String role;
  //private List<String> role;

}
