package org.userservice.domain.service;

import org.springframework.stereotype.Service;
import org.userservice.domain.model.Password;
import org.userservice.domain.model.Username;
import org.userservice.domain.repository.UserRepository;

@Service
public class UserValidationService {

    private final UserRepository userRepository;

    public UserValidationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void validateForCreation(Username username, Email email, Password password) {
        validateUsernameUniqueness(username);
        validateEmailUniqueness(email);
        validatePasswordPolicy(password);
    }
}
