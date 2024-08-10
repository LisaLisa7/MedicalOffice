package com.example.demo.dto;

public enum Specializare {
    Cardiologie,
    Pediatrie,
    Ortopedie,
    Dermatologie,
    Neurologie,
    Chirurgie,
    Oftalmologie,
    Endocrinologie,
    Psihiatrie,
    Urologie;

    public static boolean contains(String value) {
        for (Specializare specializare : Specializare.values()) {
            if (specializare.name().equals(value)) {
                return true;
            }
        }
        return false;
    }
}
