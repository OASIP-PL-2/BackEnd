package sit.project221.oasipbackend.services;

import lombok.val;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.ObjectError;
import org.springframework.web.server.ResponseStatusException;
import sit.project221.oasipbackend.config.JwtTokenUtil;
import sit.project221.oasipbackend.controllers.ValidationHandler;
import sit.project221.oasipbackend.dtos.AddEventDTO;
import sit.project221.oasipbackend.dtos.GetEventDTO;
import sit.project221.oasipbackend.dtos.UpdateEventDTO;
import sit.project221.oasipbackend.entities.Event;
import sit.project221.oasipbackend.entities.User;
import sit.project221.oasipbackend.repositories.EventRepository;
import sit.project221.oasipbackend.repositories.UserRepository;
import sit.project221.oasipbackend.utils.ListMapper;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ListMapper listMapper;

    private final JwtTokenUtil jwtTokenUtill;
    private final JwtUserDetailsService jwtUserDetailsService;

    public EventService(JwtTokenUtil jwtTokenUtill, JwtUserDetailsService jwtUserDetailsService) {
        this.jwtTokenUtill = jwtTokenUtill;
        this.jwtUserDetailsService = jwtUserDetailsService;
    }

    public User getUserFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        String userEmail = jwtTokenUtill.getUsernameFromToken(token);
        return  userRepository.findByEmail(userEmail);
    }

    public List<GetEventDTO> getAllEvent(HttpServletRequest request){
        User userOwner = getUserFromRequest(request);
        System.out.println(userOwner.getEmail());
        List<Event> eventList = new ArrayList<>();

        if (userOwner.getRole().equals("admin")){
            System.out.println("เข้า admin");
            eventList = eventRepository.findAllByOrderByEventStartTimeDesc();
        } else if (userOwner.getRole().equals("student")) {
            System.out.println("เข้า student");
            eventList = eventRepository.findAllByOwner(userOwner.getEmail());
        }
        return listMapper.mapList(eventList, GetEventDTO.class, modelMapper);
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

    public Object addEvent(HttpServletRequest request, AddEventDTO newEvent){
        User userOwner = getUserFromRequest(request);

        if (userOwner.getRole().equals("student")) {
            if (!userOwner.getEmail().equals(newEvent.getBookingEmail())) {
                return ValidationHandler.showError(HttpStatus.BAD_REQUEST, "the booking email must be the same as student's email");
            }
        }

        Event addEventList = modelMapper.map(newEvent, Event.class);
        List<Event> eventList = eventRepository.findEventByEventCategoryIdEquals(addEventList.getEventCategoryId());
        checkTimeOverLap(newEvent.getEventStartTime(), newEvent.getEventDuration(),eventList );
        return eventRepository.saveAndFlush(addEventList);
    }

    private void checkTimeOverLap(LocalDateTime updateDateTime, Integer newEventDuration, List<Event> eventList) {
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
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Time is overlapping!! change date-time please");
            }
        }
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
        return event;
    }

    public Object updateEvent(HttpServletRequest request, UpdateEventDTO updateEvent, Integer bookingId){
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
        eventRepository.saveAndFlush(event);
        return updateEvent;
    }

    public List<GetEventDTO> getEventByCategory (Integer id){
        ArrayList<Event> filterEventList = new ArrayList<>();
        List<Event> eventList = eventRepository.findAllByOrderByEventStartTimeDesc();
        for(Event event : eventList){
            if(event.getEventCategoryId().getId() == id){
                filterEventList.add(event);
            }
        }
        return listMapper.mapList(filterEventList, GetEventDTO.class, modelMapper);
    }

    public List<GetEventDTO> getPastEvent(){
        LocalDateTime currentDateTime;
        currentDateTime = LocalDateTime.now();
        List<Event> eventList = eventRepository.findPastEvent(currentDateTime);
        return listMapper.mapList(eventList, GetEventDTO.class, modelMapper);
    }
    public List<GetEventDTO> getFutureEvent(){
        LocalDateTime currentDateTime;
        currentDateTime = LocalDateTime.now();
        List<Event> eventList = eventRepository.findFutureEvent(currentDateTime);
        return listMapper.mapList(eventList, GetEventDTO.class, modelMapper);
    }

    public List<GetEventDTO> getEventsByDate(Integer date){
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        val dateTime = LocalDate.parse(date.toString(),formatter);
        LocalDateTime startTime = LocalDateTime.of(dateTime.getYear(), dateTime.getMonth(), dateTime.getDayOfMonth(), 00, 00, 00);
        LocalDateTime endTime = LocalDateTime.of(dateTime.getYear(), dateTime.getMonth(), dateTime.getDayOfMonth(), 23, 59, 59);
//        LocalDateTime startTime = LocalDateTime.of(2022, Month.AUGUST, 20, 00, 00, 00);
//        LocalDateTime endTime = LocalDateTime.of(2022, Month.AUGUST, 20, 23, 59, 59);
        List<Event> eventList = eventRepository.findEventsByDate(startTime,endTime);
        return listMapper.mapList(eventList, GetEventDTO.class, modelMapper);
    }
}




//    public List<GetEventDTO> getPastEvent(){
//        LocalDateTime currentDateTime;
//        currentDateTime = LocalDateTime.now();
//        List<Event> eventList = eventRepository.findEventByEventStartTimeIsBeforeOrderByEventStartTimeDesc(currentDateTime);
//        return listMapper.mapList(eventList, GetEventDTO.class, modelMapper);
//    }
//
//    public List<GetEventDTO> getUpcomingEvent(){
//        LocalDateTime currentDateTime;
//        currentDateTime = LocalDateTime.now();
//        List<Event> eventList = eventRepository.findEventByEventStartTimeIsAfterOrderByEventStartTimeAsc(currentDateTime);
//        return listMapper.mapList(eventList, GetEventDTO.class, modelMapper);
//    }
