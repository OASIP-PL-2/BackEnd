package sit.project221.oasipbackend.models;

import lombok.*;

import java.io.Serializable;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class JwtResponse {
    //    private static  long serialVersionUID = -8091879091924046844L;
    private String jwtToken;

    private Integer id;
    private String name;
    private String email;
    private String role;
}