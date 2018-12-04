package com.mitrais.khotim.rmsspring.server.apis;

import com.mitrais.khotim.rmsspring.server.domains.Book;
import com.mitrais.khotim.rmsspring.server.domains.BookResource;
import com.mitrais.khotim.rmsspring.server.domains.User;
import com.mitrais.khotim.rmsspring.server.domains.UserResource;
import com.mitrais.khotim.rmsspring.server.exceptions.ErrorDetails;
import com.mitrais.khotim.rmsspring.server.securities.CurrentUser;
import com.mitrais.khotim.rmsspring.server.securities.UserPrincipal;
import com.mitrais.khotim.rmsspring.server.securities.UserSummary;
import com.mitrais.khotim.rmsspring.server.services.UserService;
import com.mitrais.khotim.rmsspring.server.validations.ValidationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(new Resources<>(
                userService.findAll(),
                linkTo(methodOn(UserController.class).getAll()).withSelfRel()));
    }

    @GetMapping("/current")
    @PreAuthorize("hasRole('USER')")
    public UserSummary getCurrentUser(@CurrentUser UserPrincipal currentUser) {
        return new UserSummary(currentUser.getId(), currentUser.getName(), currentUser.getEmail());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> create(@RequestBody @Valid User newUser, BindingResult errors, WebRequest request) throws URISyntaxException {
        if (errors.hasErrors()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorDetails(ValidationMessage.getMessages(errors), request.getDescription(false)));
        }

        UserResource resource = userService.save(newUser);

        return ResponseEntity.created(new URI(resource.getId().expand().getHref())).body(resource);
    }
}
