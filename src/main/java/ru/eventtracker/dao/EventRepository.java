package ru.eventtracker.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.eventtracker.entity.Category;
import ru.eventtracker.entity.Event;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByCategory_Name(String name);

    List<Event> findByUser_Email(String email);

}
