package com.dataanalyst.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Dataset> datasets;

    public User() {}

    public User(Long id, String name, String email, String passwordHash,
                LocalDateTime createdAt, Role role, List<Dataset> datasets) {
        this.id = id; this.name = name; this.email = email;
        this.passwordHash = passwordHash; this.createdAt = createdAt;
        this.role = role; this.datasets = datasets;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (role == null) role = Role.USER;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id; private String name; private String email;
        private String passwordHash; private LocalDateTime createdAt;
        private Role role = Role.USER; private List<Dataset> datasets;

        public Builder id(Long id)                 { this.id = id; return this; }
        public Builder name(String name)           { this.name = name; return this; }
        public Builder email(String email)         { this.email = email; return this; }
        public Builder passwordHash(String ph)     { this.passwordHash = ph; return this; }
        public Builder createdAt(LocalDateTime ca) { this.createdAt = ca; return this; }
        public Builder role(Role role)             { this.role = role; return this; }
        public Builder datasets(List<Dataset> ds)  { this.datasets = ds; return this; }
        public User build() {
            return new User(id, name, email, passwordHash, createdAt, role, datasets);
        }
    }

    public Long getId()                        { return id; }
    public void setId(Long id)                 { this.id = id; }
    public String getName()                    { return name; }
    public void setName(String name)           { this.name = name; }
    public String getEmail()                   { return email; }
    public void setEmail(String email)         { this.email = email; }
    public String getPasswordHash()            { return passwordHash; }
    public void setPasswordHash(String ph)     { this.passwordHash = ph; }
    public LocalDateTime getCreatedAt()        { return createdAt; }
    public void setCreatedAt(LocalDateTime ca) { this.createdAt = ca; }
    public Role getRole()                      { return role; }
    public void setRole(Role role)             { this.role = role; }
    public List<Dataset> getDatasets()         { return datasets; }
    public void setDatasets(List<Dataset> ds)  { this.datasets = ds; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User u = (User) o;
        return Objects.equals(id, u.id) && Objects.equals(email, u.email);
    }
    @Override public int hashCode() { return Objects.hash(id, email); }
    @Override public String toString() {
        return "User{id=" + id + ", name='" + name + "', email='" + email + "', role=" + role + '}';
    }

    public enum Role { USER, ADMIN }
}
