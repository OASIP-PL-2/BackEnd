package sit.project221.oasipbackend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sit.project221.oasipbackend.entities.EventCategoryOwner;
import sit.project221.oasipbackend.services.EventCategoryOwnerService;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/owners")
public class EventCategoryOwnerController {
    @Autowired
    private EventCategoryOwnerService eventCategoryOwnerService;

    @GetMapping("")
    public List<EventCategoryOwner> getAllOwners() {
        return eventCategoryOwnerService.getAllOwners();
    }

    @GetMapping("/{userId}")
    public List<Integer> getEventCategory(@PathVariable Integer userId) {
        return eventCategoryOwnerService.checkOwner(userId);
    }
}
