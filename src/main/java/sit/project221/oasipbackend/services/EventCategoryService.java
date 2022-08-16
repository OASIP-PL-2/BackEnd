package sit.project221.oasipbackend.services;


import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.project221.oasipbackend.controllers.ValidationHandler;
import sit.project221.oasipbackend.dtos.AddCategoryDTO;
import sit.project221.oasipbackend.dtos.GetEventCategoryDTO;
import sit.project221.oasipbackend.dtos.UpdateEventCategoryDTO;
import sit.project221.oasipbackend.entities.EventCategory;
import sit.project221.oasipbackend.repositories.EventCategoryRepository;
import sit.project221.oasipbackend.utils.ListMapper;
import java.util.List;


@Service
public class EventCategoryService {
    @Autowired
    private EventCategoryRepository eventCategoryRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ListMapper listMapper;

    public List<GetEventCategoryDTO> getAllEventCategory(){
        List<EventCategory> eventCategoryList = eventCategoryRepository.findAllByOrderByIdDesc();
        return listMapper.mapList(eventCategoryList, GetEventCategoryDTO.class, modelMapper);
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
                return ValidationHandler.showError("eventCategoryName", "EventCategoryName must be unique");
            }
        }
        updateCategory.setEventCategoryName(updateEventCategory.getEventCategoryName().trim());
        updateCategory.setEventCategoryDescription(updateEventCategory.getEventCategoryDescription());
        updateCategory.setEventDuration(updateEventCategory.getEventDuration());
        eventCategoryRepository.saveAndFlush(updateCategory);
        return updateEventCategory;
    }
    public void addEventCategory(AddCategoryDTO newCategory){
        eventCategoryRepository.insertCategory(newCategory.getEventCategoryName(), newCategory.getEventDuration());
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
