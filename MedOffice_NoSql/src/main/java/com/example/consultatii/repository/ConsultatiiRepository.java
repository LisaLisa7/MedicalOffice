package com.example.consultatii.repository;

import com.example.consultatii.dto.ConsultatiiDTO;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ConsultatiiRepository extends MongoRepository<ConsultatiiDTO,String> {

    List<ConsultatiiDTO> findAll();
    List<ConsultatiiDTO> findByIdDoctor(Integer idDoctor);
}
