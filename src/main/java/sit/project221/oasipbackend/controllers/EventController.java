package sit.project221.oasipbackend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sit.project221.oasipbackend.dtos.AddEventDTO;
import sit.project221.oasipbackend.dtos.GetEventDTO;
import sit.project221.oasipbackend.dtos.UpdateEventDTO;
import sit.project221.oasipbackend.entities.Event;
import sit.project221.oasipbackend.services.EventService;
import sit.project221.oasipbackend.services.RefreshService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/events")
public class EventController {
    @Autowired
    private EventService eventService;

    @GetMapping("")
    public List<GetEventDTO> getAllEvent(@Valid HttpServletRequest request) {
        return eventService.getAllEvent(request);
    }

    @GetMapping("/{bookingId}")
    public Object getById(@Valid HttpServletRequest request, @PathVariable Integer bookingId) {
        return eventService.getEventById(request, bookingId);
    }

    //Category ID
    @GetMapping("/categories/{categoryID}")
    public List<GetEventDTO> getEventByCategory(@PathVariable Integer categoryID) { return eventService.getEventByCategory(categoryID);}

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("")
    public Object create(@Valid HttpServletRequest request, @Valid @RequestBody AddEventDTO newEvent) {
        return eventService.addEvent(request, newEvent);
    }

    @DeleteMapping("/{bookingId}")
    public Object delete(@Valid HttpServletRequest request, @PathVariable Integer bookingId) {
        return eventService.deleteEvent(request, bookingId);
    }

    @PutMapping("/{bookingId}")
    public Object update(@Valid HttpServletRequest request, @Valid @RequestBody UpdateEventDTO updateEvent, @PathVariable Integer bookingId) {
        return eventService.updateEvent(request, updateEvent, bookingId);
    }

    @GetMapping("/past")
    public List<GetEventDTO> getPastEvent() {
        return eventService.getPastEvent();
    }

    @GetMapping("/future")
    public List<GetEventDTO> getFutureEvent() {
        return eventService.getFutureEvent();
    }

    @GetMapping("/date/{date}")
    public List<GetEventDTO> getEventsByDate(@PathVariable Integer date) {
        return eventService.getEventsByDate(date);
    }
}
