package com.example.demo.repository;

import com.example.demo.dto.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;

public interface AppointmentRepository extends JpaRepository<AppointmentDTO, AppointmentPK> {
    ArrayList<AppointmentDTO> findAll();
    ArrayList<AppointmentDTO> findByAppointmentPkPacient(PacientDTO pacient);
    ArrayList<AppointmentDTO> findByAppointmentPkDoctor(DoctorDTO pacient);

}
