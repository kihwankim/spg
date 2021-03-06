package com.cnu.spg.dto.user;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class UserInfoDto {
    @NotBlank(message = "is required")
    @NotNull(message = "is required")
    @Size(min = 1, message = "too short")
    private String username;

    @NotBlank(message = "is required")
    @NotNull(message = "is required")
    @Size(min = 1, message = "too short")
    private String name;

    public UserInfoDto() {
    }

    public UserInfoDto(String username, String name) {
        this.username = username;
        this.name = name;
    }
}
