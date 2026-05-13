package com.Foodie.authentivation_service.repository;


import com.Foodie.authentivation_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {

    Optional<User> findUserByEmail(String email);

    Optional<User> getUserByIdAndDeletedFalse(Integer id);

    boolean existsByUserName(String userName);

    boolean existsByEmail(String email);
}
