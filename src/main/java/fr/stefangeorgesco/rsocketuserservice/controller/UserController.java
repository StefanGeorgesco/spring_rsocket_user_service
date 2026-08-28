package fr.stefangeorgesco.rsocketuserservice.controller;

import fr.stefangeorgesco.rsocketuserservice.domain.OperationType;
import fr.stefangeorgesco.rsocketuserservice.dto.UserDto;
import fr.stefangeorgesco.rsocketuserservice.service.UserService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@MessageMapping("user")
@SuppressWarnings({"JavaPrintToLogpoint", "CallingSubscribeInNonBlockingScope"})
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @MessageMapping("get.all")
    public Flux<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @MessageMapping("get.{id}")
    public Mono<UserDto> getUserById(@DestinationVariable String id) {
        return userService.getUserById(id);
    }

    @MessageMapping("create")
    public Mono<UserDto> createUser(Mono<UserDto> userDtoMono) {
        return userService.createUser(userDtoMono);
    }

    @MessageMapping("update.{id}")
    public Mono<UserDto> updateUser(@DestinationVariable String id, Mono<UserDto> userDtoMono) {
        return userService.updateUser(id, userDtoMono);
    }

    @MessageMapping("delete.{id}")
    public Mono<Void> deleteUser(@DestinationVariable String id) {
        return userService.deleteUser(id);
    }

    @MessageMapping("operation")
    public Mono<Void> userOperation(@Header("operation-type") OperationType operationType,
                                    Mono<UserDto> userDtoMono){
        System.out.println(operationType);
        userDtoMono.doOnNext(System.out::println).subscribe();
        return Mono.empty();
    }
}
