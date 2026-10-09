package com.tilak.internship_platform.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.tilak.internship_platform.entity.User;
import com.tilak.internship_platform.repository.UserRepository;
import com.tilak.internship_platform.security.JwtService;

class UserControllerTest {

    @Test
    void savesMissingProfileFieldsForTheAuthenticatedStudent() {
        UserRepository repository = mock(UserRepository.class);
        User user = new User();
        user.setId(14L);
        when(repository.findById(14L)).thenReturn(Optional.of(user));
        UserController controller = new UserController(
                repository, mock(PasswordEncoder.class), mock(JwtService.class));

        Map<String, Object> result = controller.updateProfile(
                14L,
                Map.of("branch", "Robotics Engineering", "graduationYear", 2034),
                () -> "14");

        assertEquals("Robotics Engineering", user.getBranch());
        assertEquals(2034, user.getGraduationYear());
        assertEquals("Robotics Engineering", result.get("branch"));
        verify(repository).save(user);
    }

    @Test
    void preventsUpdatingAnotherStudentsProfile() {
        UserController controller = new UserController(
                mock(UserRepository.class), mock(PasswordEncoder.class), mock(JwtService.class));

        assertThrows(AccessDeniedException.class, () -> controller.updateProfile(
                14L,
                Map.of("branch", "Computer Science", "graduationYear", 2029),
                () -> "15"));
    }
}