package com.github.supercodingteam1.repository.user;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "items")
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name="password", nullable = false)
    private String password;

    @Column(name = "phone_num", nullable = false)
    private String PhoneNum;

    @Column(name = "user_address", nullable = false)
    private String userAddress;

    @Column(name = "user_gender", nullable = false)
    private String userGender;

    @Column(name = "role", nullable = false)
    private String role;

    @Column(name="profile_img",nullable = false)
    private String profileImg;

}
