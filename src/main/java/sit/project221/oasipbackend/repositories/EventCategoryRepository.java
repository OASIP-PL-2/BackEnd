package sit.project221.oasipbackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import sit.project221.oasipbackend.entities.EventCategory;

import java.util.List;

public interface EventCategoryRepository extends JpaRepository<EventCategory, Integer> {
    public List<EventCategory> findAllByOrderByIdDesc();
}
