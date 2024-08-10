package com.example.demo.services;

import com.example.demo.dto.*;
import com.example.demo.repository.AppointmentRepository;
import com.example.demo.repository.DoctorRepository;
import com.example.demo.repository.PacientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AppointmentService {

    AppointmentRepository appointmentRepository;

    PacientRepository pacientRepository;
    DoctorRepository doctorRepository;


    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository,PacientRepository pacientRepository,DoctorRepository doctorRepository)
    {
        this.appointmentRepository = appointmentRepository;
        this.pacientRepository = pacientRepository;
        this.doctorRepository = doctorRepository;
    }

    public ResponseEntity<?> createAppointment(AppointmentDTO appointmentDTO) {
        try {
            System.out.println("in");
            appointmentDTO.setStatus(StatusAp.inAsteptare);
            AppointmentPK appointmentPK = appointmentDTO.getAppointmentPk();
            Integer idDoctor = appointmentPK.getDoctor().getIdDoctor();
            String cnpPacient = appointmentPK.getPacient().getCNP();
            System.out.println(idDoctor);
            System.out.println(cnpPacient);
            System.out.println(appointmentPK.getData());
            if(pacientRepository.findByCNP(cnpPacient)!= null && doctorRepository.findByIdDoctor(idDoctor)!=null) {
                appointmentRepository.save(appointmentDTO);
                return ResponseEntity.status(HttpStatus.OK).body(null);
            }
            else
            {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);
            }
        }catch (Exception e)
        {
            System.out.println(e.getMessage());

        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
    }

}
