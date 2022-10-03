package sit.project221.oasipbackend.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.project221.oasipbackend.config.JwtTokenUtil;
import sit.project221.oasipbackend.controllers.ValidationHandler;
import sit.project221.oasipbackend.dtos.GetDetailUserDTO;
import sit.project221.oasipbackend.dtos.UserDTO;
import sit.project221.oasipbackend.dtos.GetAllUserDTO;
import sit.project221.oasipbackend.entities.User;
import sit.project221.oasipbackend.repositories.UserRepository;
import sit.project221.oasipbackend.utils.ListMapper;
import sit.project221.oasipbackend.utils.Roles;

import java.util.Arrays;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ListMapper listMapper;

    @Autowired
    private Argon2PasswordEncoder argon2PasswordEncoder;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    public List<GetAllUserDTO> getAllUser(){
        List<User> users = userRepository.findAllByOrderByNameAsc();
        return listMapper.mapList(users, GetAllUserDTO.class, modelMapper);
    }

    public GetDetailUserDTO getUserById(Integer id){
        User user = userRepository.findById(id)
                .orElseThrow(()->new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User id "+ id+
                        "Does Not Exist !!!"
                ));
        return modelMapper.map(user, GetDetailUserDTO.class);
    }
    public Object AddUser(UserDTO newUser) {
        User addUserList = modelMapper.map(newUser, User.class);
        addUserList.setName(newUser.getName().trim());
        addUserList.setEmail(newUser.getEmail().trim());
        if (checkUnique(newUser, 0).equals("Pass")) {
            addUserList.setRole(newUser.getRole() == null || newUser.getRole() == "" ? Roles.student.toString() : newUser.getRole());
            if(!checkRole(addUserList.getRole())) {
                return ValidationHandler.showError(HttpStatus.BAD_REQUEST, "Don't have this role");
//                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Don't have this role");
            }
        } else if (checkUnique(newUser, 0).equals("uniqueName")) {
            return ValidationHandler.showError(HttpStatus.BAD_REQUEST, "This name is already used");
        } else if (checkUnique(newUser, 0).equals("uniqueEmail")) {
            return ValidationHandler.showError(HttpStatus.BAD_REQUEST, "This email is already used");
        }

        addUserList.setPassword(argon2PasswordEncoder.encode(addUserList.getPassword()));
        return userRepository.saveAndFlush(addUserList);
    }

    public Object updateUser(UserDTO updateUser, Integer id){
        User user = userRepository.findById(id).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND, id + "does not exist!!!"));
        if(checkUnique(updateUser , id).equals("Pass")){
            user.setName(updateUser.getName().trim());
            user.setEmail(updateUser.getEmail().trim());
            user.setPassword(user.getPassword());
//            System.out.println(updateUser.getRole() == null || updateUser.getRole() == "");
            user.setRole(updateUser.getRole() == null || updateUser.getRole() == "" ? Roles.student.toString() : updateUser.getRole());
            System.out.println(user.getRole());
            if(!checkRole(updateUser.getRole())) {
                return ValidationHandler.showError(HttpStatus.BAD_REQUEST, "Don't have this role");
//                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Don't have this role");
            }
            userRepository.saveAndFlush(user);
        }else if(checkUnique(updateUser , id).equals("uniqueName")){
            return ValidationHandler.showError(HttpStatus.BAD_REQUEST , "This name is already used");
        }else if(checkUnique(updateUser , id).equals("uniqueEmail")){
            return ValidationHandler.showError(HttpStatus.BAD_REQUEST , "This email is already used");
        }
        return user;
    }

    public void deleteUser(Integer id){
        User user = userRepository.findById(id).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        id + " does not exist !!!"));
//        userRepository.deleteById(id);
        userRepository.delete(user);
    }

//    public boolean checkUnique (UserDTO user ,Integer id){
//        List<User> allUser = userRepository.findAll();
//        for(User users : allUser){
//            if(!(users.getId() == id)){
//                if(users.getName().trim().equals(user.getName().trim())){
//                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST , "This name is already used");
//                }if (users.getEmail().trim().equals(user.getEmail().trim())){
//                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST , "This email is already used");
//                }
//            }
//        }
//        return true;
//    }

    private boolean checkRole(String newRole) {
        for (Roles role : Roles.values()){
//            System.out.println(role.toString());
            if(role.toString().equals(newRole) || newRole==null || newRole=="") {
                return true;
            }
        }
        return false;
    }

    public String checkUnique (UserDTO user ,Integer id){
        List<User> allUser = userRepository.findAll();
        for(User users : allUser){
            if(!(users.getId() == id)){
                if(users.getName().trim().equals(user.getName().trim())){
                    return "uniqueName";
//                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST , "This name is already used");
                }if (users.getEmail().trim().equals(user.getEmail().trim())){
                    return "uniqueEmail";
//                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST , "This email is already used");
                }
            }
        }
        return "Pass";
    }

}
