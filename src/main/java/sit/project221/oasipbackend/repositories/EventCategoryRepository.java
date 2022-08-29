package sit.project221.oasipbackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sit.project221.oasipbackend.entities.EventCategory;

import javax.transaction.Transactional;
import java.util.List;

public interface EventCategoryRepository extends JpaRepository<EventCategory, Integer> {
    public List<EventCategory> findAllByOrderByIdDesc();


//    @Modifying
//    @Query(value = "insert into eventCategory (eventCategoryName , eventDuration) VALUES (:CategoryName,:Duration)", nativeQuery = true)
//    @Transactional
//    void insertCategory(@Param("CategoryName") String name, @Param("Duration") int duaraion);
}
