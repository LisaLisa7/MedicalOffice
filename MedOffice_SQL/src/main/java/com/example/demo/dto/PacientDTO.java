package com.example.demo.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="pacients")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PacientDTO {


    @Column(name="cnp",nullable = false)
    @NotBlank(message = "Camp obligatoriu")
    String CNP;

    @Id
    @Column(name = "id_user", nullable = false)
    @NotBlank(message = "Camp obligatoriu")
    int idUser;

    @Column(name = "nume", nullable = false)
    @NotBlank(message = "Camp obligatoriu")
    String nume;

    @Column(name = "prenume", nullable = false)
    @NotBlank(message = "Camp obligatoriu")
    String prenume;

    @Column(name = "email", nullable = false, unique = true)
    @NotBlank(message = "Camp obligatoriu")
    String email;

    @Column(name = "telefon", nullable = false)
    @NotBlank(message = "Camp obligatoriu")
    String phoneNumber;

    @Column(name = "data_nasterii", nullable = false)

    @Temporal(TemporalType.DATE)
    Date birthday;

    @Column(name = "is_active", nullable = false)
    @NotBlank(message = "Camp obligatoriu")
    boolean is_active;




}
