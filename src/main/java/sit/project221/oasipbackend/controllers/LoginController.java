package sit.project221.oasipbackend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sit.project221.oasipbackend.dtos.UserLoginDTO;
import sit.project221.oasipbackend.services.UserService;

import javax.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/api/match")
public class LoginController {
    @Autowired
    private UserService userService;

    @PostMapping("")
    public UserLoginDTO Login(@Valid @RequestBody UserLoginDTO user){return userService.Login(user);}
}
