package sit.project221.oasipbackend.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.yaml.snakeyaml.util.EnumUtils;
import sit.project221.oasipbackend.dtos.GetDetailUserDTO;
import sit.project221.oasipbackend.dtos.UserDTO;
import sit.project221.oasipbackend.dtos.GetAllUserDTO;
import sit.project221.oasipbackend.dtos.UserLoginDTO;
import sit.project221.oasipbackend.entities.User;
import sit.project221.oasipbackend.repositories.UserRepository;
import sit.project221.oasipbackend.utils.ListMapper;
import sit.project221.oasipbackend.utils.Roles;

import java.util.Arrays;
import java.util.List;

@Service()
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ListMapper listMapper;

    @Autowired
    private Argon2PasswordEncoder argon2PasswordEncoder;

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

    public User AddUser(UserDTO newUser){
        User addUserList = modelMapper.map(newUser, User.class);
        addUserList.setName(newUser.getName().trim());
        addUserList.setEmail(newUser.getEmail().trim());
        if(checkUnique(newUser , 0)){
            addUserList.setRole(newUser.getRole() == null || newUser.getRole() == "" ? Roles.student.toString() : newUser.getRole());
//            user.setRole(updateUser.getRole()==null||updateUser.getRole()==""?Roles.student.toString():updateUser.getRole());
//            if(!checkRole(addUserList.getRole())) {
//                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Don't have this role");
//            }
        }
        addUserList.setPassword(argon2PasswordEncoder.encode(addUserList.getPassword()));
        return userRepository.saveAndFlush(addUserList);
    }
    public UserDTO updateUser(UserDTO updateUser, Integer id){
        User user = userRepository.findById(id).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND, id + "does not exist!!!"));
        if(checkUnique(updateUser , id)){
            user.setName(updateUser.getName().trim());
            user.setEmail(updateUser.getEmail().trim());
            user.setRole((updateUser.getRole() == null) ? user.getRole() : updateUser.getRole());
//            user.setPassword((updateUser.getPassword() == null)? user.getPassword() : updateUser.getPassword());
            user.setPassword(user.getPassword());
            userRepository.saveAndFlush(user);
        }
        return updateUser;
    }

    public void deleteUser(Integer id){
        userRepository.findById(id).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        id + " does not exist !!!"));
        userRepository.deleteById(id);
    }

    public boolean checkUnique (UserDTO user ,Integer id){
        List<User> allUser = userRepository.findAll();
        for(User users : allUser){
            if(!(users.getId() == id)){
                if(users.getName().trim().equals(user.getName().trim())){
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST , "This name is already used");
                }if (users.getEmail().trim().equals(user.getEmail().trim())){
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST , "This email is already used");
                }
            }
        }
        return true;
    }

    private boolean checkRole(String newRole) {
        boolean existRole = false;
        for (Roles role : Roles.values()){
            System.out.println(role.toString());
//                if(newRole==null||(newRole.equals(role.toString())||newRole=="")) {
            if(role.toString().equals(newRole) || newRole==null || newRole=="") {
                return true;
            }
        }
        return false;
//        return existRole;
    }

    public UserLoginDTO Login (UserLoginDTO user){
        if(userRepository.existsByEmail(user.getEmail())) {
            User users = userRepository.findByEmail(user.getEmail());
            if(argon2PasswordEncoder.matches(user.getPassword() , users.getPassword())){
                throw new ResponseStatusException(HttpStatus.OK , "Password Matched");
            }else{
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED , "Password NOT Matched");
            }
        }else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "A user with the specified email DOES NOT exist");
        }
    }

}
