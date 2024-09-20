package com.github.supercodingteam1.repository.entity.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "user_roles") // 테이블 이름
public class UserRole {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "role_id", nullable = false)
  private Integer roleId;

  @Enumerated(EnumType.STRING)
  @Column(name = "role_name", nullable = false, length = 20)
  private Role roleName;

  @ManyToMany(mappedBy = "user_role") // User와의 관계 설정
  private Set<User> users;
}


