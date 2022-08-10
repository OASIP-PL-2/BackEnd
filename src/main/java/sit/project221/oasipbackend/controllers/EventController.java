package sit.project221.oasipbackend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sit.project221.oasipbackend.dtos.AddEventDTO;
import sit.project221.oasipbackend.dtos.GetEventDTO;
import sit.project221.oasipbackend.dtos.UpdateEventDTO;
import sit.project221.oasipbackend.entities.Event;
import sit.project221.oasipbackend.services.EventService;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/events")
public class EventController {
    @Autowired
    private EventService eventService;


    @GetMapping("")
    public List<GetEventDTO> getAllEvent() {
        return eventService.getAllEvent();
    }

    @GetMapping("/{bookingId}")
    public GetEventDTO getById(@PathVariable Integer bookingId) {
        return eventService.getEventById(bookingId);
    }



    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("")
    public Event create(@Valid @RequestBody AddEventDTO newEvent) {
        return eventService.addEvent(newEvent);
    }

    @DeleteMapping("/{bookingId}")
    public void delete(@PathVariable Integer bookingId) {
        eventService.deleteEvent(bookingId);
    }

    @PutMapping("/{bookingId}")
    public UpdateEventDTO update(@Valid @RequestBody UpdateEventDTO updateEvent, @PathVariable Integer bookingId) {
        return eventService.updateEvent(updateEvent, bookingId);
    }
}
