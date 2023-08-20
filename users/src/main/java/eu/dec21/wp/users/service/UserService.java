package eu.dec21.wp.users.service;

import eu.dec21.wp.users.dto.UserDto;
import eu.dec21.wp.users.dto.UserResponse;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto userDto);
    UserDto getUserById(Long userId);
    List<UserDto> getAllUsers();
    UserResponse getAllUser(int pageNo, int pageSize);
    UserDto updateUser(Long userId, UserDto updatedUser);
    void deleteUser(Long userId);
    UserDto findUserByEmail(String email);

    long count();
}
