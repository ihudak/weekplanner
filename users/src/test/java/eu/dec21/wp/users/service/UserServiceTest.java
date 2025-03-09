package eu.dec21.wp.users.service;

import eu.dec21.wp.exceptions.BadRequestException;
import eu.dec21.wp.exceptions.ResourceNotFoundException;
import eu.dec21.wp.users.dto.UserDto;
import eu.dec21.wp.users.dto.UserResponse;
import eu.dec21.wp.users.entity.User;
import eu.dec21.wp.users.entity.UserDirector;
import eu.dec21.wp.users.mapper.UserMapper;
import eu.dec21.wp.users.repository.UserRepository;
import eu.dec21.wp.users.service.impl.UserServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private final UserDirector userDirector;
    private List<User> users;

    UserServiceTest() {
        userDirector = new UserDirector();
    }

    @BeforeAll
    static void init() {
    }

    @BeforeEach
    void setUp() {
        users = new ArrayList<>();
//        lenient().when(userRepository.save(Mockito.any(User.class))).thenReturn(users.getFirst());

        // count
        lenient().when(userRepository.count()).thenAnswer((Answer<Long>) invocation -> (long) users.size());

        // find by id
        lenient().when(userRepository.findById(Mockito.any(Long.class))).then((Answer<Optional<User>>) invocation -> {
            int userId = invocation.getArgument(0, Long.class).intValue();
            if (!users.isEmpty()) {
                for (User user: users) {
                    if (userId == user.getId()) {
                        return Optional.of(user);
                    }
                }
            }
            return Optional.empty();
        });

        // find by email
        lenient().when(userRepository.findByEmail(Mockito.any(String.class))).then((Answer<Optional<User>>) invocation -> {
            String email = invocation.getArgument(0, String.class);
            if (!users.isEmpty()) {
                for (User user: users) {
                    if (user.getEmail().equals(email)) {
                        return Optional.of(user);
                    }
                }
            }
            return Optional.empty();
        });

        // save all
        lenient().when(userRepository.saveAll(Mockito.anyList())).then(new Answer<List<User>>() {
            long sequence = 1;

            @Override
            public List<User> answer(InvocationOnMock invocation) throws Throwable {
                List<User> users = invocation.getArgument(0);
                for (User user: users) {
                    user.setId(sequence++);
                }
                return users;
            }
        });

        // save
        lenient().when(userRepository.save(Mockito.any(User.class))).then(new Answer<User>() {
            long sequence = 1;

            @Override
            public User answer(InvocationOnMock invocation) throws Throwable {
                User user = invocation.getArgument(0, User.class);
                long userId = 0L;
                try{
                    userId = user.getId();
                }catch (Exception ignored){}
                if (0L == userId) {
                    user.setId(sequence++);
                }
                return user;
            }
        });

        // delete by id
        lenient().doAnswer((Answer<Long>) invocation -> {
            long id = invocation.getArgument(0, Long.class);
            if (users.isEmpty()) {
                return 0L;
            }
            users.removeIf(user -> id == user.getId());
            return id;
        }).when(userRepository).deleteById(Mockito.any(Long.class));

        // find all
        lenient().when(userRepository.findAll()).then((Answer<List<User>>) invocation -> users);

        // find paged
        Page<User> userPage = Mockito.mock(Page.class);
        lenient().when(userRepository.findAll(Mockito.any(Pageable.class))).thenReturn(userPage);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void createUser() {
        users.add(userDirector.constructRandomUser());
        assertThrows(BadRequestException.class, () -> userService.createUser(UserMapper.mapToUserDto(users.getFirst())));

        UserDto userDto = UserMapper.mapToUserDto(users.getFirst());
        userDto.setPassword("Sup3RsTr0nGP@s5w0rD!");
        UserDto savedUser = userService.createUser(userDto);

        assertEquals(1,                   savedUser.getId());

        assertTrue(savedUser.equals(UserMapper.mapToUserDto(users.getFirst())));
    }

    @Test
    void getUserById() {
        int numUsers = 3;
        long userIDToFind = 1;
        users = userDirector.constructRandomUsers(numUsers);

        userRepository.saveAll(users);

        assertEquals(numUsers, userRepository.count());

        UserDto user = userService.getUserById(userIDToFind);
        assertEquals(userIDToFind, user.getId());

        assertTrue(user.equals(UserMapper.mapToUserDto(users.get((int) userIDToFind - 1))));

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(99L));
    }

    @Test
    void getAllUsers() {
        int numUsers = 3;
        users = userDirector.constructRandomUsers(3);
        userRepository.saveAll(users);
        List<UserDto> userDtoList = userService.getAllUsers();

        assertEquals(numUsers, userDtoList.size());

        assertTrue(userDtoList.get(0).equals(UserMapper.mapToUserDto(users.get(0))));
        assertTrue(userDtoList.get(1).equals(UserMapper.mapToUserDto(users.get(1))));
        assertTrue(userDtoList.get(2).equals(UserMapper.mapToUserDto(users.get(2))));

        assertEquals(3, userService.count());
    }

    @Test
    void getAllUsersPaged() {
        UserResponse userResponse = userService.getAllUser(1,10);
        assertNotNull(userResponse);
    }

    @Test
    void updateUser() {
        String newPassword = "An0Th3RP@5Sw0rD!";
        users = userDirector.constructRandomUsers(1);
        UserDto userDto = UserMapper.mapToUserDto(userRepository.save(users.getFirst()));
        String newName = "Bart";
        userDto.setFirstName(newName);
        userService.updateUser(userDto.getId(), userDto);

        assertEquals(1, userRepository.count());
        assertEquals(newName, users.getFirst().getFirstName());

        String oldPassword = users.getFirst().getPassword();
        userDto.setPassword(newPassword);
        userService.updateUser(userDto.getId(), userDto);
        assertNotEquals(users.getFirst().getPassword(), oldPassword);

        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser((long) (users.size() + 1), userDto));
    }

    @Test
    void deleteUser() {
        int numUsers = 3;
        users = userDirector.constructRandomUsers(numUsers);
        userRepository.saveAll(users);
        assertEquals(numUsers, userRepository.count());

        long userToDel = 1;
        assertTrue(userRepository.findById(userToDel).isPresent());

        userService.deleteUser(userToDel);
        assertEquals(numUsers - 1, userRepository.count());
        assertFalse(userRepository.findById(userToDel).isPresent());

        long user2ToDel = numUsers + 5;
        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(user2ToDel));
    }

    @Test
    void findUserByEmail() {
        int numUsers = 3;
        users = userDirector.constructRandomUsers(numUsers);
        userRepository.saveAll(users);
        assertEquals(numUsers, userRepository.count());

        String emailToFind = users.get(1).getEmail();
        UserDto foundUser = userService.findUserByEmail(emailToFind);
        assertTrue(foundUser.equals(UserMapper.mapToUserDto(users.get(1))));

        assertThrows(ResourceNotFoundException.class, () -> userService.findUserByEmail("non.existing@email.rum"));
    }

    @Test
    void count() {
        int numUsers = 3;
        users = userDirector.constructRandomUsers(numUsers);
        userRepository.saveAll(users);
        assertEquals(numUsers, userService.count());
    }
}