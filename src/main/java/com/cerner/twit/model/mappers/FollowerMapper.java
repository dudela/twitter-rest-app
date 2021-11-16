package com.cerner.twit.model.mappers;

import com.cerner.twit.domain.Follower;
import com.cerner.twit.model.FollowerDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FollowerMapper extends EntityMapper<FollowerDTO, Follower> {

    @Mapping(source = "follower.id", target = "follower")
    FollowerDTO toDto(Follower follower);

    @Mapping(source = "follower", target = "follower.id")
    Follower toEntity(FollowerDTO followerDTO);
}
