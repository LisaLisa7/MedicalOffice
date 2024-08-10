package com.example.demo.dto;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name="doctors")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DoctorDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_doctor")
    int idDoctor;

    @Column(name = "id_user", nullable = false)
    int idUser;

    @Column(name = "nume", nullable = false)
    String nume;

    @Column(name = "prenume", nullable = false)
    String prenume;

    @Column(name = "email", nullable = false, unique = true)
    String email;

    @Column(name = "telefon", nullable = false)
    String phoneNumber;

    @Column(name="specializare",nullable = false)
    @Enumerated(EnumType.STRING)
    Specializare specializare;



}
