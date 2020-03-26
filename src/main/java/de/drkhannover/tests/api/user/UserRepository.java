package de.drkhannover.tests.api.user;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import de.drkhannover.tests.api.user.jpa.User;

@Repository
interface UserRepository extends CrudRepository<User, Integer>{
    Optional<User> findByusername(String username);
    Iterable<User> findAll();
}
