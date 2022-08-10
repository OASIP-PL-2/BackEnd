package sit.project221.oasipbackend.dtos;

import lombok.*;

import javax.validation.constraints.*;


@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventCategoryDTO {
    @NotBlank(message="eventCategoryName can not be blank")
    @Size(max = 100, message = "eventCategoryName must not be more than 100 characters")
    private String eventCategoryName;
    @Min(value = 1, message = "duration must be 1-480")@Max(value = 480, message = "duration must be 1-480")
    private Integer eventDuration;
    @Size(max = 500, message = "eventCategoryDescription must not be more than 500 characters")
    private String eventCategoryDescription;
}
