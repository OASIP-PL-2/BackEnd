package sit.project221.oasipbackend.dtos;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sit.project221.oasipbackend.entities.EventCategory;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetEventDTO {
    private Integer id;
    private String bookingName;
    private String bookingEmail;
    private EventCategory eventCategoryId;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventStartTime;
    private Integer eventDuration;
    private String eventNote;
}
