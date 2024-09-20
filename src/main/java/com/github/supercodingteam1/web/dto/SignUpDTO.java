package com.github.supercodingteam1.web.dto;

import com.github.supercodingteam1.repository.entity.user.Role;
import com.github.supercodingteam1.repository.entity.user.User;
import com.github.supercodingteam1.repository.entity.user.UserRole;
import com.github.supercodingteam1.service.UserRoleService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpDTO {

  @NotEmpty(message = "유저 이름은 필수입니다.")
  @Size(min = 2, max = 20, message = "유저 이름은 2자에서 20자 사이여야 합니다.")
  private String user_name;

  @NotEmpty(message = "이메일은 필수입니다.")
  @Email(message = "유효한 이메일 주소를 입력해 주세요.")
  private String user_email;

  @NotEmpty(message = "패스워드는 필수입니다.")
  @Size(min = 4, max = 15, message = "패스워드는 4자에서 15자 사이여야 합니다.")
  private String user_password;

  @NotEmpty(message = "전화번호는 필수입니다.")
  @Size(min = 10, max = 15, message = "전화번호는 10자에서 15자 사이여야 합니다.")
  private String user_phone;

  private String user_profile;

  private Role[] roles;


  /**
   * SignUpDTO를 User 엔티티로 변환하는 메서드
   * @param signUpDTO 회원가입 DTO
   * @return User 엔티티
   */
  public static User toCreateUser(SignUpDTO signUpDTO, UserRoleService userRoleService) {

    List<UserRole> userRoles=null;
    if(signUpDTO.getRoles()==null || signUpDTO.getRoles().length == 0) {
          userRoles = new ArrayList<>();
          UserRole userRole1 = userRoleService.findByRoleName(Role.BUYER);
          if (userRole1 == null) {
            userRole1 = new UserRole();
            userRole1.setRoleName(Role.SELLER);
            userRole1 = userRoleService.save(userRole1);
          }
           userRoles.add(userRole1);
//          UserRole userRole2 = userRoleService.findByRoleName(Role.SELLER);
//          if (userRole2 == null) {
//            userRole2 = new UserRole();
//            userRole2.setRoleName(Role.SELLER);
//            userRole2 = userRoleService.save(userRole2);
//          }
//          userRoles.add(userRole2);

    }else{
      userRoles = Arrays.stream(signUpDTO.getRoles())
              .map(roleName -> {
                UserRole userRole = userRoleService.findByRoleName(roleName); // 기존 Role을 조회
                if (userRole == null) {
                  userRole = new UserRole();
                  userRole.setRoleName(roleName);
                  userRole = userRoleService.save(userRole);
                }
                return userRole;
              })
              .collect(Collectors.toList());
    }


    return User.builder()
        .userName(signUpDTO.getUser_name())
        .email(signUpDTO.getUser_email())
        .password(signUpDTO.getUser_password())
        .phoneNum(signUpDTO.getUser_phone())
        .userAddress(signUpDTO.getUser_profile())
        .userGender("")
        .user_role(userRoles)
        .build();
  }
}
