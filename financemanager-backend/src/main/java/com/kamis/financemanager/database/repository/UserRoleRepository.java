package com.kamis.financemanager.database.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kamis.financemanager.database.domain.UserRole;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Integer>{

	/**
	 * Fetches a list of UserRoles by a user's id
	 * @param userId The id of the user to search for
	 * @return a List of UserRole objects for the given userId
	 */
	public List<UserRole> findByUserId(Integer userId);
}
