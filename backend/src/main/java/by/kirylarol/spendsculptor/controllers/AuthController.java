package by.kirylarol.spendsculptor.controllers;


import by.kirylarol.spendsculptor.dto.FullIdentityDTO;
import by.kirylarol.spendsculptor.service.UserService;
import by.kirylarol.spendsculptor.dto.IdentityDTO;
import by.kirylarol.spendsculptor.dto.UserDTO;
import by.kirylarol.spendsculptor.entities.Identity;
import by.kirylarol.spendsculptor.entities.User;
import by.kirylarol.spendsculptor.security.JWTComponent;
import by.kirylarol.spendsculptor.security.UserCredentials;
import by.kirylarol.spendsculptor.utils.Util;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
public class AuthController {

    private final UserService userService;
    private final JWTComponent jwtComponent;
    private final AuthenticationManager authenticationManager;

    private final Util util;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public AuthController(UserService userService, JWTComponent jwtComponent, AuthenticationManager authenticationManager, Util util, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtComponent = jwtComponent;
        this.authenticationManager = authenticationManager;
        this.util = util;
        this.passwordEncoder = passwordEncoder;

    }

    //write a bubble sort function



    @PatchMapping("/user")
    FullIdentityDTO updateIdentity(@RequestBody FullIdentityDTO fullIdentityDTO) throws Exception {
        if (fullIdentityDTO == null || fullIdentityDTO.getIdentityDTO() == null || fullIdentityDTO.getUserDTO() == null) {
            throw new Exception("Ошибка");
        }
        User user = util.getUser();
        if (user == null) {
            throw new Exception("Пользователь не найден");
        }
        Identity identity = user.getIdentity();
        identity.setSurname(fullIdentityDTO.getIdentityDTO().getSurname());
        identity.setName(fullIdentityDTO.getIdentityDTO().getName());
        if (!Objects.equals(fullIdentityDTO.getUserDTO().getPassword(), user.getPassword())) {
            user.setPassword(passwordEncoder.encode(fullIdentityDTO.getUserDTO().getPassword()));
        }
        user.setLogin(fullIdentityDTO.getUserDTO().getLogin());
        userService.addUser(user);
        return new FullIdentityDTO(user, identity);
    }


    @PostMapping("/register")
    public Map<String, String> registration(@RequestBody @Valid FullIdentityDTO registrationDTO, BindingResult bindingResult) {


        if (bindingResult.hasErrors()) {
            return Map.of("message", bindingResult.getAllErrors()
                    .stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.joining(", ")));
        }
        IdentityDTO identityDTO = registrationDTO.getIdentityDTO();
        UserDTO userDTO = registrationDTO.getUserDTO();
        Identity identity = convertToIdentity(identityDTO);
        User user = convertToUser(userDTO);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        String token = null;

        if (userService.getUser(user.getLogin()) == null) {
            user.setIdentity(identity);
            userService.addUser(user);
            UserCredentials userCredentials = new UserCredentials(user);
            token = jwtComponent.generateToken(userCredentials);
            return Map.of("jwt-token", token, "username", identity.getName());
        }
        return Map.of("message", "Ошибка");
    }

    @GetMapping("/login")
    public void login() {

    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody UserDTO userDTO, BindingResult bindingResult) {
        UsernamePasswordAuthenticationToken authInputToken =
                new UsernamePasswordAuthenticationToken(userDTO.login(),
                        userDTO.getPassword()
                );

        try {
            authenticationManager.authenticate(authInputToken);
        } catch (Exception e) {
            return Map.of("message", "Incorrect credentials!");
        }
        String name = userService.getUser(userDTO.login()).getIdentity().getName();
        UserCredentials userCredentials = new UserCredentials(userService.getUser(userDTO.login()));
        String token = jwtComponent.generateToken(userCredentials);
        return Map.of("jwt-token", token, "username", name);
    }

    public User convertToUser(UserDTO userDTO) {
        return new User(userDTO.login(), userDTO.getPassword());

    }

    public Identity convertToIdentity(IdentityDTO identityDTO) {
        return new Identity(identityDTO.getName(), identityDTO.getSurname());
    }

    @ExceptionHandler
    public void handleException(Exception e) { // idk why firefox login produce exception))
        return;
    }
}
