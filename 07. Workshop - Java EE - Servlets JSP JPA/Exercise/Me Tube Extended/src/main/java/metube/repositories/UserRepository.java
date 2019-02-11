package metube.repositories;

import metube.domain.entities.User;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, String> {

    Optional<User> findById(String id);

    Optional<User> findByUsername(String username);
}
