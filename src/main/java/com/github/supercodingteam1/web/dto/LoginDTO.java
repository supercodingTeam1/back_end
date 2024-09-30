package com.github.supercodingteam1.web.dto;

import com.github.supercodingteam1.repository.entity.user.User;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class LoginDTO {

    @NotEmpty(message = "이메일은 필수입니다.")
    private String user_email;

    @NotEmpty(message = "패스워드는 필수입니다.")
    private String user_password;


    public static User toLoginUser(LoginDTO loginDTO) {
        return User.builder()
                .email(loginDTO.getUser_email())
                .password(loginDTO.getUser_password())
                .build();
    }
}
