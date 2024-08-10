package com.example.consultatii.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Document(collection = "consultatii")
public class ConsultatiiDTO {

    @Id
    String idConsultatie;

    String idPacient;

    int idDoctor;

    Date date;

    List<InvestigatiiDTO> investigatii;

    Diagnostic diagnostic;

}
