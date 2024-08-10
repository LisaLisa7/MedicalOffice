package com.example.demo.validators;

import com.example.demo.dto.DoctorDTO;
import com.example.demo.dto.Specializare;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DoctorValidator implements Validator {

    @Override
    public boolean supports(Class<?> c) {
        return DoctorDTO.class.isAssignableFrom(c);
    }

    @Override
    public void validate(Object target, Errors errors) {
        DoctorDTO doctorDTO = (DoctorDTO) target;


        // nume
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "nume", "nume.empty", "Camp obligatoriu");

        // prenume
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "prenume", "prenume.empty", "Camp obligatoriu");

        //email
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "email.empty", "Camp obligatoriu");
        if (!EmailValidator.getInstance().isValid(doctorDTO.getEmail())) {
            errors.rejectValue("email", "email.invalid", "Adresa de email nu este valida");
        }

        //telefon
        ValidationUtils.rejectIfEmptyOrWhitespace(errors,"phoneNumber","telefon.empty","Camp obligatoriu");

        if(doctorDTO.getPhoneNumber().length() != 10 || !doctorDTO.getPhoneNumber().startsWith("07") || !doctorDTO.getPhoneNumber().substring(2).matches("\\d+") )
        {
            errors.rejectValue("phoneNumber","telefon.invalid","Format invalid");
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors,"specializare","specializare.empty","Camp obligatoriu");




    }
}
