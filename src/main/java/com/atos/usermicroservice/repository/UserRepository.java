package com.atos.usermicroservice.repository;
import com.atos.usermicroservice.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
