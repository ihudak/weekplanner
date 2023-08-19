package eu.dec21.wp.users.service.impl;

import eu.dec21.wp.exceptions.ResourceNotFoundException;
import eu.dec21.wp.users.dto.UserDto;
import eu.dec21.wp.users.entity.User;
import eu.dec21.wp.users.mapper.UserMapper;
import eu.dec21.wp.users.repository.UserRepository;
import eu.dec21.wp.users.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.mapToUser(userDto);
        User savedUser = userRepository.save(user);
        return UserMapper.mapToUserDto(savedUser);
    }

    @Override
    public UserDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User does not exist with the given ID: " + userId));

        return UserMapper.mapToUserDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(UserMapper::mapToUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto updateUser(Long userId, UserDto updatedUserDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User does not exist with the given ID: " + userId));

        user.setEmail(updatedUserDto.getEmail());
        user.setFirstName(updatedUserDto.getFirstName());
        user.setLastName(updatedUserDto.getLastName());
        user.setAuthSystem(updatedUserDto.getAuthSystem());
        user.setAuthID(updatedUserDto.getAuthID());

        User updatedUser = userRepository.save(user);

        return UserMapper.mapToUserDto(updatedUser);
    }

    @Override
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User does not exist with the given ID: " + userId));

        userRepository.deleteById(userId);
    }

    @Override
    public UserDto findUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                        new ResourceNotFoundException("User does not exist with the given email: " + email));

        return UserMapper.mapToUserDto(user);
    }

    @Override
    public long count() {
        return userRepository.count();
    }
}
