package com.cerner.twit.model;

import java.io.Serializable;
import java.time.Instant;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class TweetDTO implements Serializable {
    private Long id;

    @NotNull
    private String tweetContent;

    @PositiveOrZero
    private Long likes;

    @NotNull
    private String createdBy;

    private Instant createdAt;

    private String lastModifiedAt;
}
