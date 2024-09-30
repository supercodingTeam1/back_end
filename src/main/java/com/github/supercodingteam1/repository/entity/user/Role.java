package com.github.supercodingteam1.repository.entity.user;

import lombok.*;

@Getter
@AllArgsConstructor
@ToString
@NoArgsConstructor
public enum Role {
  ROLE_BUYER("ROLE_BUYER"),
  ROLE_SELLER("ROLE_SELLER"),
  ROLE_ADMIN("ROLE_ADMIN");

  String role;
}