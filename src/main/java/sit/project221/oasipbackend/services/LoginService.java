package sit.project221.oasipbackend.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;
import sit.project221.oasipbackend.config.JwtTokenUtil;
import sit.project221.oasipbackend.controllers.ValidationHandler;
import sit.project221.oasipbackend.dtos.GetAllUserDTO;
import sit.project221.oasipbackend.dtos.UserLoginDTO;
import sit.project221.oasipbackend.entities.User;
import sit.project221.oasipbackend.models.JwtResponse;
import sit.project221.oasipbackend.repositories.UserRepository;

@Service
public class LoginService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Argon2PasswordEncoder argon2PasswordEncoder;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private ModelMapper modelMapper;

    public ResponseEntity match(UserLoginDTO userLogin) throws  Exception {
        if (userRepository.existsByEmail(userLogin.getEmail())) {
            User user = userRepository.findByEmail(userLogin.getEmail());
            if (argon2PasswordEncoder.matches(userLogin.getPassword(), user.getPassword())) {

                return ValidationHandler.showError(HttpStatus.OK, "Password Matched");

            } else {
                return ValidationHandler.showError(HttpStatus.UNAUTHORIZED, "Password NOT Matched");
            }
        } else {
            return ValidationHandler.showError(HttpStatus.NOT_FOUND, "A user with the specified email DOES NOT exist");
        }
    }

    public ResponseEntity login(UserLoginDTO userLogin) throws  Exception {
        if (userRepository.existsByEmail(userLogin.getEmail())) {
            User user = userRepository.findByEmail(userLogin.getEmail());
            if (argon2PasswordEncoder.matches(userLogin.getPassword(), user.getPassword())) {
                authenticate(userLogin.getEmail() , userLogin.getPassword());
                authenticate(userLogin.getEmail(), userLogin.getPassword());

                final UserDetails userDetails = userDetailsService.loadUserByUsername(userLogin.getEmail());

                final String token = jwtTokenUtil.generateToken(userDetails);
                final String refreshToken = jwtTokenUtil.generateRefreshToken(userDetails);

                GetAllUserDTO userResponse = modelMapper.map(user, GetAllUserDTO.class);
                return ResponseEntity.ok(new JwtResponse("Login Success", token, refreshToken, userResponse));

//                return ResponseEntity.ok(new JwtResponse(token, user.getId(), user.getName(), user.getEmail(), user.getRole()));
//                throw new ResponseStatusException(HttpStatus.OK, "Password Matched");
            } else {
                return ValidationHandler.showError(HttpStatus.UNAUTHORIZED, "Password NOT Matched");
//                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Password NOT Matched");
            }
        } else {
            return ValidationHandler.showError(HttpStatus.NOT_FOUND, "A user with the specified email DOES NOT exist");
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "A user with the specified email DOES NOT exist");
        }
    }

    private void authenticate(String email, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }
}
