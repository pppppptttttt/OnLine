package ru.hse.online.service.core;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hse.online.exceptions.EntityNotFoundException;
import ru.hse.online.mapper.UserMapper;
import ru.hse.online.model.User;
import ru.hse.online.repository.EmailToUserIdRepository;
import ru.hse.online.repository.UserRepository;
import ru.hse.online.service.UserService;
import ru.hse.online.storage.EmailToUserIdData;
import ru.hse.online.storage.UserData;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class UserCoreService implements UserService {
    private final UserRepository userRepository;
    private final EmailToUserIdRepository emailToUserIdRepository;

    public User getUserByEmail(String email) {
        return emailToUserIdRepository.findById(email)
                .map(EmailToUserIdData::getUserId)
                .flatMap(userRepository::findById)
                .map(UserMapper::toModel)
                .orElseThrow(() -> new EntityNotFoundException("User with email \"" + email + "\" - not found"));
    }

    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .map(UserMapper::toModel)
                .orElseThrow(() -> new EntityNotFoundException("User", id));
    }

    @Transactional
    public void saveUser(User user) {
        UserData userData = UserMapper.toData(user);
        userRepository.save(userData);

        EmailToUserIdData emailToUserId = EmailToUserIdData.builder()
                .email(user.getEmail())
                .userId(user.getUserId())
                .build();
        emailToUserIdRepository.save(emailToUserId);
    }
}