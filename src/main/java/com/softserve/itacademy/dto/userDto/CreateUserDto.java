package com.softserve.itacademy.dto.userDto;

import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CreateUserDto extends UpdateUserDto {

    @Pattern(regexp = "(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}",
            message = "Must be minimum 8 characters, at least one letter and one number")
    private String password;

}
