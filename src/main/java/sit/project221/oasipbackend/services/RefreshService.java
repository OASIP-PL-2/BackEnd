package sit.project221.oasipbackend.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import sit.project221.oasipbackend.config.JwtTokenUtil;
import sit.project221.oasipbackend.dtos.GetAllUserDTO;
import sit.project221.oasipbackend.entities.User;
import sit.project221.oasipbackend.models.JwtResponse;
import sit.project221.oasipbackend.models.Response;
import sit.project221.oasipbackend.repositories.UserRepository;

import javax.servlet.http.HttpServletRequest;

@Service
public class RefreshService {
    private final JwtTokenUtil jwtTokenUtill;
    private final JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepository userRepository;

    public RefreshService(JwtTokenUtil jwtTokenUtill, JwtUserDetailsService jwtUserDetailsService) {
        this.jwtTokenUtill = jwtTokenUtill;
        this.jwtUserDetailsService = jwtUserDetailsService;
    }


    public ResponseEntity refreshToken(HttpServletRequest request){
        String requestRefreshToken = request.getHeader("Authorization").substring(7);
        String userEmail = jwtTokenUtill.getUsernameFromToken(requestRefreshToken);
        UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(userEmail);
        System.out.println(userDetails.getUsername());
        String accessToken = jwtTokenUtill.generateToken(userDetails);
        String refreshToken = jwtTokenUtill.generateRefreshToken(userDetails);
//            String getRefreshTokenExpire = jwtTokenUtill.getExpirationDateFromToken(u)
//        if (checkExpired(requestRefreshToken).equals(true)) {
//            return ResponseEntity.ok(new JwtResponse("Refresh Token Success",accessToken,refreshToken));
//        }

        if(jwtTokenUtill.validateToken(requestRefreshToken, userDetails)){
            User user = userRepository.findByEmail(userEmail);
            GetAllUserDTO userResponse = modelMapper.map(user, GetAllUserDTO.class);
            return ResponseEntity.ok(new JwtResponse("Refresh Token Successfully", accessToken, refreshToken, userResponse));
        }
        return Response.response(HttpStatus.NOT_FOUND, "Can't find Refresh Token");
    }

    private Boolean checkExpired(String request){
        if(!jwtTokenUtill.isTokenExpired(request)){
            return true;
        }
        return false;
    }
}