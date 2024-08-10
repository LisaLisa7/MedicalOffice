package com.example.demo.hateoas;


import com.example.demo.controller.MedController;
import com.example.demo.dto.PacientDTO;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import com.example.demo.services.PacientService;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PacientHateoas implements RepresentationModelAssembler<PacientDTO, EntityModel<PacientDTO>> {

    @Override
    public EntityModel<PacientDTO> toModel(PacientDTO pacient) {
        return EntityModel.of(pacient,
                linkTo(methodOn(MedController.class).getPacientByUID(pacient.getIdUser())).withSelfRel(),
                linkTo(methodOn(MedController.class).getAllPacients()).withRel("parent"));
    }

    public CollectionModel<EntityModel<PacientDTO>> toCollectionModel(List<PacientDTO> pacientDTOList) {
        System.out.println("col_hateoas_in");
        List<EntityModel<PacientDTO>> pacientModels = pacientDTOList.stream()
                .map(this::toModel)
                .collect(Collectors.toList());
        System.out.println("col_hateoas_in");
        return CollectionModel.of(pacientModels,
                linkTo(methodOn(MedController.class).getAllPacients()).withSelfRel());

    }
}
