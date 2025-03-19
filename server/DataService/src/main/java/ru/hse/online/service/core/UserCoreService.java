package ru.hse.online.service.core;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hse.online.exceptions.EntityNotFoundException;
import ru.hse.online.model.EmailToUserId;
import ru.hse.online.model.User;
import ru.hse.online.repository.EmailToUserIdRepository;
import ru.hse.online.repository.UserRepository;
import ru.hse.online.service.UserService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserCoreService implements UserService {
    private final UserRepository userRepository;
    private final EmailToUserIdRepository emailToUserIdRepository;

    public User getUserByEmail(String email) {
        return emailToUserIdRepository.findById(email)
                .map(EmailToUserId::getUserId)
                .flatMap(userRepository::findById)
                .orElseThrow(() -> new EntityNotFoundException("User with email \"" + email + "\" - not found"));
    }

    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User", id));
    }

    @Transactional
    public void saveUser(User user) {
        userRepository.save(user);
        emailToUserIdRepository.save(
                EmailToUserId.builder()
                        .email(user.getEmail())
                        .userId(user.getUserId())
                        .build()
        );
    }
}
