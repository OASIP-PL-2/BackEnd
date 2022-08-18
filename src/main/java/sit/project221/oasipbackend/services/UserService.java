package sit.project221.oasipbackend.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.project221.oasipbackend.dtos.GetDetailUserDTO;
import sit.project221.oasipbackend.dtos.UserDTO;
import sit.project221.oasipbackend.dtos.GetAllUserDTO;
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
        addUserList.setRole(newUser.getRole() == null ? Roles.student : newUser.getRole());
        return userRepository.saveAndFlush(addUserList);
    }
    public UserDTO updateUser(UserDTO updateUser, Integer id){
        User user = userRepository.findById(id).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND, id + "does not exist!!!"));
        user.setName(updateUser.getName());
        user.setEmail(updateUser.getEmail());
        user.setRole((updateUser.getRole() == null) ? user.getRole() : updateUser.getRole());
        userRepository.saveAndFlush(user);
        return updateUser;
    }

    public void deleteUser(Integer id){
        userRepository.findById(id).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        id + " does not exist !!!"));
        userRepository.deleteById(id);
    }

    public boolean checkRoles (Roles role){
        return Arrays.stream(Roles.values()).equals(role);
    }

}
