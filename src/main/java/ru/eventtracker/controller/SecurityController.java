package ru.eventtracker.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.eventtracker.dao.EventRepository;
import ru.eventtracker.dto.UserDto;
import ru.eventtracker.entity.Event;
import ru.eventtracker.entity.User;
import ru.eventtracker.service.UserService;

import java.util.List;


@Controller
public class SecurityController {

    private final UserService userService;

    public SecurityController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/signin")
    public String getSignInForm(Model model) {
        model.addAttribute("user", new UserDto());
        return "sign-in";
    }

    @GetMapping("/signup")
    public String getSignUpForm(Model model) {
        model.addAttribute("user", new UserDto());
        return "sign-up";
    }

    @PostMapping("/signup")
    public String signUp(@Valid @ModelAttribute("user") UserDto userDto, BindingResult bindingResult, Model model) {
        User existingUser = userService.findUserByEmail(userDto.getEmail());

        if (existingUser != null && existingUser.getEmail() != null && !existingUser.getEmail().isEmpty()) {
            bindingResult.rejectValue(
                    "email", null, "На этот адрес уже зарегистрирована учётная запись."
            );
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("user", userDto);
            return "sign-up";
        }

        userService.saveUser(userDto);
        return "events";
    }

    @GetMapping("/users")
    public String getUsers(Model model) {
        List<UserDto> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "user-list";
    }

    @PostMapping("/users/{id}/access")
    public String switchState(@PathVariable(name = "id") Long id, @RequestParam(name = "enable") Boolean enable, Model model) {
        userService.switchEnable(id, enable);

        List<UserDto> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "user-list";
    }
}
