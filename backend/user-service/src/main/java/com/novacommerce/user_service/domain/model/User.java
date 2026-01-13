package com.novacommerce.user_service.domain.model;


import com.novacommerce.user_service.domain.enums.UserStatusEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Entidad que representa un usuario del sistema.
 * Los usuarios son cuentas de acceso para administrar la plataforma.
 * NO representa empleados, clientes u otras entidades de negocio.
 * Utiliza relación MANY-TO-MANY con Role.
 */
@Entity
@Table(
    name = "users",
    indexes = {
        @Index(name = "idx_username", columnList = "username", unique = true),
        @Index(name = "idx_email", columnList = "email", unique = true)
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "username", nullable = false, unique = true, length = 100)
    @NotBlank(message = "El nombre de usuario es obligatorio")
    private String username;

    @Column(name = "email", nullable = false, unique = true, length = 150)
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    private String email;

    @Column(name = "password", nullable = false)
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private UserStatusEnum status = UserStatusEnum.ACTIVE;

    @Column(name = "enabled", nullable = false)
    @Builder.Default
    private Boolean enabled = true;

    @Column(name = "locked", nullable = false)
    @Builder.Default
    private Boolean locked = false;

    @Column(name = "customer_id")
    private Long customerId;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "user_role",
        joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
    )
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    /**
     * Agrega un rol al usuario.
     *
     * @param role el rol a agregar
     */
    public void addRole(Role role) {
        if (role != null) {
            this.roles.add(role);
        }
    }

    /**
     * Remueve un rol del usuario.
     *
     * @param role el rol a remover
     */
    public void removeRole(Role role) {
        if (role != null) {
            this.roles.remove(role);
        }
    }

    /**
     * Verifica si el usuario está activo y disponible para autenticación.
     *
     * @return true si el usuario está activo, no bloqueado y habilitado
     */
    public Boolean isAccountActive() {
        return UserStatusEnum.ACTIVE.equals(this.status) 
            && this.enabled 
            && !this.locked;
    }

    /**
     * Bloquea la cuenta del usuario.
     */
    public void lockAccount() {
        this.locked = true;
    }

    /**
     * Desbloquea la cuenta del usuario.
     */
    public void unlockAccount() {
        this.locked = false;
    }
}
