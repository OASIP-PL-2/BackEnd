package sit.project221.oasipbackend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sit.project221.oasipbackend.dtos.GetDetailUserDTO;
import sit.project221.oasipbackend.dtos.UserDTO;
import sit.project221.oasipbackend.dtos.GetAllUserDTO;
import sit.project221.oasipbackend.dtos.UserLoginDTO;
import sit.project221.oasipbackend.entities.User;
import sit.project221.oasipbackend.services.UserService;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@CrossOrigin
@RestController
//@DeclareRoles({"admin", "student", "lecture"})
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @RolesAllowed("admin")
    @GetMapping("")
    public List<GetAllUserDTO> getAllUser() {
        return userService.getAllUser();
    }

    @GetMapping("/{id}")
    public GetDetailUserDTO getUserById(@PathVariable Integer id) {
        return userService.getUserById(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("")
    public Object create(HttpServletRequest request, @Valid @RequestBody UserDTO newUser) {
        return userService.AddUser(request, newUser);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        userService.deleteUser(id);
    }

    @PutMapping("/{id}")
    public Object update(@Valid @RequestBody UserDTO updateUser, @PathVariable Integer id) {
        return userService.updateUser(updateUser , id);
    }
}
