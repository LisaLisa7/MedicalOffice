package com.example.demo.services;

import com.example.demo.controller.MedController;
import com.example.demo.dto.AppointmentDTO;
import com.example.demo.dto.AppointmentResponse;
import com.example.demo.dto.DoctorDTO;
import com.example.demo.dto.PacientDTO;
import com.example.demo.hateoas.DoctorHateoas;
import com.example.demo.repository.AppointmentRepository;
import com.example.demo.repository.DoctorRepository;
import com.example.demo.validators.DoctorValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class DoctorService {

    @Autowired
    DoctorRepository doctorRepository;

    @Autowired
    DoctorHateoas doctorHateoas;


    AppointmentRepository appointmentRepository;

    private Integer itemsPerPage = 4;

    @Autowired
    public DoctorService(AppointmentRepository appointmentRepository)
    {
        this.appointmentRepository = appointmentRepository;
    }

    public ResponseEntity<CollectionModel<EntityModel<DoctorDTO>>> getAllDoctors(String specialization){
        List<DoctorDTO> doctors;
        Link l = linkTo(methodOn(MedController.class).getAllDoctors("",null,null)).withSelfRel();

        try {
            doctors = doctorRepository.findAll();


        }catch (Exception e)
        {
            System.out.println("Tabela nu exista!");
            CollectionModel<EntityModel<DoctorDTO>> doctorCollectionModel = CollectionModel.of(CollectionModel.empty(),l);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(doctorCollectionModel);

        }

        if (doctors.isEmpty()) {

            CollectionModel<EntityModel<DoctorDTO>> doctorCollectionModel = CollectionModel.of(CollectionModel.empty(),l);
            return ResponseEntity.status(HttpStatus.OK).body(doctorCollectionModel);
        }

        else if(specialization!= null && !specialization.isEmpty()){
            doctors = doctors.stream()
                    .filter(doctor -> doctor.getSpecializare().toString().toLowerCase().contains(specialization.toLowerCase()))
                    .collect(Collectors.toList());

        }


        return ResponseEntity.ok(doctorHateoas.toCollectionModel(doctors));


    }

    public Integer getUidById( Integer id){

        try {
            DoctorDTO doc = doctorRepository.findByIdDoctor(id);
            if (doc == null) {

                return -1;
            }
            else
                return doc.getIdUser();

        }catch (Exception e)
        {
            System.out.println("Tabela nu exista!");
            return -1;
        }



    }


    public ResponseEntity<?> createDoctor(DoctorDTO doctor, BindingResult bindingResult){

        DoctorValidator doctorValidator = new DoctorValidator();
        doctorValidator.validate(doctor,bindingResult);

        if(bindingResult.hasErrors())
        {
            List<String> errors = new ArrayList<>();
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                errors.add(fieldError.getField() + ": " + fieldError.getDefaultMessage());
            }
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(errors);
        }

        try {
            doctorRepository.save(doctor);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            Link l = linkTo(methodOn(MedController.class).getAllDoctors(null,null,null)).withRel("parent");
            System.out.println("No table");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(l);
        }
        Link l = linkTo(methodOn(MedController.class).getDoctorById(doctor.getIdDoctor())).withRel("doctorDetails");
        return ResponseEntity.status(HttpStatus.CREATED).body(l);
    }

    public ResponseEntity<?> getDoctorById(int id){
        Link l2 = linkTo(methodOn(MedController.class).getAllDoctors(null,null,null)).withRel("parent");
        try {
            DoctorDTO doctor = doctorRepository.findByIdDoctor(id);
            if (doctor == null) {
                Link l = linkTo(methodOn(MedController.class).getDoctorById(id)).withSelfRel();
                //Link l2 = linkTo(methodOn(MedController.class).getAllDoctors(null)).withRel("parent");
                CollectionModel<EntityModel<DoctorDTO>> emptyCollectionModel = CollectionModel.of(CollectionModel.empty(),l,l2);
                return ResponseEntity.status(HttpStatus.OK).body(emptyCollectionModel);

            }
            EntityModel<DoctorDTO> doctorDTOEntityModel = doctorHateoas.toModel(doctor);
            return ResponseEntity.ok(doctorDTOEntityModel);
        }
        catch (Exception e)
        {
            System.out.println("Tabela nu exista!");
        }

        return ResponseEntity.status(HttpStatus.OK).body(CollectionModel.of(CollectionModel.empty(),l2));

        //return ResponseEntity.status(HttpStatus.NOT_FOUND).body(CollectionModel.empty());
    }

    public ResponseEntity<?> findAppointmentsByDoctor(Integer id){

        Link l = linkTo(methodOn(MedController.class).findAppointmentsByDoctor(id)).withSelfRel();
        Link l2 = linkTo(methodOn(MedController.class).getAllDoctors(null,null,null)).withRel("parent");
        try {
            DoctorDTO doctor = doctorRepository.findByIdDoctor(id);

            if (doctor != null) {
                List<AppointmentDTO> listAp = appointmentRepository.findByAppointmentPkDoctor(doctor);
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
                                    linkTo(methodOn(MedController.class).findAppointmentsByDoctor(id)).withSelfRel()
                            )).collect(Collectors.toList());

                    System.out.println(brief.get(0).getStatusAp());


                    return ResponseEntity.ok(CollectionModel.of(app, l, l2));
                } else
                    return ResponseEntity.status(HttpStatus.OK).body(CollectionModel.of(CollectionModel.empty(), l, l2));

            }
        }catch (Exception e)
        {
            System.out.println("Tabela nu exista!");
            System.out.println(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.OK).body(CollectionModel.of(CollectionModel.empty(),l,l2));


    }


    public ResponseEntity<?> getDoctorsByPage(Integer page,Integer items ){

        if(items!= null && items>0)
        {
            this.itemsPerPage = items;
        }


        try{
            List<DoctorDTO> doctorList = doctorRepository.findAll();
            Integer indexStart = page * this.itemsPerPage;
            Integer indexStop = indexStart + this.itemsPerPage;

            List<EntityModel<DoctorDTO>> modelDoctor = new ArrayList<>();
            for(Integer i = indexStart; i<indexStop; i++){
                if(i<doctorList.size())
                    modelDoctor.add(doctorHateoas.toModel(doctorList.get(i)));
            }

            Link selfLink = linkTo(methodOn(MedController.class).getAllDoctors(null,page,itemsPerPage)).withSelfRel();
            Link parent = linkTo(methodOn(MedController.class).getAllDoctors(null,null,null)).withRel("parent");

            Integer next = page + 1;

            Link nextLink = linkTo(methodOn(MedController.class).getAllDoctors(null,next,itemsPerPage)).withRel("next");

            Integer prev = page - 1;

            Link prevLink = linkTo(methodOn(MedController.class).getAllDoctors(null,prev,itemsPerPage)).withRel("prev");


            if(modelDoctor.isEmpty() && page!=0) // ultima pagina ---> nu exista next
            {
                    prev = doctorList.size() / this.itemsPerPage - 1;
                    prevLink = linkTo(methodOn(MedController.class).getAllDoctors(null,prev,this.itemsPerPage)).withRel("prev");

                    return ResponseEntity.status(HttpStatus.OK).body(CollectionModel.of(modelDoctor,selfLink,parent,prevLink));

            }

            if(page== 0)  // prima pagina ----> nu exista prev
            {
                return ResponseEntity.status(HttpStatus.OK).body(CollectionModel.of(modelDoctor,selfLink,parent,nextLink));
            }

            if(page == (modelDoctor.size()/this.itemsPerPage - 1))
            {
                return ResponseEntity.status(HttpStatus.OK).body(CollectionModel.of(modelDoctor,selfLink,parent,prevLink));
            }

            // ramane pagina cu next si prev
            return ResponseEntity.status(HttpStatus.OK).body(CollectionModel.of(modelDoctor,selfLink,parent,nextLink,prevLink));


        }catch (Exception e)
        {
            System.out.println(e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // nu exita tabela
    }


    public ResponseEntity<?> getAppointmentByDate(Integer id,Integer date,String type)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try{
            DoctorDTO doctor = doctorRepository.findByIdDoctor(id);

            List<AppointmentDTO> appList = new ArrayList<>();
            appList = appointmentRepository.findByAppointmentPkDoctor(doctor);


            List<AppointmentResponse> appBrief = new ArrayList<>();
            if(appList!=null)
            {
                appList = appList.stream()
                        .filter(app -> isMatchingDate(app,date,type) == true)
                        .collect(Collectors.toList());


                for (AppointmentDTO appointmentDTO : appList) {
                    AppointmentResponse response = new AppointmentResponse();

                    response.setStatusAp(appointmentDTO.getStatus());
                    response.setData(appointmentDTO.getAppointmentPk().getData());
                    response.setNumePacient(appointmentDTO.getAppointmentPk().getPacient().getNume());
                    response.setPrenumePacient(appointmentDTO.getAppointmentPk().getPacient().getPrenume());
                    response.setNumeDoctor(appointmentDTO.getAppointmentPk().getDoctor().getNume());
                    response.setPrenumeDoctor(appointmentDTO.getAppointmentPk().getDoctor().getPrenume());

                    appBrief.add(response);
                }

            }
            Link selfLink = linkTo(methodOn(MedController.class).getDoctorAppointmentByDate(id,date,type)).withSelfRel();
            Link parentLink = linkTo(methodOn(MedController.class).findAppointmentsByDoctor(doctor.getIdDoctor())).withRel("parent");

            return ResponseEntity.status(HttpStatus.OK).body(CollectionModel.of(appBrief,selfLink,parentLink));



        }catch (Exception e)
        {
            System.out.println(e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);
    }

    private boolean isMatchingDate(AppointmentDTO appointment, Integer date, String type) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(appointment.getAppointmentPk().getData());

        switch (type) {
            case "day":
                System.out.println(Calendar.DAY_OF_MONTH);
                return calendar.get(Calendar.DAY_OF_MONTH) == date;
            case "month":
                return calendar.get(Calendar.MONTH) + 1 == date;
            case "year":
                //System.out.println(calendar.get(Calendar.YEAR));
                //System.out.println(date);
                //System.out.println(appointment.getAppointmentPk().getData());
                return calendar.get(Calendar.YEAR) == date;
            default:
                return false;
        }
    }



}
