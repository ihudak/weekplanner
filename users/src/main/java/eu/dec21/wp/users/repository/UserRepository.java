package eu.dec21.wp.users.repository;

import eu.dec21.wp.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
