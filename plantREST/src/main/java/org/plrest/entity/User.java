package org.plrest.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "mail", nullable = false, length = 100)
    private String mail;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "phone", nullable = false, length = 100)
    private String phone;

    @OneToOne
    @JoinColumn(name = "home_plants_id")
    private HomePlant department;
}
