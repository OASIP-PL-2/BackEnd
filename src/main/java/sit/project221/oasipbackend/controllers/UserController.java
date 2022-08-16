package sit.project221.oasipbackend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sit.project221.oasipbackend.dtos.AddUserDTO;
import sit.project221.oasipbackend.dtos.GetAllUserDTO;
import sit.project221.oasipbackend.dtos.UpdateUserDTO;
import sit.project221.oasipbackend.entities.User;
import sit.project221.oasipbackend.services.UserService;

import javax.validation.Valid;
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

    @GetMapping("/{id}")
    public GetAllUserDTO getUserById(@PathVariable Integer id) {
        return userService.getUserById(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("")
    public User create(@Valid @RequestBody AddUserDTO newUser) {
        return userService.AddUser(newUser);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        userService.deleteUser(id);
    }

    @PutMapping("/{id}")
    public UpdateUserDTO update(@Valid @RequestBody UpdateUserDTO updateUser, @PathVariable Integer id) {
        return userService.updateUser(updateUser , id);
    }

}
