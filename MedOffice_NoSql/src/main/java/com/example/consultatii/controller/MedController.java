package com.example.consultatii.controller;

import com.example.consultatii.dto.ConsultatiiDTO;
import com.example.consultatii.hateoas.ConsultatiiHateoas;
import com.example.consultatii.repository.ConsultatiiRepository;
import com.example.consultatii.services.ConsultatiiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.swing.text.html.parser.Entity;
import java.util.List;

@RestController
@RequestMapping("/api/medical_office")
public class MedController {

    @Autowired
    ConsultatiiService consultatiiService;



    @GetMapping("/consultatii")
    public ResponseEntity<?> getAllC(){

        return consultatiiService.getAllC();

    }

    @GetMapping("/consultatii/doc/{id}")
    public ResponseEntity<?> getConsultatiiByDoc(@PathVariable Integer id){

        return consultatiiService.getConsulatieByDoctorID(id);

    }

    @PostMapping("/consultatii")
    public ResponseEntity<?> createConsultatie(@RequestBody ConsultatiiDTO consultatiiDTO)
    {

        return consultatiiService.createConsultatie(consultatiiDTO);
    }





}
