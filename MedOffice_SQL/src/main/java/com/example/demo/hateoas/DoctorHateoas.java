package com.example.demo.hateoas;


import com.example.demo.controller.MedController;
import com.example.demo.dto.DoctorDTO;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import javax.print.Doc;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class DoctorHateoas implements RepresentationModelAssembler<DoctorDTO, EntityModel<DoctorDTO>> {

    @Override
    public EntityModel<DoctorDTO> toModel(DoctorDTO doctor) {
        return EntityModel.of(doctor,
                linkTo(methodOn(MedController.class).getDoctorById(doctor.getIdDoctor())).withSelfRel(),
                linkTo(methodOn(MedController.class).getAllDoctors(null,null,null)).withRel("parent"));
    }

    public CollectionModel<EntityModel<DoctorDTO>> toCollectionModel(List<DoctorDTO> doctorDTOList) {

        List<EntityModel<DoctorDTO>> doctorModels = doctorDTOList.stream()
                .map(this::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(doctorModels,
                linkTo(methodOn(MedController.class).getAllDoctors(null,null,null)).withSelfRel());

    }
}
