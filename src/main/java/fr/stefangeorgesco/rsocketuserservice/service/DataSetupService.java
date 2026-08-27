package fr.stefangeorgesco.rsocketuserservice.service;

import fr.stefangeorgesco.rsocketuserservice.entity.User;
import fr.stefangeorgesco.rsocketuserservice.repository.UserRepository;
import org.jspecify.annotations.NullMarked;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@NullMarked
public class DataSetupService implements CommandLineRunner {

    private final UserRepository userRepository;

    public DataSetupService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        Flux.just("sam", "mike", "jake")
                .flatMap(name -> userRepository.findByName(name)
                        .doOnNext(user -> System.out.println("User '" + name + "' already exists: " + user))
                        .switchIfEmpty(userRepository.save(new User(name, 10000))
                                .doOnNext(user -> System.out.println("Created user: " + user))))
                .subscribe();
    }
}
