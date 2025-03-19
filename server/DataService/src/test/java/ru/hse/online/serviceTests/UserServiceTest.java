package ru.hse.online.serviceTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.hse.online.exceptions.EntityNotFoundException;
import ru.hse.online.model.EmailToUserId;
import ru.hse.online.model.User;
import ru.hse.online.repository.EmailToUserIdRepository;
import ru.hse.online.repository.UserRepository;
import ru.hse.online.service.core.UserCoreService;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailToUserIdRepository usernameToUserIdRepository;

    @InjectMocks
    private UserCoreService userCoreService;

    private User testUser;
    private UUID testUserId;
    private String testUsername;
    private String testEmail;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testUsername = "andrey";
        testEmail = "java.enjoyer@gmail.com";
        testUser = User.builder()
                .userId(testUserId)
                .username(testUsername)
                .email(testEmail)
                .build();
    }

    @Test
    void getExistingUserByNameReturnsUser() {
        EmailToUserId usernameToUserId = EmailToUserId.builder()
                .email(testEmail)
                .userId(testUserId)
                .build();

        when(usernameToUserIdRepository.findById(testUsername)).thenReturn(Optional.of(usernameToUserId));
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

        User result = userCoreService.getUserByEmail(testUsername);

        assertEquals(testUser, result);
        verify(usernameToUserIdRepository).findById(testUsername);
        verify(userRepository).findById(testUserId);
    }

    @Test
    void getNonExistingUserByEmailThrowsEntityNotFoundException() {
        when(usernameToUserIdRepository.findById(testUsername)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userCoreService.getUserByEmail(testUsername));
        verify(usernameToUserIdRepository).findById(testUsername);
        verify(userRepository, never()).findById(any());
    }

    @Test
    void getExistingUserByIdReturnsUser() {
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

        User result = userCoreService.getUserById(testUserId);

        assertEquals(testUser, result);
        verify(userRepository).findById(testUserId);
    }

    @Test
    void getNonExistingUserByIdThrowsEntityNotFoundException() {
        when(userRepository.findById(testUserId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userCoreService.getUserById(testUserId));
        verify(userRepository).findById(testUserId);
    }

    @Test
    void saveValidUserSavesUser() {
        userCoreService.saveUser(testUser);

        verify(userRepository).save(testUser);
        verify(usernameToUserIdRepository).save(any(EmailToUserId.class));
    }
}
