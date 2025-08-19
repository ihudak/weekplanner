package eu.dec21.wp.users.service.impl;

import eu.dec21.wp.exceptions.BadRequestException;
import eu.dec21.wp.exceptions.ResourceNotFoundException;
import eu.dec21.wp.users.dto.UserDto;
import eu.dec21.wp.users.dto.UserResponse;
import eu.dec21.wp.users.entity.User;
import eu.dec21.wp.users.mapper.UserMapper;
import eu.dec21.wp.users.repository.UserRepository;
import eu.dec21.wp.users.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;


    @Transactional
    @Override
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.mapToUser(userDto);
        if (null == userDto.getPassword() || userDto.getPassword().isEmpty()) {
            throw new BadRequestException("User must have password: " + userDto.getEmail());
        }

        user.setPassword(userDto.getPassword());

        try {
            User savedUser = userRepository.save(user);
            return UserMapper.mapToUserDto(savedUser);
        } catch (ObjectOptimisticLockingFailureException e) {
            // Reload entity and retry or inform user
            throw new ConcurrentModificationException("User was updated by another user");
        }
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
    public UserResponse getAllUser(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<User> users = userRepository.findAll(pageable);
        List<User> userList = users.getContent();
        List<UserDto> userDtoList = userList.stream().map(UserMapper::mapToUserDto).toList();

        UserResponse userResponse = new UserResponse();
        userResponse.setContent(userDtoList);
        userResponse.setPageNo(users.getNumber());
        userResponse.setPageSize(users.getSize());
        userResponse.setTotalElements(users.getTotalElements());
        userResponse.setTotalPages(users.getTotalPages());
        userResponse.setLast(users.isLast());

        return userResponse;
    }

    @Transactional
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

        if (null != updatedUserDto.getPassword() && !updatedUserDto.getPassword().isEmpty()) {
            user.setPassword(updatedUserDto.getPassword());
        }

        try {
            User updatedUser = userRepository.save(user);
            return UserMapper.mapToUserDto(updatedUser);
        } catch (ObjectOptimisticLockingFailureException e) {
            // Reload entity and retry or inform user
            throw new ConcurrentModificationException("Category was updated by another user");
        }
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
