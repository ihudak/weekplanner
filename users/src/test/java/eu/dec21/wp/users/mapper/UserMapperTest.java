package eu.dec21.wp.users.mapper;

import eu.dec21.wp.users.dto.UserDto;
import eu.dec21.wp.users.entity.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserMapperTest {

    @Test
    void mapToUserDto() {
        User user = new User();

        user.setId(1L);
        user.setFirstName("Boo");
        user.setLastName("Foo");
        user.setEmail("boo@foo.com");
        user.setAuthSystem("fb");
        user.setAuthID("fb4343423");

        UserDto userDto = UserMapper.mapToUserDto(user);

        assertEquals(user.getId(),         userDto.getId());
        assertEquals(user.getEmail(),      userDto.getEmail());
        assertEquals(user.getFirstName(),  userDto.getFirstName());
        assertEquals(user.getLastName(),   userDto.getLastName());
        assertEquals(user.getAuthSystem(), userDto.getAuthSystem());
        assertEquals(user.getAuthID(),     userDto.getAuthID());
    }

    @Test
    void mapToUser() {
        UserDto userDto = new UserDto();

        userDto.setId(1L);
        userDto.setFirstName("Boo");
        userDto.setLastName("Foo");
        userDto.setEmail("boo@foo.com");
        userDto.setAuthSystem("fb");
        userDto.setAuthID("fb4343423");

        User user = UserMapper.mapToUser(userDto);

        assertEquals(userDto.getId(),         user.getId());
        assertEquals(userDto.getEmail(),      user.getEmail());
        assertEquals(userDto.getFirstName(),  user.getFirstName());
        assertEquals(userDto.getLastName(),   user.getLastName());
        assertEquals(userDto.getAuthSystem(), user.getAuthSystem());
        assertEquals(userDto.getAuthID(),     user.getAuthID());
    }
}