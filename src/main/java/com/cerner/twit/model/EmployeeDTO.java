package com.cerner.twit.model;

import java.io.Serializable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class EmployeeDTO implements Serializable {
    private Long id;

    @NotNull
    private String accountName;

    @NotNull
    private String fullName;

    @NotNull
    private String gender;

    @NotNull
    private String email;

    private String phone;

    @Size(max = 1024)
    private String profileImageUrl;
}
