package ru.eventtracker.service;

import org.springframework.expression.AccessException;
import org.springframework.stereotype.Service;
import ru.eventtracker.dto.EventDto;
import ru.eventtracker.entity.Event;

import java.util.List;

@Service
public interface EventService {

    public Event save(EventDto eventDto, String userName) throws AccessException;

    public List<Event> findByCategoryName(String categoryName);

    public List<Event> findByUserEmail(String email);

    public String createICalPin(Long eventId);

}
