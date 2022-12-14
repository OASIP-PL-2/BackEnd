package sit.project221.oasipbackend.services;

import lombok.val;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import sit.project221.oasipbackend.config.JwtTokenUtil;
import sit.project221.oasipbackend.controllers.ValidationHandler;
import sit.project221.oasipbackend.dtos.AddEventDTO;
import sit.project221.oasipbackend.dtos.GetBlindEvent;
import sit.project221.oasipbackend.dtos.GetEventDTO;
import sit.project221.oasipbackend.dtos.UpdateEventDTO;
import sit.project221.oasipbackend.entities.Event;
import sit.project221.oasipbackend.entities.User;
import sit.project221.oasipbackend.repositories.EventCategoryOwnerRepository;
import sit.project221.oasipbackend.repositories.EventRepository;
import sit.project221.oasipbackend.repositories.UserRepository;
import sit.project221.oasipbackend.storage.StorageService;
import sit.project221.oasipbackend.utils.ListMapper;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventCategoryOwnerRepository eventCategoryOwnerRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ListMapper listMapper;

    @Autowired
    private StorageService storageService;

    @Autowired
    private Validator validator;
    private final JwtTokenUtil jwtTokenUtill;
    private final JwtUserDetailsService jwtUserDetailsService;

    public EventService(JwtTokenUtil jwtTokenUtill, JwtUserDetailsService jwtUserDetailsService) {
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

    public List<GetEventDTO> getAllEvent(HttpServletRequest request){
        User userOwner = getUserFromRequest(request);
        System.out.println(userOwner.getEmail());
        List<Event> eventList = new ArrayList<>();
        if (userOwner.getRole().equals("admin")){
            System.out.println("???????????? admin");
            eventList = eventRepository.findAllByOrderByEventStartTimeDesc();
        } else if (userOwner.getRole().equals("student")) {
            System.out.println("???????????? student");
            eventList = eventRepository.findAllByOwner(userOwner.getEmail());
        } else if (userOwner.getRole().equals("lecturer")){
            System.out.println("???????????? lecturer");
            List<Integer> categoriesId = eventCategoryOwnerRepository.findAllByUserId(userOwner.getId());
            System.out.println(categoriesId);
            eventList = eventRepository.findAllByEventCategory(categoriesId);
        }
        return listMapper.mapList(eventList, GetEventDTO.class, modelMapper);
    }

    public List<GetBlindEvent> getAllBlindEvent (){
        List<Event> eventList = new ArrayList<>();
        eventList = eventRepository.findAllByOrderByEventStartTimeDesc();
        return listMapper.mapList(eventList, GetBlindEvent.class, modelMapper);
    }

    public Object getEventById(HttpServletRequest request, Integer bookingId){
        User userOwner = getUserFromRequest(request);
        Event event = eventRepository.findById(bookingId)
                .orElseThrow(()->new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Customer id "+ bookingId+
                        "Does Not Exist !!!"
                ));
        
        if (userOwner.getRole().equals("student")) {
            if (!userOwner.getEmail().equals(event.getBookingEmail())) {
                return ValidationHandler.showError(HttpStatus.FORBIDDEN, "You not have permission this event");
            }

        }

        return modelMapper.map(event, GetEventDTO.class);
    }


    public Object addEvent(HttpServletRequest request,AddEventDTO newEvent, MultipartFile file){
        //check valid
        Set<ConstraintViolation<AddEventDTO>> violations = validator.validate(newEvent);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<AddEventDTO> constraintViolation : violations) {
                sb.append(constraintViolation.getMessage());
            }
            throw new ConstraintViolationException("Error occurred: " + sb.toString(), violations);
        }

        User userOwner = getUserFromRequest(request);

        if (userOwner != null) {
            if (userOwner.getRole().equals("student")) {
                if (!userOwner.getEmail().equals(newEvent.getBookingEmail())) {
                    return ValidationHandler.showError(HttpStatus.BAD_REQUEST, "the booking email must be the same as student's email");
                }
            }
        }

        Event addEventList = modelMapper.map(newEvent, Event.class);
        List<Event> eventList = eventRepository.findEventByEventCategoryIdEquals(addEventList.getEventCategoryId());

        System.out.println(checkTimeOverLap(newEvent.getEventStartTime(), newEvent.getEventDuration(),eventList ));
        if (!checkTimeOverLap(newEvent.getEventStartTime(), newEvent.getEventDuration(),eventList))  {
            Event addedEvent = eventRepository.saveAndFlush(addEventList);
            if(file != null) {
                if (!file.isEmpty()) {
                    storageService.store(file, addedEvent.getId());
                    return file.getOriginalFilename();
                }
            }
            return addedEvent;
        } else {
            return ValidationHandler.showError(HttpStatus.BAD_REQUEST, "Time is overlapping!! change date-time please");
        }

    }

    private Boolean checkTimeOverLap(LocalDateTime updateDateTime, Integer newEventDuration, List<Event> eventList) {
        LocalDateTime newEventStartTime = updateDateTime;
        LocalDateTime newEventEndTime = findEndDate(newEventStartTime, newEventDuration);
        for (Event event : eventList) {
            LocalDateTime eventStartTime = event.getEventStartTime();
            LocalDateTime eventEndTime = findEndDate(event.getEventStartTime(),
                    event.getEventDuration());
            if (newEventStartTime.isEqual(eventStartTime) ||
                    newEventStartTime.isBefore(eventStartTime) && newEventEndTime.isAfter(eventStartTime) ||
                    newEventStartTime.isBefore(eventEndTime) && newEventEndTime.isAfter(eventEndTime) ||
                    newEventStartTime.isBefore(eventStartTime) && newEventEndTime.isAfter(eventEndTime) ||
                    newEventStartTime.isAfter(eventStartTime) && newEventEndTime.isBefore(eventEndTime)) {
                return true;
            }
        }
        return false;
    }

    private LocalDateTime findEndDate(LocalDateTime eventStartTime, Integer duration) {
        LocalDateTime getEventEndTime = eventStartTime.plusMinutes(duration);
        return getEventEndTime;
    }

    public Object deleteEvent(HttpServletRequest request, Integer bookingId){
        User userOwner = getUserFromRequest(request);
        Event event = eventRepository.findById(bookingId).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        bookingId + " does not exist !!!"));

        if (userOwner.getRole().equals("student")) {
            if (!userOwner.getEmail().equals(event.getBookingEmail())) {
                return ValidationHandler.showError(HttpStatus.FORBIDDEN, "You not have permission this event");
            }

        }

        eventRepository.deleteById(bookingId);
        storageService.deleteFileById(bookingId);
        return event;
    }

    public Object updateEvent(HttpServletRequest request, UpdateEventDTO updateEvent, MultipartFile file, Integer bookingId){
        //check valid
        Set<ConstraintViolation<UpdateEventDTO>> violations = validator.validate(updateEvent);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<UpdateEventDTO> constraintViolation : violations) {
                sb.append(constraintViolation.getMessage());
            }
            throw new ConstraintViolationException("Error occurred: " + sb.toString(), violations);
        }



        User userOwner = getUserFromRequest(request);
        Event event = eventRepository.findById(bookingId).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        bookingId + " does not exist !!!"));

        if (userOwner.getRole().equals("student")) {
            if (!userOwner.getEmail().equals(event.getBookingEmail())) {
                return ValidationHandler.showError(HttpStatus.FORBIDDEN, "You not have permission this event");
            }

        }

        Event updateEventList = modelMapper.map(updateEvent, Event.class);
        List<Event> eventList = eventRepository.findEventByEventCategoryIdEquals(updateEventList.getEventCategoryId());
        Event update = eventRepository.findById(bookingId).orElseThrow(()->new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Booking id "+ updateEventList.getId()+
                "Does Not Exist !!!"
        ));
        eventList.remove(update);
        checkTimeOverLap(updateEvent.getEventStartTime(), updateEvent.getEventDuration(), eventList );

        event.setEventStartTime(updateEvent.getEventStartTime());
        event.setEventNote(updateEvent.getEventNote());
        event.setEventCategoryId(updateEventList.getEventCategoryId());
        Event updatedEvent = eventRepository.saveAndFlush(event);

        if(file != null) {
            if (!file.isEmpty()) {
                storageService.store(file, updatedEvent.getId());
                return file.getOriginalFilename();
            } else {
                storageService.deleteFileById(updatedEvent.getId());
            }
        } else {
            storageService.deleteFileById(updatedEvent.getId());
        }

        return updateEvent;
    }

    public List<GetEventDTO> getEventByCategory (ArrayList id){
        ArrayList<Event> filterEventList = new ArrayList<>();
        List<Event> eventList = eventRepository.findAllByOrderByEventStartTimeDesc();
        for(Event event : eventList){
            for(Object idCate : id){
                if(event.getEventCategoryId().getId() == idCate){
                    filterEventList.add(event);
                }
            }
        }
        return listMapper.mapList(filterEventList, GetEventDTO.class, modelMapper);
    }

    public List<GetEventDTO> getPastEvent(HttpServletRequest request){
        User userOwner = getUserFromRequest(request);

        LocalDateTime currentDateTime;
        currentDateTime = LocalDateTime.now();
        List<Event> eventList = eventRepository.findPastEvent(currentDateTime);
        List<Event> eventListFilter = eventListByRole(eventList, userOwner);

        return listMapper.mapList(eventListFilter, GetEventDTO.class, modelMapper);
    }
    public List<GetEventDTO> getFutureEvent(HttpServletRequest request){
        User userOwner = getUserFromRequest(request);
        LocalDateTime currentDateTime;
        currentDateTime = LocalDateTime.now();
        List<Event> eventList = eventRepository.findFutureEvent(currentDateTime);
        List<Event> eventListFilter = eventListByRole(eventList, userOwner);
        return listMapper.mapList(eventListFilter, GetEventDTO.class, modelMapper);
    }

    public List<GetEventDTO> getEventsByDate(HttpServletRequest request, Integer date){
        User userOwner = getUserFromRequest(request);
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        val dateTime = LocalDate.parse(date.toString(),formatter);
        LocalDateTime startTime = LocalDateTime.of(dateTime.getYear(), dateTime.getMonth(), dateTime.getDayOfMonth(), 00, 00, 00);
        LocalDateTime endTime = LocalDateTime.of(dateTime.getYear(), dateTime.getMonth(), dateTime.getDayOfMonth(), 23, 59, 59);
        List<Event> eventList = eventRepository.findEventsByDate(startTime,endTime);
        List<Event> eventListFilter = eventListByRole(eventList, userOwner);

        return listMapper.mapList(eventListFilter, GetEventDTO.class, modelMapper);
    }

    public List<Event> eventListByRole(List<Event> eventList, User userOwner) {
        List<Event> eventListFilter = new ArrayList<>();

        if (userOwner.getRole().equals("admin")){
            eventListFilter = eventList;
        } else if (userOwner.getRole().equals("student")) {
            for(Event event : eventList){
                if (event.getBookingEmail().equals(userOwner.getEmail())) {
                    eventListFilter.add(event);
                }
            }
        } else if (userOwner.getRole().equals("lecturer")){
            List<Integer> categoriesId = eventCategoryOwnerRepository.findAllByUserId(userOwner.getId());
            for(Event event : eventList){
                if (categoriesId.contains(event.getEventCategoryId().getId())) {
                    eventListFilter.add(event);
                }
            }
        }
        return eventListFilter;
    }
}

