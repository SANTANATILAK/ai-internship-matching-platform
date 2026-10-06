package com.tilak.internship_platform.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, unique = true, length = 160)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(length = 25)
    private String phone;

    @Column(length = 100)
    private String branch;

    @Column(length = 180)
    private String college;

    @Column(name = "graduation_year")
    private Integer graduationYear;

    @Column(length = 120)
    private String location;

    @Column(name = "preferred_location", length = 120)
    private String preferredLocation;

    @Column(name = "preferred_work_type", length = 50)
    private String preferredWorkType;

    @Column(name = "role_preference", length = 120)
    private String rolePreference;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private Role role = Role.STUDENT;

    @Column(nullable = false)
    @Builder.Default
    private boolean enabled = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
