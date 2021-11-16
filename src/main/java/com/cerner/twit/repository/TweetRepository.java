package com.cerner.twit.repository;

import com.cerner.twit.domain.Tweet;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TweetRepository extends JpaRepository<Tweet, Long> {

    @Query("SELECT t FROM Tweet t WHERE t.employee.id in ?1")
    Page<Tweet> getTweetsFromFollowers(List<Long> followers, Pageable pageable);
}
