package sit.project221.oasipbackend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sit.project221.oasipbackend.dtos.GetAllUserDTO;
import sit.project221.oasipbackend.entities.User;
import sit.project221.oasipbackend.services.UserService;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("")
    public List<GetAllUserDTO> getAllUser() {
        return userService.getAllUser();
    }
}
