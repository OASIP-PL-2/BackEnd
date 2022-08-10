package sit.project221.oasipbackend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;

import sit.project221.oasipbackend.dtos.GetEventCategoryDTO;
import sit.project221.oasipbackend.dtos.UpdateEventCategoryDTO;
import sit.project221.oasipbackend.dtos.UpdateEventDTO;
import sit.project221.oasipbackend.services.EventCategoryService;
import sit.project221.oasipbackend.services.EventService;


import javax.validation.Valid;
import java.util.List;
@EnableAutoConfiguration

@CrossOrigin
@RestController
@RequestMapping("/api/categories")
public class EventCategoryController {
    @Autowired
    private EventCategoryService eventCategoryService;

    private EventService eventService;

    @GetMapping("")
    public List<GetEventCategoryDTO> getEventCategory() {
        return eventCategoryService.getAllEventCategory();
    }

    @GetMapping("{categoryId}")
    public GetEventCategoryDTO getEventByCategoryId(@PathVariable Integer categoryId) {
        return eventCategoryService.getEventByCategoryId(categoryId);
    }

    @PutMapping("/{categoryId}")
    public Object update(@Valid @RequestBody UpdateEventCategoryDTO updateEventCategory, @PathVariable Integer categoryId) {
        return eventCategoryService.updateEventCategory(updateEventCategory, categoryId);
    }

//    @GetMapping("/{categories}/events")
//    public List<GetEventDTO> getEventByCategory(@PathVariable Integer categoryId) {
//        return eventService.getEventByCategory(categoryId);
//    }
}
