package eu.dec21.wp.users.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.dec21.wp.users.dto.UserDto;
import eu.dec21.wp.users.dto.UserResponse;
import eu.dec21.wp.users.entity.User;
import eu.dec21.wp.users.entity.UserDirector;
import eu.dec21.wp.users.mapper.UserMapper;
import eu.dec21.wp.users.service.UserService;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;
    private List<User> users;
    private final UserDirector userDirector;

    UserControllerTest() {
        userDirector = new UserDirector();
    }

    @BeforeEach
    public void init() {
        users = new ArrayList<>();
    }

    @Test
    public void UserController_CreateUser_ReturnCreated() throws Exception {
        given(userService.createUser(any())).willAnswer(invocation -> invocation.getArgument(0));

        users.add(userDirector.constructRandomUser());
        UserDto userDto = UserMapper.mapToUserDto(users.get(0));

        ResultActions response = mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)));

        response.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName", CoreMatchers.is(userDto.getFirstName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName", CoreMatchers.is(userDto.getLastName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", CoreMatchers.is(userDto.getEmail())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.authSystem", CoreMatchers.is(userDto.getAuthSystem())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.authID", CoreMatchers.is(userDto.getAuthID())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.suspended", CoreMatchers.is(userDto.isSuspended())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is(userDto.getId().intValue())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void UserController_UpdateUser_ReturnUpdated() throws Exception {
        UserDto userDto = UserMapper.mapToUserDto(userDirector.constructRandomUser());
        when(userService.updateUser(any(Long.class), any(UserDto.class))).thenReturn(userDto);

        ResultActions response = mockMvc.perform(put("/api/v1/users/" + userDto.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName", CoreMatchers.is(userDto.getFirstName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName", CoreMatchers.is(userDto.getLastName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", CoreMatchers.is(userDto.getEmail())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.authSystem", CoreMatchers.is(userDto.getAuthSystem())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.authID", CoreMatchers.is(userDto.getAuthID())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.suspended", CoreMatchers.is(userDto.isSuspended())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is(userDto.getId().intValue())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void UserController_GetAllUsers_ReturnResponseDto() throws Exception {
        ArrayList<UserDto> userDtos = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            userDtos.add(UserMapper.mapToUserDto(userDirector.constructRandomUser()));
        }

        UserResponse responseDto = UserResponse.builder().pageSize(10).last(true).pageNo(1).content(userDtos).build();
        when(userService.getAllUser(1, 10)).thenReturn(responseDto);

        ResultActions response = mockMvc.perform(get("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .param("pageNo", "1")
                .param("pageSize", "10"));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.size()", CoreMatchers.is(userDtos.size())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.[0].firstName", CoreMatchers.is(userDtos.get(0).getFirstName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.[0].lastName", CoreMatchers.is(userDtos.get(0).getLastName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.[0].email", CoreMatchers.is(userDtos.get(0).getEmail())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.[0].authSystem", CoreMatchers.is(userDtos.get(0).getAuthSystem())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.[0].authID", CoreMatchers.is(userDtos.get(0).getAuthID())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.[0].suspended", CoreMatchers.is(userDtos.get(0).isSuspended())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.[0].id", CoreMatchers.is(userDtos.get(0).getId().intValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.[9].firstName", CoreMatchers.is(userDtos.get(9).getFirstName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.[9].lastName", CoreMatchers.is(userDtos.get(9).getLastName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.[9].email", CoreMatchers.is(userDtos.get(9).getEmail())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.[9].authSystem", CoreMatchers.is(userDtos.get(9).getAuthSystem())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.[9].authID", CoreMatchers.is(userDtos.get(9).getAuthID())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.[9].suspended", CoreMatchers.is(userDtos.get(9).isSuspended())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.[9].id", CoreMatchers.is(userDtos.get(9).getId().intValue())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void UserController_UserDetail_ReturnUserDto() throws Exception {
        UserDto userDto = UserMapper.mapToUserDto(userDirector.constructRandomUser());
        when(userService.getUserById(userDto.getId())).thenReturn(userDto);

        ResultActions response = mockMvc.perform(get("/api/v1/users/" + userDto.getId())
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName", CoreMatchers.is(userDto.getFirstName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName", CoreMatchers.is(userDto.getLastName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", CoreMatchers.is(userDto.getEmail())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.authSystem", CoreMatchers.is(userDto.getAuthSystem())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.authID", CoreMatchers.is(userDto.getAuthID())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.suspended", CoreMatchers.is(userDto.isSuspended())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is(userDto.getId().intValue())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void UserController_FindUser_ReturnUserDto() throws Exception {
        UserDto userDto = UserMapper.mapToUserDto(userDirector.constructRandomUser());
        when(userService.findUserByEmail(userDto.getEmail())).thenReturn(userDto);

        ResultActions response = mockMvc.perform(get("/api/v1/users/find?email=" + userDto.getEmail())
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName", CoreMatchers.is(userDto.getFirstName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName", CoreMatchers.is(userDto.getLastName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", CoreMatchers.is(userDto.getEmail())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.authSystem", CoreMatchers.is(userDto.getAuthSystem())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.authID", CoreMatchers.is(userDto.getAuthID())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.suspended", CoreMatchers.is(userDto.isSuspended())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is(userDto.getId().intValue())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void UserController_DeleteUser_ReturnString() throws Exception {
        long userId = 1L;
        doNothing().when(userService).deleteUser(any(Long.class));

        ResultActions response = mockMvc.perform(delete("/api/v1/users/" + userId)
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", CoreMatchers.is("User deleted with ID: " + (int) userId)));
    }
}
