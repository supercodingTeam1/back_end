package com.github.supercodingteam1.repository.entity.user;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Integer> {

  UserRole findByRoleName(Role roleName);

}
