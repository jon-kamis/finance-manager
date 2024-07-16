package com.kamis.financemanager.database.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kamis.financemanager.database.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

	/**
	 * Fetch a User by its username
	 * @param username the username of the user to search for
	 * @return An Optional User object containing the user with the given username if one exists
	 */
	public Optional<User> findByUsername(String username);
	
	/**
	 * Fetch a User by its id
	 * @param id the id of the user to search for
	 * @return An Optional User object containing the user with the given id if one exists
	 */
	public Optional<User> findById(int id);
	
	/**
	 * Counts the number of users with a given username
	 * @param username The username of the user to search for
	 * @return An integer representing the number of users with the given username
	 */
	public int countByUsername(String username);
	
	/**
	 * Counts the number of users with a given email
	 * @param email The email of the user to search for
	 * @return An integer representing the number of users with the given email
	 */
	public int countByEmail(String email);
}
