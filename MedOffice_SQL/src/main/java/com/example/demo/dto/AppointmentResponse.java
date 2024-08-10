package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentResponse {

    StatusAp statusAp;

    String numePacient;
    String prenumePacient;

    String numeDoctor;
    String prenumeDoctor;

    Date data;

}
