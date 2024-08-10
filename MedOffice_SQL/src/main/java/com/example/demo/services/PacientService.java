package com.example.demo.services;

import com.example.demo.config.JwtUtil;
import com.example.demo.controller.MedController;
import com.example.demo.dto.AppointmentDTO;
import com.example.demo.dto.AppointmentResponse;
import com.example.demo.dto.PacientDTO;
import com.example.demo.hateoas.PacientHateoas;
import com.example.demo.repository.AppointmentRepository;
import com.example.demo.repository.PacientRepository;
import com.example.demo.validators.PacientValidator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class PacientService {

    @Autowired
    PacientRepository pacientRepository;

    @Autowired
    PacientHateoas pacientHateoas;

    @Autowired
    private final UserService userService;


    ModelMapper modelMapper = new ModelMapper();


    AppointmentRepository appointmentRepository;

    @Autowired
    public PacientService(AppointmentRepository appointmentRepository,UserService userService){
        this.appointmentRepository = appointmentRepository;
        this.userService = userService;
    }

    //////////////////////////////////////////////          GET (ALL)               ////////////////////////////////////////////
    public ResponseEntity<CollectionModel<EntityModel<PacientDTO>>> getAllPacients(){

        List<PacientDTO> pacients;
        try {
            pacients = pacientRepository.findAll();
            CollectionModel<EntityModel<PacientDTO>> pacientCollectionModel = pacientHateoas.toCollectionModel(pacients);

            return ResponseEntity.ok(pacientCollectionModel);
        } catch (Exception e) {
            System.out.println("Tabela nu exista!");
            System.out.println(e.getMessage());
            //CollectionModel<EntityModel<PacientDTO>> emptyCollectionModel = CollectionModel.of(CollectionModel.empty());
        }
        CollectionModel<EntityModel<PacientDTO>> emptyCollectionModel = CollectionModel.of(CollectionModel.empty());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(emptyCollectionModel);

    }

    //////////////////////////////////////////////          PATCH                ////////////////////////////////////////////

    public ResponseEntity<?> updatePacient(String id,Map<String, Object> updates){

        String currentUID = this.userService.getCurrentUserRole();
        Integer uidFromBd = this.getUidByCnp(id);



        try{
            PacientDTO pacient = pacientRepository.findByCNP(id);

            if(pacient != null) {
                updates.forEach((key, value) -> {
                    switch (key) {
                        case "nume":
                            pacient.setNume((String) value);
                            break;
                        case "prenume":
                            pacient.setPrenume((String) value);
                            break;
                        case "phoneNumber":
                            pacient.setPhoneNumber((String) value);
                            break;
                        case "email":
                            pacient.setEmail((String) value);
                            break;

                    }
                });

                //System.out.println("be3fore validator");
                //System.out.println(pacient.getCNP());


                DataBinder dataBinder = new DataBinder(pacient);
                PacientValidator pacientValidator = new PacientValidator();
                dataBinder.addValidators(pacientValidator);
                dataBinder.validate();

                //  BindingResult asociat cu PacientDTO si nu cu Map-ul :D ( took me a while )
                BindingResult result = dataBinder.getBindingResult();

                //System.out.println("after validator");

                if (result.hasErrors()) {

                    List<String> errors = new ArrayList<>();
                    for (FieldError fieldError : result.getFieldErrors()) {
                        errors.add(fieldError.getField() + ": " + fieldError.getDefaultMessage());
                    }
                    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(errors);
                    //System.out.println(bindingResult.getAllErrors());

                } else {

                    pacientRepository.save(pacient);
                    return ResponseEntity.ok(pacientHateoas.toModel(pacient));
                }

                 /*
                pacientRepository.save(pacient);
                return ResponseEntity.ok(pacientHateoas.toModel(pacient));

                  */

            }

        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();

        }
        return ResponseEntity.status(HttpStatus.OK).body(CollectionModel.of(CollectionModel.empty()));
    }



    //////////////////////////////////////////////          PUT              ////////////////////////////////////////////

    public ResponseEntity<?> updatePacientOrCreate(String id,PacientDTO pacientUpdated, BindingResult bindingResult) {

        try{
            PacientDTO p = pacientRepository.findByCNP(id);

            PacientValidator pacientValidator = new PacientValidator();
            if(pacientUpdated!=null) {
                pacientValidator.validate(pacientUpdated, bindingResult);

                if (pacientRepository.findByCNP(pacientUpdated.getCNP()) != null && pacientRepository.findByEmail(pacientUpdated.getEmail()) != null)
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(null);


            }
            if (bindingResult.hasErrors() || pacientUpdated==null)
            {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);

            }

            if(p != null )
            {
                System.out.println("inlocuire_in");
                pacientRepository.delete(p);

                modelMapper.map(pacientUpdated,p);
                System.out.println("inlocuire_in2");
                System.out.println(p.getCNP());

                pacientRepository.save(p);
                System.out.println("inlocuire");
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);


            }
            else
            {
                pacientRepository.save(pacientUpdated);
                return ResponseEntity.status(HttpStatus.CREATED).body(pacientHateoas.toModel(pacientUpdated));
            }

        }catch (Exception e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }


    //////////////////////////////////////////////          POST               ////////////////////////////////////////////

    public  ResponseEntity<?> createPacient( PacientDTO pacient, BindingResult bindingResult){
        System.out.println("in!");
        Link l = linkTo(methodOn(MedController.class).getAllPacients()).withRel("parent");

        PacientValidator pacientValidator = new PacientValidator();
        pacientValidator.validate(pacient,bindingResult);

        //return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).body(l);

        if (bindingResult.hasErrors()) {

            List<String> errors = new ArrayList<>();
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                errors.add(fieldError.getField() + ": " + fieldError.getDefaultMessage());
            }
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(errors);
            //System.out.println(bindingResult.getAllErrors());
            //return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(l);

        } else {
            try {
                if (pacientRepository.findByCNP(pacient.getCNP()) == null && pacientRepository.findByEmail(pacient.getEmail()) == null) {
                    pacientRepository.save(pacient);
                    //return ResponseEntity.status(HttpStatus.NOT_FOUND).body(l);
                    Link l2 = linkTo(methodOn(MedController.class).getPacientByUID(pacient.getIdUser())).withRel("pacientDetails");
                    return ResponseEntity.status(HttpStatus.CREATED).body(CollectionModel.of(CollectionModel.empty(),l,l2));
                }
                else{
                    // deja exista
                    System.out.println("Pacientul deja exista");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(l);
                }
            }catch (Exception e)
            {
                System.out.println("No table");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(l);
            }
        }

    }

    //////////////////////////////////////////////          GET BY ID               ////////////////////////////////////////////

    public ResponseEntity<?> getPacientByUID( Integer id){
        Link l = linkTo(methodOn(MedController.class).getPacientByUID(id)).withSelfRel();
        Link l2 = linkTo(methodOn(MedController.class).getAllPacients()).withRel("parent");
        CollectionModel<EntityModel<PacientDTO>> emptyCollectionModel = CollectionModel.of(CollectionModel.empty(), l, l2);



        PacientDTO targetPacient = pacientRepository.findByIdUser(id);
        Integer uidFromBD = -1;
        if(targetPacient != null ) {
            uidFromBD = targetPacient.getIdUser();
        }

        String currentUserId = this.userService.getCurrentUserId();
        String role = this.userService.getCurrentUserRole();
        System.out.println(currentUserId + " " + role + " in bd: " + uidFromBD);

        if(currentUserId.equals(uidFromBD.toString()) || role.equals("ROLE_ADMIN")) {
            try {
                if (targetPacient == null) {

                    return ResponseEntity.status(HttpStatus.OK).body(emptyCollectionModel);

                    //return ResponseEntity.status(HttpStatus.OK).body(EntityModel.of(new PacientDTO(), l, l2));
                }

                EntityModel<PacientDTO> rez = pacientHateoas.toModel(targetPacient);

                return ResponseEntity.ok(rez);
            } catch (Exception e) {
                System.out.println("Tabela nu exista!");
            }

            return ResponseEntity.status(HttpStatus.OK).body(emptyCollectionModel);
        }
        else
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(emptyCollectionModel);


    }

    //////////////////////////////////////////////         getting the id using the cnp             ////////////////////////////////////////////

    public Integer getUidByCnp( String cnp){

        try {
            PacientDTO pacient = pacientRepository.findByCNP(cnp);
            if (pacient == null) {

                return -1;
            }
            else
                return pacient.getIdUser();

        }catch (Exception e)
        {
            System.out.println("Tabela nu exista!");
            return -1;
        }



    }



    //////////////////////////////////////////////          GET - PROGRAMARI               ////////////////////////////////////////////

    public ResponseEntity<?> findAppointmentsByPacient(Integer id){

        Link l = linkTo(methodOn(MedController.class).findAppointmentsByPacient(id)).withSelfRel();
        Link l2 = linkTo(methodOn(MedController.class).getAllPacients()).withRel("parent");

        PacientDTO targetPacient = pacientRepository.findByIdUser(id);
        Integer uidFromBD = -1;
        if(targetPacient != null ) {
            uidFromBD = targetPacient.getIdUser();
        }

        String currentUserId = this.userService.getCurrentUserId();
        String role = this.userService.getCurrentUserRole();
        //currentUserId = authentication.getName();

        System.out.println(currentUserId + " " + role + " in bd: " + uidFromBD);

        if(currentUserId.equals(uidFromBD.toString()))
        {
            System.out.println("pacientul bun!");

            try {
                if (targetPacient != null) {

                    List<AppointmentDTO> listAp = appointmentRepository.findByAppointmentPkPacient(targetPacient);
                    if (!listAp.isEmpty()) {

                        List<AppointmentResponse> brief = new ArrayList<>();

                        for (AppointmentDTO appointmentDTO : listAp) {
                            AppointmentResponse response = new AppointmentResponse();

                            response.setStatusAp(appointmentDTO.getStatus());
                            response.setData(appointmentDTO.getAppointmentPk().getData());
                            response.setNumePacient(appointmentDTO.getAppointmentPk().getPacient().getNume());
                            response.setPrenumePacient(appointmentDTO.getAppointmentPk().getPacient().getPrenume());
                            response.setNumeDoctor(appointmentDTO.getAppointmentPk().getDoctor().getNume());
                            response.setPrenumeDoctor(appointmentDTO.getAppointmentPk().getDoctor().getPrenume());

                            brief.add(response);
                        }

                        List<EntityModel<AppointmentResponse>> app = brief.stream()
                                .map(response -> EntityModel.of(response,
                                        linkTo(methodOn(MedController.class).findAppointmentsByPacient(id)).withSelfRel()
                                )).collect(Collectors.toList());

                        System.out.println(brief.get(0).getStatusAp());

                        return ResponseEntity.ok(CollectionModel.of(app, l, l2));

                    } else {
                        return ResponseEntity.status(HttpStatus.OK).body(CollectionModel.of(CollectionModel.empty(), l, l2));
                    }

                }
            }catch (Exception e){
                System.out.println("tabela nu exista!");
                e.printStackTrace();
            }
            return ResponseEntity.status(HttpStatus.OK).body(CollectionModel.of(CollectionModel.empty(), l, l2));

        }
        else{
            System.out.println("nope nope");

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(CollectionModel.of(CollectionModel.empty(), l, l2));


        }


    }


}
