package com.example.consultatii.services;

import com.example.consultatii.controller.MedController;
import com.example.consultatii.dto.ConsultatiiDTO;
import com.example.consultatii.hateoas.ConsultatiiHateoas;
import com.example.consultatii.repository.ConsultatiiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.client.Traverson;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@Service
public class ConsultatiiService {

    @Autowired
    ConsultatiiRepository consultatiiRepository;

    @Autowired
    ConsultatiiHateoas consultatiiHateoas;

    @Autowired
    private final UserService userService;

    @Autowired
    public ConsultatiiService(UserService userService){
        this.userService = userService;
    }


    private RestTemplate restTemplate = new RestTemplate();


    //////////////////////////////////////////////          GET (ALL)               ////////////////////////////////////////////
    public ResponseEntity<?> getAllC(){

        List<ConsultatiiDTO> consulatii;

        try {
            consulatii = consultatiiRepository.findAll();
            //return consultatiiHateoas.toCollectionModel(consulatii);
            if(!consulatii.isEmpty())
            {
                return ResponseEntity.status(HttpStatus.OK).body(consultatiiHateoas.toCollectionModel(consulatii));
            }
            else
            {
                return ResponseEntity.status(HttpStatus.OK).body(CollectionModel.of(CollectionModel.empty()));
            }
        }catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(CollectionModel.of(CollectionModel.empty()));


    }

    //////////////////////////////////////////////         GET BY DOCTOR ID              ////////////////////////////////////////////
    public ResponseEntity<?> getConsulatieByDoctorID(Integer idDoctor)
    {
        List<ConsultatiiDTO> consulatii;

        String currentUserId = this.userService.getCurrentUserId();

        // trb verificat ca userul logat sa fie acelasi pt care se vrea aflarea consultatiilor



        try{
            consulatii = consultatiiRepository.findByIdDoctor(idDoctor);
            return ResponseEntity.status(HttpStatus.OK).body(consultatiiHateoas.toCollectionModel(consulatii));


        }catch (Exception e)
        {
            System.out.println(e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    //////////////////////////////////////////////         POST            ////////////////////////////////////////////

    public ResponseEntity<?> createConsultatie(ConsultatiiDTO consultatieDTO)
    {

        try{

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxIiwidXNlcm5hbWUiOiJ1c2VybmFtZSIsInJvbGUiOiJhZG1pbiIsImV4cCI6MTcyMzIxMzA0MSwianRpIjoiZGQ1MmNjMDYtOWM1NC00YTE4LWE1YzQtYTIxMmMxN2E3NjIzIn0.ezyiZS6LcKI7Yo7Mkk5R1hoEsCkolo0Szxvi8ZrEiJw");
            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<?> appResponse = restTemplate.exchange(
                    "http://localhost:8080/api/medical_office/physicians/{id}/pacients",
                    HttpMethod.GET,
                    entity,
                    Object.class,
                    consultatieDTO.getIdDoctor()
            );

            Object responseBody = appResponse.getBody();
            //System.out.println(responseBody);

            // Transformam intr un map
            if(responseBody != null && responseBody instanceof Map)
            {
                Map<String, Object> responseMap = (Map<String, Object>) responseBody;

                System.out.println(responseMap.keySet());

                if(responseMap.containsKey("_embedded"))
                {
                    Map<String, Object> embeddedMap = (Map<String, Object>) responseMap.get("_embedded");
                    List<Map<String, Object>> appointmentResponseList = (List<Map<String, Object>>) embeddedMap.get("appointmentResponseList");

                    for (Map<String, Object> appointment : appointmentResponseList) {
                        String statusAp = (String) appointment.get("statusAp");
                        String numePacient = (String) appointment.get("numePacient");
                        String prenumePacient = (String) appointment.get("prenumePacient");
                        String numeDoctor = (String) appointment.get("numeDoctor");
                        String prenumeDoctor = (String) appointment.get("prenumeDoctor");
                        String data = (String) appointment.get("data");

                        Map<String, Object> links = (Map<String, Object>) appointment.get("_links");
                        Map<String, Object> selfLink = (Map<String, Object>) links.get("self");
                        String href = (String) selfLink.get("href");

                        System.out.println("Status: " + statusAp);
                        System.out.println("Patient: " + numePacient + " " + prenumePacient);
                        System.out.println("Doctor: " + numeDoctor + " " + prenumeDoctor);
                        System.out.println("Date: " + data);
                        System.out.println("Link: " + href);
                        System.out.println("---");
                    }

                    System.out.println("exista programare");
                    consultatiiRepository.save(consultatieDTO);
                    return ResponseEntity.status(HttpStatus.CREATED).body(null);

                    //ResponseEntity<?> appointmentResponse = restTemplate.getForEntity();
                }
                else
                {
                    System.out.println("nu exista programare");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);

                }
            }




        }catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);



    }






}
