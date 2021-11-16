package com.cerner.twit.model.mappers;

import com.cerner.twit.domain.Tweet;
import com.cerner.twit.model.TweetDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TweetMapper extends EntityMapper<TweetDTO, Tweet> {
    @Mapping(target = "employee.id", source = "createdBy")
    Tweet toEntity(TweetDTO dto);

    @Mapping(target = "createdBy", source = "employee.id")
    TweetDTO toDto(Tweet tweet);
}
