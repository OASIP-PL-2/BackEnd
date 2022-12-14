package sit.project221.oasipbackend.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.codec.serialization.ObjectEncoder;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import sit.project221.oasipbackend.dtos.AddEventDTO;
import sit.project221.oasipbackend.dtos.GetBlindEvent;
import sit.project221.oasipbackend.dtos.GetEventDTO;
import sit.project221.oasipbackend.dtos.UpdateEventDTO;
import sit.project221.oasipbackend.entities.Event;
import sit.project221.oasipbackend.entities.EventCategory;
import sit.project221.oasipbackend.repositories.EventCategoryRepository;
import sit.project221.oasipbackend.services.EmailSenderService;
import sit.project221.oasipbackend.services.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/events")
public class EventController {
    @Autowired
    private EventService eventService;
    @Autowired
    private ModelMapper modelMapper;
    private EmailSenderService senderService;
    private EventCategoryRepository eventCategoryRepository;
    public EventController(EmailSenderService senderService, EventCategoryRepository eventCategoryRepository) {
        this.senderService = senderService;
        this.eventCategoryRepository = eventCategoryRepository;
    }

    @GetMapping("")
    public List<GetEventDTO> getAllEvent(@Valid HttpServletRequest request) {
        return eventService.getAllEvent(request);
    }

    @GetMapping("/blindEvent")
    public List<GetBlindEvent> getAllBlindEvent(){
        return eventService.getAllBlindEvent();
    }

    @GetMapping("/{bookingId}")
    public Object getById(@Valid HttpServletRequest request, @PathVariable Integer bookingId) {
        return eventService.getEventById(request, bookingId);
    }

    //Category ID
//    @GetMapping("/categories/{categoryID}")
//    public List<GetEventDTO> getEventByCategory(@PathVariable Integer categoryID) { return eventService.getEventByCategory(categoryID);}

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path="",consumes = {"multipart/form-data"})
    public Object create(@Valid HttpServletRequest request, @RequestParam("event") String event, @RequestParam(name = "file", required = false) MultipartFile file) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        AddEventDTO newEvent  = objectMapper.readValue(event, AddEventDTO.class);

        int categoryId = Integer.parseInt(newEvent.getEventCategoryId());
        EventCategory eventCategory = eventCategoryRepository.findById(categoryId).orElseThrow(()->new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Category id "+ categoryId+
                "Does Not Exist !!!"
        ));
        LocalDateTime time = newEvent.getEventStartTime();
        String formattedDate = time.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy, hh.mm"));
        String header = "You have made a new appointment for 1 event.";
        String body = "Your appointment has been registered successfully. \n \n" +
                "Details  \n" + "Name : " + newEvent.getBookingName() + "\n" +"Clinic : " + eventCategory.getEventCategoryName() +
                "\n" + "Date : " + formattedDate + " ???." + "\n" + "Note : " + newEvent.getEventNote();


        Object addedEvent = eventService.addEvent(request, newEvent, file);
        senderService.sendEmail(newEvent.getBookingEmail() , header , body);
        return addedEvent;
    }

    @DeleteMapping("/{bookingId}")
    public Object delete(@Valid HttpServletRequest request, @PathVariable Integer bookingId) {
        return eventService.deleteEvent(request, bookingId);
    }

    @PutMapping("/{bookingId}")
    public Object update(@Valid HttpServletRequest request, @RequestParam("event") String event, @RequestParam(name = "file", required = false) MultipartFile file, @PathVariable Integer bookingId) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        UpdateEventDTO editEvent  = objectMapper.readValue(event, UpdateEventDTO.class);

        return eventService.updateEvent(request, editEvent, file, bookingId);
    }

    @GetMapping("/past")
    public List<GetEventDTO> getPastEvent(HttpServletRequest request) {
        return eventService.getPastEvent(request);
    }

    @GetMapping("/future")
    public List<GetEventDTO> getFutureEvent(HttpServletRequest request) {
        return eventService.getFutureEvent(request);
    }

    @GetMapping("/date/{date}")
    public List<GetEventDTO> getEventsByDate(HttpServletRequest request, @PathVariable Integer date) {
        return eventService.getEventsByDate(request, date);
    }
}
