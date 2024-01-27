package ru.eventtracker.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.eventtracker.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

    User findByName(String name);
}
