package sit.project221.oasipbackend.dtos;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddEventDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Email(regexp ="(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])",
            flags = Pattern.Flag.CASE_INSENSITIVE, message = "Email should be valid")
    @NotNull(message = "Email cannot be Null")
    private String bookingEmail;
    @NotEmpty(message="booking name can not be empty")
    @NotBlank(message="booking name can not be blank")
    @Size(max = 100, message = "booking name must not be more than 100 characters")
    private String bookingName;
    private Integer eventDuration;
    @Size(max = 500, message = "eventNotes must not be more than 500 characters")
    private String eventNote;
    @NotNull(message="eventStartTime can't be null")
    @JsonFormat(pattern="dd/MM/yyyy, HH:mm:ss")
    @Future(message = "required future date time")
    private LocalDateTime eventStartTime;
    @NotNull(message="event category can not be null")
    private String eventCategoryId;
}
