package sit.project221.oasipbackend.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.project221.oasipbackend.dtos.AddEventDTO;
import sit.project221.oasipbackend.dtos.GetEventDTO;
import sit.project221.oasipbackend.dtos.UpdateEventDTO;
import sit.project221.oasipbackend.entities.Event;
import sit.project221.oasipbackend.repositories.EventRepository;
import sit.project221.oasipbackend.utils.ListMapper;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ListMapper listMapper;


    public List<GetEventDTO> getAllEvent(){
        List<Event> eventList = eventRepository.findAllByOrderByEventStartTimeDesc();
        return listMapper.mapList(eventList, GetEventDTO.class, modelMapper);
    }

    public GetEventDTO getEventById(Integer bookingId){
        Event event = eventRepository.findById(bookingId)
                .orElseThrow(()->new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Customer id "+ bookingId+
                        "Does Not Exist !!!"
                ));
        return modelMapper.map(event, GetEventDTO.class);
    }

    public Event addEvent(AddEventDTO newEvent){
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

    public void deleteEvent(Integer bookingId){
        eventRepository.findById(bookingId).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        bookingId + " does not exist !!!"));
        eventRepository.deleteById(bookingId);
    }

    public UpdateEventDTO updateEvent(UpdateEventDTO updateEvent, Integer bookingId){
        Event event = eventRepository.findById(bookingId).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND, bookingId + "does not exist!!!"));

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
        eventRepository.saveAndFlush(event);
        return updateEvent;
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
