package fr.stefangeorgesco.rsocketuserservice.util;

import fr.stefangeorgesco.rsocketuserservice.dto.UserDto;
import fr.stefangeorgesco.rsocketuserservice.entity.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EntityDtoUtilTest {

    @Test
    void toDto() {
        User user = new User();
        user.setId("1");
        user.setName("John Doe");
        user.setBalance(100);
        UserDto userDto = EntityDtoUtil.toDto(user);
        assertEquals(user.getId(), userDto.id());
        assertEquals(user.getName(), userDto.name());
        assertEquals(user.getBalance(), userDto.balance());
    }

    @Test
    void toEntity() {
        UserDto userDto = new UserDto("1", "John Doe", 100);
        User user = EntityDtoUtil.toEntity(userDto);
        assertEquals(userDto.id(), user.getId());
        assertEquals(userDto.name(), user.getName());
        assertEquals(userDto.balance(), user.getBalance());
    }
}