package com.cerner.twit.repository;

import com.cerner.twit.domain.Follower;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowerRepository extends JpaRepository<Follower, Long> {

    @Query("SELECT id FROM Follower t WHERE t.employee.id = ?1 AND t.follower.id = ?2")
    Optional<Long> getFollower(Long employeeId, Long followerId);
}
