package eu.dec21.wp.users.mapper;

import eu.dec21.wp.users.dto.UserDto;
import eu.dec21.wp.users.entity.User;

public class UserMapper {
    public static UserDto mapToUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                null, // keep password unfetchable
                user.getAuthSystem(),
                user.getAuthID(),
                user.isSuspended()
        );
    }

    public static User mapToUser(UserDto userDto) {
        return new User(
                userDto.getId(),
                userDto.getFirstName(),
                userDto.getLastName(),
                userDto.getEmail(),
                null,
                userDto.getAuthSystem(),
                userDto.getAuthID(),
                userDto.isSuspended()
        );
    }
}
