package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.services.AppointmentService;
import com.example.demo.services.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.example.demo.services.PacientService;

import java.util.Map;


@RestController
@RequestMapping("/api/medical_office")
@CrossOrigin(origins =  "http://localhost:3000")
public class MedController {

    @Autowired
    PacientService pacientService;

    @Autowired
    DoctorService doctorService;

    @Autowired
    AppointmentService appointmentService;


    @GetMapping("/pacients")
    public ResponseEntity<CollectionModel<EntityModel<PacientDTO>>> getAllPacients(){
        return pacientService.getAllPacients();
    }

    @GetMapping("/pacients/{id}")
    public ResponseEntity<?> getPacientByUID(@PathVariable Integer id){

        return pacientService.getPacientByUID(id);
    }

    @PostMapping("/pacients")
    public  ResponseEntity<?> createPacient(@RequestBody PacientDTO pacient, BindingResult bindingResult){

        return pacientService.createPacient(pacient,bindingResult);

    }

    @PatchMapping("/pacients/{id}")
    public ResponseEntity<?> updatePacient(@PathVariable String id, @RequestBody Map<String, Object> updates){

        return pacientService.updatePacient(id,updates);
    }

    @PutMapping("/pacients/{id}")
    public ResponseEntity<?> updatePacientOrCreate(@PathVariable String id, @RequestBody PacientDTO pacient, BindingResult bindingResult) {

        return pacientService.updatePacientOrCreate(id,pacient,bindingResult);
    }


    @GetMapping("/pacients/{id}/physicians")
    public ResponseEntity<?> findAppointmentsByPacient(@PathVariable Integer id){

        return pacientService.findAppointmentsByPacient(id);
    }



    @GetMapping("/physicians")
    public ResponseEntity<?> getAllDoctors(@RequestParam(value = "specialization",required = false) String specialization, @RequestParam(value = "page",required = false) Integer page, @RequestParam(value = "items_per_page",required = false) Integer itemsPerPage){

        if(page == null)
            return doctorService.getAllDoctors(specialization);
        return  doctorService.getDoctorsByPage(page,itemsPerPage);
    }


    @PostMapping("/physicians")
    public ResponseEntity<?> createDoctor( @RequestBody DoctorDTO doctor,BindingResult bindingResult){

        return doctorService.createDoctor(doctor,bindingResult);
    }


    @GetMapping("/physicians/{id}")
    public ResponseEntity<?> getDoctorById(@PathVariable int id){

        return doctorService.getDoctorById(id);
    }


    @GetMapping("/physicians/{id}/pacients")
    public ResponseEntity<?> findAppointmentsByDoctor(@PathVariable Integer id){

        return doctorService.findAppointmentsByDoctor(id);

    }

    @PostMapping("/appointment")
    public ResponseEntity<?> createAppointment(@RequestBody AppointmentDTO app){

        return appointmentService.createAppointment(app);

    }


    @GetMapping("/physicians/{id}/app")
    public ResponseEntity<?> getDoctorAppointmentByDate(@PathVariable Integer id,@RequestParam(value = "date",required = false) Integer date,@RequestParam(value = "type",required = false) String type)
    {

        return doctorService.getAppointmentByDate(id,date,type);
    }


}
