package com.mitrais.khotim.rmsspring.server.apis;

import com.mitrais.khotim.rmsspring.server.domains.*;
import com.mitrais.khotim.rmsspring.server.exceptions.AppException;
import com.mitrais.khotim.rmsspring.server.securities.JwtTokenProvider;
import com.mitrais.khotim.rmsspring.server.services.RoleService;
import com.mitrais.khotim.rmsspring.server.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.Collections;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    @Autowired
    public AuthController(
            AuthenticationManager authenticationManager,
            UserService userService,
            RoleService roleService,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider tokenProvider) {

        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody SignIn signIn) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signIn.getEmail(), signIn.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }

    /**
     * Registers a new user and set its role as super administrator.
     * This function should only be called once when no single user found in database.
     *
     * @param signUp The signup data.
     * @return Successful registration message or
     */
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUp signUp) {
        if (!userService.findAll().isEmpty()) {
            return new ResponseEntity<>(new ApiResponse(false, "The requested page is current not available."),
                    HttpStatus.NOT_FOUND);
        }

        // Creating user's account
        User user = new User(signUp.getName(), signUp.getEmail(), signUp.getPassword());

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Role userRole = roleService.findByName(RoleName.ROLE_ADMIN)
                .orElseThrow(() -> new AppException("User role not set."));

        user.setRoles(Collections.singleton(userRole));

        UserResource result = userService.save(user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/users/{username}")
                .buildAndExpand(result.getEmail()).toUri();

        return ResponseEntity.created(location).body(new ApiResponse(true, "User registered successfully."));
    }
}