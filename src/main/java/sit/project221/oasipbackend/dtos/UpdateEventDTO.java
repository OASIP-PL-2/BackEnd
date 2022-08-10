package sit.project221.oasipbackend.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Future;
import javax.validation.constraints.Size;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventDTO {
    @Future(message = "required future date time")
    @JsonFormat(pattern="dd/MM/yyyy, HH:mm:ss")
    private LocalDateTime eventStartTime;
    @Size(max = 500, message = "eventNotes must not be more than 500 characters")
    private String eventNote;
    private Integer eventDuration;
    private String eventCategoryId;
}
