package sit.project221.oasipbackend.services;


import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.project221.oasipbackend.config.JwtTokenUtil;
import sit.project221.oasipbackend.controllers.ValidationHandler;
import sit.project221.oasipbackend.dtos.GetEventCategoryDTO;
import sit.project221.oasipbackend.dtos.UpdateEventCategoryDTO;
import sit.project221.oasipbackend.entities.Event;
import sit.project221.oasipbackend.entities.EventCategory;
import sit.project221.oasipbackend.entities.User;
import sit.project221.oasipbackend.repositories.EventCategoryOwnerRepository;
import sit.project221.oasipbackend.repositories.EventCategoryRepository;
import sit.project221.oasipbackend.repositories.UserRepository;
import sit.project221.oasipbackend.utils.ListMapper;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;


@Service
public class EventCategoryService {
    @Autowired
    private EventCategoryRepository eventCategoryRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EventCategoryOwnerRepository eventCategoryOwnerRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ListMapper listMapper;

    private final JwtTokenUtil jwtTokenUtill;
    private final JwtUserDetailsService jwtUserDetailsService;

    public EventCategoryService(JwtTokenUtil jwtTokenUtill, JwtUserDetailsService jwtUserDetailsService) {
        this.jwtTokenUtill = jwtTokenUtill;
        this.jwtUserDetailsService = jwtUserDetailsService;
    }

    public User getUserFromRequest(HttpServletRequest request) {
        if (request.getHeader("Authorization") != null) {
            String token = request.getHeader("Authorization").substring(7);
            String userEmail = jwtTokenUtill.getUsernameFromToken(token);
            return  userRepository.findByEmail(userEmail);
        }
        return null;
    }
    public List<GetEventCategoryDTO> getAllEventCategory(HttpServletRequest request){
        User userOwner = getUserFromRequest(request);
        List<EventCategory> eventCategoryList = eventCategoryRepository.findAllByOrderByIdDesc();
        List<EventCategory> eventCategoryFilter = new ArrayList<>();

        if (userOwner != null) {
            if (userOwner.getRole().equals("lecturer")){
                System.out.println("เข้า lecturer");
                List<Integer> categoriesId = eventCategoryOwnerRepository.findAllByUserId(userOwner.getId());
                for(EventCategory category : eventCategoryList){
                    if (categoriesId.contains(category.getId())) {
                        eventCategoryFilter.add(category);
                    }
                }
            } else {
                eventCategoryFilter = eventCategoryList;
            }
        } else {
            eventCategoryFilter = eventCategoryList;
        }

        return listMapper.mapList(eventCategoryFilter, GetEventCategoryDTO.class, modelMapper);
    }

    public GetEventCategoryDTO getEventByCategoryId(Integer categoryId){
        EventCategory eventCategory = eventCategoryRepository.findById(categoryId).orElseThrow(()->new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Customer id "+ categoryId+
                "Does Not Exist !!!"
        ));
        return modelMapper.map(eventCategory, GetEventCategoryDTO.class);
    }

    public Object updateEventCategory(UpdateEventCategoryDTO updateEventCategory, Integer categoryId){
        EventCategory updateCategory = eventCategoryRepository.findById(categoryId).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND, categoryId + "does not exist!!!"));
        List <EventCategory> eventCategory = eventCategoryRepository.findAll();
        eventCategory.remove(updateCategory);

        for(EventCategory category:eventCategory) {
            if (category.getEventCategoryName().toLowerCase().trim().equals(updateEventCategory.getEventCategoryName().toLowerCase().trim())) {
                return ValidationHandler.showError(HttpStatus.BAD_REQUEST, "EventCategoryName must be unique");
            }
        }
        updateCategory.setEventCategoryName(updateEventCategory.getEventCategoryName().trim());
        updateCategory.setEventCategoryDescription(updateEventCategory.getEventCategoryDescription());
        updateCategory.setEventDuration(updateEventCategory.getEventDuration());
        eventCategoryRepository.saveAndFlush(updateCategory);
        return updateEventCategory;
    }

//
//    public Object checkDuplicateCategoryName(List<EventCategory> eventCategory,String updateCategoryName){
//
//        for(EventCategory category:eventCategory){
//            if(category.getEventCategoryName().toLowerCase().trim().equals(updateCategoryName.toLowerCase().trim())){
//                return ValidationHandler.showError("eventCategoryName", "EventCategoryName must be unique");
//            }
//        }
//        return null;
//    }


}
