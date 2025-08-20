package eu.dec21.wp.users.mapper;

import eu.dec21.wp.users.dto.UserDto;
import eu.dec21.wp.users.entity.User;
import eu.dec21.wp.users.entity.UserBuilder;

import java.util.Optional;

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
        UserBuilder userBuilder = new UserBuilder()
                .setFirstName(userDto.getFirstName())
                .setLastName(userDto.getLastName())
                .setEmail(userDto.getEmail())
                .setAuthSystem(userDto.getAuthSystem())
                .setAuthID(userDto.getAuthID())
                .setSuspended(userDto.isSuspended());

        Optional.ofNullable(userDto.getId()).ifPresent(userBuilder::setId);

        return userBuilder.build();
    }
}
