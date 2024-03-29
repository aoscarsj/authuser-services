package com.ead.authuser.services.Impl;

import com.ead.authuser.models.UserModel;
import com.ead.authuser.repositories.UserRepository;
import com.ead.authuser.services.UserService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserModel> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<UserModel> findById(UUID userId) {
        return userRepository.findById(userId);
    }

    @Override
    public void delete(UserModel userModel) {
        userRepository.delete(userModel);
    }

    @Override
    public void insert(UserModel userModel) {

        validateUser(userModel);
        userRepository.save(userModel);
    }

    private void validateUser(UserModel userModel) {

        if(userModel.getUsername() == null || userModel.getUsername().trim().isEmpty() || userModel.getUsername().contains(" "))
            throw new RestClientException("Error: invalid username");
        if(userModel.getUsername().length() < 4 || userModel.getUsername().length() > 50)
            throw new RestClientException("Error: username cannot be less than 4 characters");
        if(!userModel.getEmail().matches("[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")) //RFC 5322
            throw new RestClientException("Error: invalid email");
        if(userModel.getPassword().length() < 8 || userModel.getPassword().length() > 20)
            throw new RestClientException("Error: password cannot be less than 8 characters");
        if(userModel.getImageUrl().length() == 0)
            throw new RestClientException("Error: image url cannot be empty" );
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }


}
