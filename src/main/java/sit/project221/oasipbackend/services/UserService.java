package sit.project221.oasipbackend.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sit.project221.oasipbackend.dtos.GetAllUserDTO;
import sit.project221.oasipbackend.entities.User;
import sit.project221.oasipbackend.repositories.UserRepository;
import sit.project221.oasipbackend.utils.ListMapper;

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
//
//    public List<User> getAllUser(){
//        return userRepository.findAll();
//    }
}
