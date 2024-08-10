package com.example.demo.repository;

import com.example.demo.dto.DoctorDTO;
import com.example.demo.dto.Specializare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<DoctorDTO,Integer> {

    DoctorDTO findByIdDoctor(int id_doctor);
    ArrayList<DoctorDTO> findAll();

}
