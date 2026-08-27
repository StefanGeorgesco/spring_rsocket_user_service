package fr.stefangeorgesco.rsocketuserservice.repository;

import fr.stefangeorgesco.rsocketuserservice.entity.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveMongoRepository<User, String> {
    Mono<User> findByName(String name);
}
