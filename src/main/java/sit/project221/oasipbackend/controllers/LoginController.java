package sit.project221.oasipbackend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;
import sit.project221.oasipbackend.config.JwtTokenUtil;
import sit.project221.oasipbackend.dtos.UserLoginDTO;
import sit.project221.oasipbackend.services.JwtUserDetailsService;
import sit.project221.oasipbackend.services.UserService;

import javax.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/api/login")
public class LoginController {
    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @PostMapping("")
    public ResponseEntity Login(@Valid @RequestBody UserLoginDTO user) throws Exception {return userService.login(user);}
}
