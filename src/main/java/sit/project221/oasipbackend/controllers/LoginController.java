package sit.project221.oasipbackend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;
import sit.project221.oasipbackend.config.JwtTokenUtil;
import sit.project221.oasipbackend.dtos.UserLoginDTO;
import sit.project221.oasipbackend.services.JwtUserDetailsService;
import sit.project221.oasipbackend.services.LoginService;
import sit.project221.oasipbackend.services.UserService;

import javax.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/api")
public class LoginController {
    @Autowired
    private LoginService loginService;

    @PostMapping("/match")
    public ResponseEntity Match(@Valid @RequestBody UserLoginDTO user) throws Exception {return loginService.match(user);}

    @PostMapping("/login")
    public ResponseEntity Login(@Valid @RequestBody UserLoginDTO user) throws Exception {return loginService.login(user);}
}
