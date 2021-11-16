package com.cerner.twit.model;

import java.io.Serializable;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class FollowerDTO implements Serializable {

    @NotNull
    private Long follower;
}
