package sit.project221.oasipbackend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sit.project221.oasipbackend.entities.EventCategoryOwner;
import sit.project221.oasipbackend.repositories.EventCategoryOwnerRepository;

import java.util.List;

@Service
public class EventCategoryOwnerService {
    @Autowired
    private EventCategoryOwnerRepository eventCategoryOwnerRepository;

    public List<Integer> checkOwner(Integer userId) {
        List<EventCategoryOwner> allOwners = eventCategoryOwnerRepository.findAll();
        return eventCategoryOwnerRepository.findAllByUserId(userId);

    }

    public List<EventCategoryOwner> getAllOwners() {
        return eventCategoryOwnerRepository.findAll();


    }
}
