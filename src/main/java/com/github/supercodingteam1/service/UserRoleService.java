package com.github.supercodingteam1.service;

import com.github.supercodingteam1.repository.entity.user.Role;
import com.github.supercodingteam1.repository.entity.user.UserRole;
import com.github.supercodingteam1.repository.entity.user.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.print.attribute.standard.RequestingUserName;

@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class UserRoleService {

  private final UserRoleRepository userRoleRepository;

  public UserRole findByRoleName(Role roleName) {
    return userRoleRepository.findByRoleName(roleName);
  }


  public UserRole save(UserRole userRole) {
    return userRoleRepository.save(userRole);
  }
}
