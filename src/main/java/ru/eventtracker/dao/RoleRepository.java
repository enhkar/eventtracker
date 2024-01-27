package ru.eventtracker.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.eventtracker.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByName(String name);
}
