package sit.project221.oasipbackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sit.project221.oasipbackend.entities.Event;
import sit.project221.oasipbackend.entities.EventCategory;

import java.time.LocalDateTime;
import java.util.List;


public interface EventRepository extends JpaRepository<Event, Integer> {
    public List<Event> findAllByOrderByEventStartTimeDesc();
    public List<Event> findEventByEventCategoryIdEquals(EventCategory eventCategory);

    @Query("select a from Event a where a.bookingEmail = :ownerEmail order by a.eventStartTime DESC")
    List<Event> findAllByOwner(@Param("ownerEmail") String ownerEmail);

    @Query("select a from Event a where a.eventStartTime <= :currentDateTime order by a.eventStartTime DESC")
    List<Event> findPastEvent(@Param("currentDateTime") LocalDateTime currentDateTime);
    @Query("select a from Event a where a.eventStartTime >= :currentDateTime")
    List<Event> findFutureEvent(@Param("currentDateTime") LocalDateTime currentDateTime);

    @Query("select a from Event a where a.eventStartTime >= :startTime and a.eventStartTime <= :endTime")
    List<Event> findEventsByDate(@Param("startTime") LocalDateTime startTime,@Param("endTime") LocalDateTime endTime);
}

    //    public List<Event> findAllByOrderByEventStartTimeAsc();
//    public List<Event> findEventByEventCategoryId(Integer categoryId);
//    public List<Event> findByBookingNameContaining(String name);
//    public List<Event> findEventByEventStartTimeBetweenOrderByEventStartTimeAsc(LocalDateTime fromDate, LocalDateTime toDate);
//    public List<Event> findEventByEventStartTimeIsBeforeOrderByEventStartTimeDesc(LocalDateTime currentDateTime);
//    public List<Event> findEventByEventStartTimeIsAfterOrderByEventStartTimeAsc(LocalDateTime currentDateTime);