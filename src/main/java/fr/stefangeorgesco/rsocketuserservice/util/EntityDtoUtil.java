package fr.stefangeorgesco.rsocketuserservice.util;

import fr.stefangeorgesco.rsocketuserservice.dto.UserDto;
import fr.stefangeorgesco.rsocketuserservice.entity.User;
import org.springframework.beans.BeanUtils;

public class EntityDtoUtil {

    private EntityDtoUtil() {
    }

    public static UserDto toDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getBalance());
    }

    public static User toEntity(UserDto userDto) {
        User user = new User();
        BeanUtils.copyProperties(userDto, user);
        return user;
    }
}
