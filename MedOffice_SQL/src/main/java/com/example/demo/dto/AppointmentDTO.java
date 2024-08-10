package com.example.demo.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "programari")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class AppointmentDTO {

    @EmbeddedId
    AppointmentPK appointmentPk;

    @Column(name="status",nullable = false)
    @Enumerated(EnumType.STRING)
    StatusAp status;


}