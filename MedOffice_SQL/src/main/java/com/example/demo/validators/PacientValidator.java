package com.example.demo.validators;

import com.example.demo.dto.PacientDTO;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class PacientValidator implements Validator {

    @Override
    public boolean supports(Class<?> c) {
        return PacientDTO.class.isAssignableFrom(c);
    }

    @Override
    public void validate(Object target, Errors errors) {

        PacientDTO pacientDTO = (PacientDTO) target;

        // CNP
        if (pacientDTO.getCNP() == null || pacientDTO.getCNP().length() != 13 ) {
            errors.rejectValue("CNP", "cnp.invalid", "CNP-ul nu are lungimea corecta!");
        }
        if(!pacientDTO.getCNP().matches("\\d+"))
        {
            errors.rejectValue("CNP", "cnp.invalid", "CNP-ul trebuie sa contina numai cifre!");
        }

        // nume
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "nume", "nume.empty", "Camp obligatoriu");

        // prenume
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "prenume", "prenume.empty", "Camp obligatoriu");

        //email
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "email.empty", "Camp obligatoriu");
        if (!EmailValidator.getInstance().isValid(pacientDTO.getEmail())) {
            errors.rejectValue("email", "email.invalid", "Adresa de email nu este valida");
        }

        //telefon
        ValidationUtils.rejectIfEmptyOrWhitespace(errors,"phoneNumber","telefon.empty","Camp obligatoriu");

        if(pacientDTO.getPhoneNumber().length() != 10 || !pacientDTO.getPhoneNumber().startsWith("07") || !pacientDTO.getPhoneNumber().substring(2).matches("\\d+") )
        {
            errors.rejectValue("phoneNumber","telefon.invalid","Format invalid");
        }

        // data de nastere
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "birthday", "birthday.empty", "Camp obligatoriu");


        try {
            Date birthDate = pacientDTO.getBirthday();
            Date currentDate = new Date();

            if (birthDate.after(currentDate)) {
                errors.rejectValue("birthday", "birthday.invalid", "Data de nastere nu poate fi in viitor");
            }

        } catch (Exception e) {
            errors.rejectValue("birthday", "birthday.invalid", "Format de data invalid");
        }


    }



}
