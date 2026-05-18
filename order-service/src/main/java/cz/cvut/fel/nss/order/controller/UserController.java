package cz.cvut.fel.nss.order.controller;

import cz.cvut.fel.nss.order.dto.UserDto;
import cz.cvut.fel.nss.order.model.Role;
import cz.cvut.fel.nss.order.model.User;
import cz.cvut.fel.nss.order.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/me")
    public UserDto getCurrentUser(@AuthenticationPrincipal User user) {
        return mapToDto(user);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public UserDto createUser(@RequestBody User user) {
        if (user.getRole() == null) {
            user.setRole(Role.ROLE_CUSTOMER);
        }
        return mapToDto(userRepository.save(user));
    }

    private UserDto mapToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
