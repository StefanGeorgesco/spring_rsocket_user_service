package fr.stefangeorgesco.rsocketuserservice.service;

import fr.stefangeorgesco.rsocketuserservice.dto.UserDto;
import fr.stefangeorgesco.rsocketuserservice.repository.UserRepository;
import fr.stefangeorgesco.rsocketuserservice.util.EntityDtoUtil;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public Flux<UserDto> getAllUsers() {
        return repository.findAll()
                .map(EntityDtoUtil::toDto);
    }

    public Mono<UserDto> getUserById(String id) {
        return repository.findById(id)
                .map(EntityDtoUtil::toDto);
    }

    public Mono<UserDto> createUser(Mono<UserDto> userDtoMono) {
        return userDtoMono
                .map(EntityDtoUtil::toEntity)
                .flatMap(repository::save)
                .map(EntityDtoUtil::toDto);
    }

    public Mono<UserDto> updateUser(String id, Mono<UserDto> userDtoMono) {
        return repository.findById(id)
                .flatMap(existingUser -> userDtoMono
                        .map(EntityDtoUtil::toEntity)
                        .doOnNext(user -> user.setId(existingUser.getId()))
                        .flatMap(repository::save)
                        .map(EntityDtoUtil::toDto));
    }

    public Mono<Void> deleteUser(String id) {
        return repository.deleteById(id);
    }
}
