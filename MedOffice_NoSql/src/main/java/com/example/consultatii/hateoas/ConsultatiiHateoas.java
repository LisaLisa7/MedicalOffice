package com.example.consultatii.hateoas;

import com.example.consultatii.controller.MedController;
import com.example.consultatii.dto.ConsultatiiDTO;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.server.RepresentationModelAssembler;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.stereotype.Component;

@Component
public class ConsultatiiHateoas implements RepresentationModelAssembler<ConsultatiiDTO, EntityModel<ConsultatiiDTO>> {

    @Override
    public EntityModel<ConsultatiiDTO> toModel(ConsultatiiDTO entity) {
        return EntityModel.of(entity,
                linkTo(methodOn(MedController.class).getAllC()).withSelfRel());
    }


    public CollectionModel<EntityModel<ConsultatiiDTO>> toCollectionModel(List<ConsultatiiDTO> consultatiiDTOList) {
        List<EntityModel<ConsultatiiDTO>> consultatiiModel = consultatiiDTOList.stream()
                .map(this::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of((consultatiiModel),
                linkTo(methodOn(MedController.class).getAllC()).withSelfRel());
    }
}
