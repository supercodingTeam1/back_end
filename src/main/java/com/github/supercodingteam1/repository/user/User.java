package com.github.supercodingteam1.repository.user;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false, length = 11)
    private Integer userId;

    @Column(name = "user_name", nullable = false, length = 20)
    private String userName;

    @Column(name = "email", nullable = false, length = 50)
    private String email;

    @Column(name="password", nullable = false, length = 20)
    private String password;

    @Column(name = "phone_num", nullable = false, length = 15)
    private String phoneNum;

    @Column(name = "user_address", nullable = false, length = 200)
    private String userAddress;

    @Column(name = "user_gender", nullable = false, length = 2)
    private String userGender;

    @Column(name = "user_role", nullable = false, length = 30)
    private String user_role;

    @Column(name="user_img",nullable = false, length = 255)
    private String userImg;

}
