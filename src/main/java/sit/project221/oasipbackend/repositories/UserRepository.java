package sit.project221.oasipbackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.project221.oasipbackend.entities.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {
    List<User> findAllByOrderByNameAsc();
    public User findByEmail(String email);
    public boolean existsByEmail(String email);
}