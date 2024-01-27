package ru.eventtracker.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.AccessException;
import org.springframework.stereotype.Service;
import ru.eventtracker.dao.CategoryRepository;
import ru.eventtracker.dao.EventRepository;
import ru.eventtracker.dao.UserRepository;
import ru.eventtracker.dto.EventDto;
import ru.eventtracker.entity.Category;
import ru.eventtracker.entity.Event;
import ru.eventtracker.entity.User;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Autowired
    public EventServiceImpl(EventRepository eventRepository, CategoryRepository categoryRepository, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Event save(EventDto eventDto, String email) throws AccessException {
        Event event = new Event();

        Long id = eventDto.getId();
        if (id != null) {
            event = eventRepository.findById(id).orElse(event);
        }

        event.setId(eventDto.getId());
        event.setName(eventDto.getName());
        event.setAddress(eventDto.getAddress());
        event.setDescription(eventDto.getDescription());
        event.setStartedAt(eventDto.getStartedAt());

        byte[] imageData = eventDto.getImageData();
        if (imageData != null && imageData.length != 0) {
            event.setImageData(eventDto.getImageData());
        }

        Category category = categoryRepository.findByName(eventDto.getCategoryName());
        event.setCategory(category);

        User currentUser = userRepository.findByEmail(email);
        event.setUser(currentUser);

        return eventRepository.save(event);
    }

    @Override
    public List<Event> findByCategoryName(String categoryName) {
        return eventRepository.findByCategory_Name(categoryName);
    }

    @Override
    public List<Event> findByUserEmail(String email) {
        return eventRepository.findByUser_Email(email);
    }

    @Override
    public String createICalPin(Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(NoSuchElementException::new);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");
        return """
                BEGIN:VCALENDAR
                VERSION:2.0
                PRODID:-//Apple Computer\\\\, Inc//iCal 1.5//EN
                CALSCALE:GREGORIAN
                BEGIN:VEVENT
                SUMMARY:%s
                DTSTART;VALUE=DATE-TIME:%s
                DESCRIPTION:%s
                END:VEVENT
                            """.formatted(event.getName(), event.getStartedAt().format(formatter), event.getDescription());
    }
}
