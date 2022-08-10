package sit.project221.oasipbackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetEventCategoryDTO {
    private Integer id;
    private String eventCategoryName;
    private Integer eventDuration;
    private String eventCategoryDescription;
}
