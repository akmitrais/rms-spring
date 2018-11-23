package com.mitrais.khotim.rmsspring.controllers;

import com.mitrais.khotim.rmsspring.exceptions.ResourceNotFoundException;
import com.mitrais.khotim.rmsspring.models.User;
import com.mitrais.khotim.rmsspring.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/users")
public class UserController {
    @Autowired
    private UserRepository repository;

    @GetMapping
    public List<User> index() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable(value = "id") Long userId) {
        User user = getUser(userId);

        return ResponseEntity.ok().body(user);
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        return repository.save(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updatePut(@PathVariable(value = "id") Long userId, @Valid @RequestBody User userDetails) {
        User user = getUser(userId);

        user.setEmail(userDetails.getEmail());
        user.setPassword(userDetails.getPassword());
        user.setName(userDetails.getName());

        final User updateUser = repository.save(user);
        return ResponseEntity.ok(updateUser);
    }

    @DeleteMapping("/{id}")
    public Map<String, Boolean> delete(@PathVariable(value = "id") Long userId) {
        User user = getUser(userId);

        repository.delete(user);
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);

        return response;
    }

    private User getUser(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found for this id " + id));
    }
}
