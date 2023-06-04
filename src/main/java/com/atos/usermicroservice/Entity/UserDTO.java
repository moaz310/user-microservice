package com.atos.usermicroservice.Entity;

import jakarta.validation.constraints.*;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class UserDTO {

    @NotBlank
    private String userName;

    private String firstName;

    private String lastName;

    @NotBlank
    private String password;

    @Pattern(regexp = "^(Teacher|Student)$")
    private String role;

    @Email
    @NotNull
    private String email;


}
