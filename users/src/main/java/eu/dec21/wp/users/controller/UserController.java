package eu.dec21.wp.users.controller;

import eu.dec21.wp.users.dto.UserDto;
import eu.dec21.wp.users.dto.UserResponse;
import eu.dec21.wp.users.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class UserController {
    private UserService userService;

    @PostMapping("")
    @Operation(summary = "Create a new User")
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto) {
        UserDto savedUser = userService.createUser(userDto);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    @Operation(summary = "Get User by ID")
    public ResponseEntity<UserDto> getUserById(@PathVariable("id") Long userId) {
        UserDto userDto = userService.getUserById(userId);
        return ResponseEntity.ok(userDto);
    }

    @GetMapping("")
    @Operation(summary = "Get all Users")
    public ResponseEntity<UserResponse> getAllUsers(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize
    ) {
        return new ResponseEntity<>(userService.getAllUser(pageNo, pageSize), HttpStatus.OK);
    }

    @PutMapping("{id}")
    @Operation(summary = "Update User by ID")
    public ResponseEntity<UserDto> updateUser(@PathVariable("id") Long userId,
                                              @RequestBody UserDto updatedUser) {
        UserDto userDto = userService.updateUser(userId, updatedUser);
        return ResponseEntity.ok(userDto);
    }

    @DeleteMapping("{id}")
    @Operation(summary = "Delete User by ID")
    public ResponseEntity<String> deleteUserById(@PathVariable("id") Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok("User deleted with ID: " + userId);
    }


    @GetMapping("/find")
    @Operation(summary = "Find User by email address")
    public ResponseEntity<UserDto> getUserByEmail(@Parameter(name="email", description = "email address", example = "pbrown.gmail.com") @RequestParam String email) {
        UserDto userDto = userService.findUserByEmail(email);
        return ResponseEntity.ok(userDto);
    }
}
