package ru.eventtracker.service;

import org.springframework.stereotype.Service;
import ru.eventtracker.dto.UserDto;
import ru.eventtracker.entity.User;

import java.util.List;

@Service
public interface UserService {

    void saveUser(UserDto userDto);

    User findUserByEmail(String email);

    List<UserDto> findAllUsers();

    void switchEnable(Long id, boolean enable);
}
