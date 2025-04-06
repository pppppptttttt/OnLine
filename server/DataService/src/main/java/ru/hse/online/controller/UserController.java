package ru.hse.online.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hse.online.model.User;
import ru.hse.online.service.UserService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get user by ID or email")
    @GetMapping("/get/user/")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = User.class))),
    })
    public ResponseEntity<User> getUserByIdOrEmail(
            @Parameter(description = "Email filter")
            @RequestParam(name = "email", required = false) String email,
            @Parameter(description = "User ID filter")
            @RequestParam(name = "userId", required = false) UUID userId) {
        if (userId != null) {
            return ResponseEntity.ok(userService.getUserById(userId));
        } else if (email != null) {
            return ResponseEntity.ok(userService.getUserByEmail(email));
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Create new user")
    @PostMapping("/create/user")
    @ApiResponses({
            @ApiResponse(responseCode = "204"),
    })
    public ResponseEntity<Void> createUser(
            @Valid @RequestBody
            @Schema(implementation = User.class, example = """
                    {
                        "userId": "770e8400-e29b-41d4-a716-446655440000",
                        "username": "anton",
                        "email": "java.enjoyer@gmail.com",
                        "friends": []
                    }""")
            User user) {
        userService.saveUser(user);
        return ResponseEntity.noContent().build();
    }
}