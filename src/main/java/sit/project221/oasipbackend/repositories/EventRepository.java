package sit.project221.oasipbackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.project221.oasipbackend.entities.Event;
import sit.project221.oasipbackend.entities.EventCategory;

import java.time.LocalDateTime;
import java.util.List;


public interface EventRepository extends JpaRepository<Event, Integer> {
    public List<Event> findAllByOrderByEventStartTimeDesc();
    public List<Event> findEventByEventCategoryIdEquals(EventCategory eventCategory);
}

    //    public List<Event> findAllByOrderByEventStartTimeAsc();
//    public List<Event> findEventByEventCategoryId(Integer categoryId);
//    public List<Event> findByBookingNameContaining(String name);
//    public List<Event> findEventByEventStartTimeBetweenOrderByEventStartTimeAsc(LocalDateTime fromDate, LocalDateTime toDate);
//    public List<Event> findEventByEventStartTimeIsBeforeOrderByEventStartTimeDesc(LocalDateTime currentDateTime);
//    public List<Event> findEventByEventStartTimeIsAfterOrderByEventStartTimeAsc(LocalDateTime currentDateTime);