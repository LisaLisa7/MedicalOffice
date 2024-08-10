package com.example.demo.repository;

import com.example.demo.dto.PacientDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface PacientRepository extends JpaRepository<PacientDTO,String>{
    PacientDTO findByIdUser(int id_user);
    PacientDTO findByCNP(String cnp);
    PacientDTO findByEmail(String email);
    ArrayList<PacientDTO> findAll();


}
