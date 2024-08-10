package com.example.demo.dto;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;


@Getter
@Setter
public class AppointmentPK implements Serializable {

    @ManyToOne
    @JoinColumn(name ="id_pacient",nullable = false)
    private PacientDTO pacient;

    @ManyToOne
    @JoinColumn(name="id_doctor",nullable = false)
    private DoctorDTO doctor;

    @Column(name="data")
    private Date data;


}
