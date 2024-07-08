package com.kamis.financemanager.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kamis.financemanager.database.domain.User;

public interface UserRepository extends JpaRepository<User, Integer> {

}
