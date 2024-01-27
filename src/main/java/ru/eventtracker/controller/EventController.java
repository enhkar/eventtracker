package ru.eventtracker.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.expression.AccessException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import ru.eventtracker.dao.CategoryRepository;
import ru.eventtracker.dao.EventRepository;
import ru.eventtracker.dao.UserRepository;
import ru.eventtracker.dto.EventDto;
import ru.eventtracker.dto.UserDto;
import ru.eventtracker.entity.Category;
import ru.eventtracker.entity.Event;
import ru.eventtracker.entity.User;
import ru.eventtracker.service.EventService;
import ru.eventtracker.service.UserService;

import java.io.*;
import java.security.Principal;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Controller
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserService userService;

    @Autowired
    public EventController(EventService eventService, EventRepository eventRepository, CategoryRepository categoryRepository, UserService userService) {
        this.eventService = eventService;
        this.eventRepository = eventRepository;
        this.categoryRepository = categoryRepository;
        this.userService = userService;
    }

    @PostMapping()
    public ModelAndView saveEvent(
            @RequestPart("image") MultipartFile image,
            @Valid @ModelAttribute("event") EventDto eventDto,
            BindingResult bindingResult,
            Principal principal,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("event", eventDto);
            return new ModelAndView("event-form");
        }
        try {
            eventDto.setImageData(image.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Event event = null;
        try {
            event = eventService.save(eventDto, principal.getName());
        } catch (AccessException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        log.info("return event={}", event);
        model.addAttribute("event", event);
        return new ModelAndView("redirect:/events/" + event.getId());
    }

    @GetMapping("/form")
    public String getEventForm(@RequestParam(required = false, name = "id") Long eventId, Principal principal, Model model) {
        Event event = new Event();
        List<Category> categories = categoryRepository.findAll();
        String started = "";
        if (eventId != null) {
            Optional<Event> eventOptional = eventRepository.findById(eventId);
            if (eventOptional.isPresent()) {
                event = eventOptional.get();

                if (!event.getUser().getEmail().equals(principal.getName())) {
                    User user = userService.findUserByEmail(principal.getName());
                    if (!user.hasRole("ROLE_ADMIN")) {
                        log.warn("wrong user role");
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN);
                    }
                }

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
                started = event.getStartedAt().format(formatter);
            }
        }

        model.addAttribute("event", event);
        model.addAttribute("categories", categories);
        model.addAttribute("started", started);
        return "event-form";
    }

    @GetMapping()
    public String getEvents(@RequestParam(required = false) String category, Model model) {
        List<Event> events = new ArrayList<>();

        if (category != null) {
            events = eventService.findByCategoryName(category);
        }
        else {
            events = eventRepository.findAll();
        }

        log.info("return events={}", events);
        model.addAttribute("events", events);
        return "events";
    }

    @GetMapping("/{id}")
    public String getEvent(@PathVariable Long id, Model model) {
        Optional<Event> optionalEvent = eventRepository.findById(id);

        if (optionalEvent.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "event not found"
            );
        }

        model.addAttribute("event", optionalEvent.get());
        return "event";
    }

    @GetMapping("{id}/image")
    public ResponseEntity<byte[]> showEventImage(@PathVariable Long id, HttpServletResponse response) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "image not found"));
        byte[] content = event.getImageData();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setContentLength(content.length);

        return ResponseEntity.ok().headers(headers).body(content);
    }

    @GetMapping("{id}/ical")
    public ResponseEntity<FileSystemResource> createEvent(@PathVariable Long id) {
        File file = null;

        try {
            file = File.createTempFile("event", ".ics");
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(eventService.createICalPin(id));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.status(HttpStatus.OK)
                .header("Content-Disposition", "attachment; filename=event.ics")
                .body(new FileSystemResource(file));
    }

    @GetMapping("/my")
    public String getUserSpace(Principal principal, Model model) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        model.addAttribute("events", eventService.findByUserEmail(principal.getName()));
        return "lk";
    }
}
