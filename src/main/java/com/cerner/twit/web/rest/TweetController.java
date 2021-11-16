package com.cerner.twit.web.rest;

import com.cerner.twit.domain.Tweet;
import com.cerner.twit.model.TweetDTO;
import com.cerner.twit.model.mappers.TweetMapper;
import com.cerner.twit.repository.TweetRepository;
import com.cerner.twit.web.rest.util.HeaderUtil;
import com.cerner.twit.web.rest.util.PaginationUtil;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TweetController {
    private static final String RESOURCE_NAME = "tweet";

    TweetRepository tweetRepository;
    TweetMapper tweetMapper;

    public TweetController(TweetRepository tweetRepository, TweetMapper tweetMapper) {
        this.tweetRepository = tweetRepository;
        this.tweetMapper = tweetMapper;
    }

    @GetMapping("/tweets")
    public ResponseEntity<List<TweetDTO>> getAllTweets(Pageable pageable) {
        Page<Tweet> tweetsPage = tweetRepository.findAll(pageable);
        HttpHeaders headers =
            PaginationUtil.generatePaginationHttpHeaders(tweetsPage, "/api/tweets");
        return new ResponseEntity<>(
            tweetMapper.toDto(tweetsPage.getContent()), headers, HttpStatus.OK);
    }

    @GetMapping("/tweets/{id}")
    public ResponseEntity<TweetDTO> getTweet(@PathVariable Long id) {
        Optional<Tweet> tweet = tweetRepository.findById(id);
        return tweet.map(t -> ResponseEntity.ok().body(tweetMapper.toDto(t)))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/tweets")
    public ResponseEntity<TweetDTO> createTweet(@Valid @RequestBody TweetDTO tweetDTO)
        throws URISyntaxException {
        if (tweetDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil
                .createFailureAlert(RESOURCE_NAME, "id exists",
                    "ID cannot be assigned to " + RESOURCE_NAME)).body(null);
        }
        Tweet tweet = tweetMapper.toEntity(tweetDTO);
        tweet.setCreatedAt(Instant.now());
        tweet.setLastModifiedAt(tweet.getCreatedAt());
        tweet = tweetRepository.save(tweet);

        return ResponseEntity.created(new URI("/api/tweets/" + tweet.getId()))
            .body(tweetMapper.toDto(tweet));
    }

    @PutMapping("/tweets/{id}")
    public ResponseEntity<TweetDTO> updateTweet(
        @PathVariable Long id, @Valid @RequestBody TweetDTO tweetDTO)
        throws URISyntaxException {
        Tweet tweetUpdated = tweetMapper.toEntity(tweetDTO);

        Tweet tweet = tweetRepository.findById(id)
            .map(tweet1 -> {
                this.updateTweet(tweet1, tweetUpdated);
                return tweetRepository.save(tweet1);
            })
            .orElseGet(() -> tweetRepository.save(tweetUpdated));

        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(RESOURCE_NAME, String.valueOf(id)))
            .body(tweetMapper.toDto(tweet));
    }

    private void updateTweet(Tweet existingTweet, Tweet modifiedTweet) {
        existingTweet.setTweetContent(modifiedTweet.getTweetContent());
        existingTweet.setLastModifiedAt(Instant.now());
    }
}
