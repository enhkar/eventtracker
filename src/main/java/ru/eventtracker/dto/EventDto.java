package ru.eventtracker.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {
    private Long id;

    @NotEmpty
    private String  name;

    @NotEmpty
    private String categoryName;

    @NotEmpty
    private String address;

    @NotEmpty
    private String description;

    private byte[] imageData;

    @DateTimeFormat
    private LocalDateTime startedAt;
}
