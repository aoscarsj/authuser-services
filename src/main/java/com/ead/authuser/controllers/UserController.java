package com.ead.authuser.controllers;

import com.ead.authuser.dtos.UserDto;
import com.ead.authuser.models.UserModel;
import com.ead.authuser.services.UserService;
import com.fasterxml.jackson.annotation.JsonView;
import org.apache.catalina.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/users")
public class UserController {

    final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<Page<UserModel>> getAllUsers(@PageableDefault(page = 0, size = 10, sort = "userId", direction = Sort.Direction.ASC)Pageable pageable) {

        Page<UserModel> userModelPage = userService.findAll(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(userModelPage);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getOneUser(@PathVariable(value = "userId") UUID userId) {

        Optional<UserModel> userModelOptional = userService.findById(userId);
        if (userModelOptional.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        return ResponseEntity.status(HttpStatus.OK).body(userModelOptional.get());
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable(value = "userId") UUID userId) {
        Optional<UserModel> userModelOptional = userService.findById(userId);
        if (userModelOptional.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");

        userService.delete(userModelOptional.get());
        return ResponseEntity.status(HttpStatus.OK).body("User deleted successful");

    }

    @PutMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable(value = "userId") UUID userId,
                                             @RequestBody @Validated(UserDto.UserView.UserPut.class)
                                             @JsonView(UserDto.UserView.UserPut.class) UserDto userDto) {

        Optional<UserModel> userModelOptional = userService.findById(userId);
        if (userModelOptional.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");

        var userModel = userModelOptional.get();
        userModel.setFullName(userDto.getFullName());
        userModel.setCpf(userDto.getCpf());
        userModel.setPhoneNumber(userDto.getPhoneNumber());
        userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));

        try {
            userService.insert(userModel);
        }catch (RestClientException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.OK).body(userModel);
    }

    @PutMapping("/{userId}/password")
    public ResponseEntity<String> updatePassword(@PathVariable(value = "userId") UUID userId,
                                                 @RequestBody @Validated(UserDto.UserView.PasswordPut.class)
                                                 @JsonView(UserDto.UserView.PasswordPut.class) UserDto userDto) {

        Optional<UserModel> userModelOptional = userService.findById(userId);
        if (userModelOptional.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");

        var userModel = userModelOptional.get();
        if (!userModel.getPassword().equals(userDto.getOldPassword()))
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: Mismatched old password!.");

        userModel.setPassword(userDto.getPassword());
        userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));

        try {
            userService.insert(userModel);
        }catch (RestClientException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.OK).body("Password updated successfully.");
    }

    @PutMapping("/{userId}/image")
    public ResponseEntity<String> updateImage(@PathVariable(value = "userId") UUID userId,
                                              @RequestBody @Validated(UserDto.UserView.ImagePut.class)
                                              @JsonView(UserDto.UserView.ImagePut.class) UserDto userDto) {

        Optional<UserModel> userModelOptional = userService.findById(userId);
        if (userModelOptional.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");

        var userModel = userModelOptional.get();
        userModel.setImageUrl(userDto.getImageUrl());
        userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));

        try {
            userService.insert(userModel);
        }catch (RestClientException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.OK).body("Image updated successfully.");
    }
}