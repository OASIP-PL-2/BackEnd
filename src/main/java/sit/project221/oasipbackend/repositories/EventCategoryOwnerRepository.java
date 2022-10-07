package sit.project221.oasipbackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sit.project221.oasipbackend.entities.Event;
import sit.project221.oasipbackend.entities.EventCategoryOwner;

import java.util.List;

public interface EventCategoryOwnerRepository extends JpaRepository<EventCategoryOwner, Integer> {

    @Query("select a.eventCategory.id from EventCategoryOwner a where a.user.id = :id ")
    List<Integer> findAllByUserId(@Param("id") int id);
}