package com.example.demo.errors;

import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.Link;

@Getter
@Setter
public class ErrorResponse {
    private String message;
    private Link link;

    public ErrorResponse(String message, Link link) {
        this.message = message;
        this.link = link;
    }


}