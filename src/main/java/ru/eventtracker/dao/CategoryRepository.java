package ru.eventtracker.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.eventtracker.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Category findByName(String name);
}
