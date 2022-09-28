package sit.project221.oasipbackend.models;

import lombok.*;
import sit.project221.oasipbackend.dtos.GetAllUserDTO;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class JwtResponse {
    //    private String jwtToken;
    private String message;
    private String accessToken;
    private String refreshToken;
    private GetAllUserDTO user;
}