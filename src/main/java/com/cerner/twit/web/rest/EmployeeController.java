package com.cerner.twit.web.rest;

import com.cerner.twit.domain.Employee;
import com.cerner.twit.domain.Follower;
import com.cerner.twit.domain.Tweet;
import com.cerner.twit.model.EmployeeDTO;
import com.cerner.twit.model.FeedDTO;
import com.cerner.twit.model.FollowerDTO;
import com.cerner.twit.model.mappers.EmployeeMapper;
import com.cerner.twit.model.mappers.FollowerMapper;
import com.cerner.twit.model.mappers.TweetMapper;
import com.cerner.twit.repository.EmployeeRepository;
import com.cerner.twit.repository.FollowerRepository;
import com.cerner.twit.repository.TweetRepository;
import com.cerner.twit.web.rest.util.HeaderUtil;
import com.cerner.twit.web.rest.util.PaginationUtil;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class EmployeeController {

    private static final String RESOURCE_NAME = "employee";

    EmployeeRepository employeeRepository;
    EmployeeMapper employeeMapper;

    @Autowired
    FollowerMapper followerMapper;

    @Autowired
    FollowerRepository followerRepository;

    @Autowired
    TweetRepository tweetRepository;

    @Autowired
    TweetMapper tweetMapper;

    public EmployeeController(
        EmployeeRepository employeeRepository, EmployeeMapper employeeMapper) {
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
    }

    /**
     * {@code GET  /employees} : get all the employees.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of employees in body.
     */
    @GetMapping("/employees")
    public List<EmployeeDTO> getAllEmployees(Pageable pageable) {
        Page<Employee> employees = employeeRepository.findAll(pageable);
        //return employees.getContent();
        return employeeMapper.toDto(employees.getContent());
    }

    @GetMapping("/employees/{id}")
    public ResponseEntity<EmployeeDTO> getEmployee(@PathVariable Long id) {
        Optional<Employee> employee = employeeRepository.findById(id);
        return employee.map(emp -> ResponseEntity.ok().body(employeeMapper.toDto(emp)))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/employees")
    public ResponseEntity<EmployeeDTO> createEmployee(@Valid @RequestBody EmployeeDTO employeeDTO)
        throws URISyntaxException {
        if (employeeDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil
                .createFailureAlert(RESOURCE_NAME, "id exists",
                    "ID cannot be assigned to " + RESOURCE_NAME)).body(null);
        }
        Employee employee = employeeMapper.toEntity(employeeDTO);
        employee = employeeRepository.save(employee);

        return ResponseEntity.created(new URI("/api/employees/" + employee.getId()))
            .body(employeeMapper.toDto(employee));
    }

    @PutMapping("/employees/{id}")
    public ResponseEntity<EmployeeDTO> updateEmployee(
        @PathVariable Long id, @Valid @RequestBody EmployeeDTO employeeDTO)
        throws URISyntaxException {
        Employee employeeUpdated = employeeMapper.toEntity(employeeDTO);
        System.out.println(employeeUpdated);

        Employee emp = employeeRepository.findById(id)
            .map(employee -> {
                this.updateEmployee(employee, employeeUpdated);
                return employeeRepository.save(employee);
            })
            .orElseGet(() -> employeeRepository.save(employeeUpdated));

        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(RESOURCE_NAME, String.valueOf(id)))
            .body(employeeMapper.toDto(emp));
    }

    /**
     * DELETE  /employees/:id : delete the "id" employee.
     *
     * @param id the id of the employee to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/employees/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeRepository.deleteById(id);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityDeletionAlert(RESOURCE_NAME, id.toString())).build();
    }

    private void updateEmployee(Employee employee, Employee employeeNew) {
        employee.setEmail(employeeNew.getEmail());
        employee.setAccountName(employeeNew.getAccountName());
        employee.setGender(employeeNew.getGender());
        employee.setPhone(employee.getPhone());
        employee.setProfileImageUrl(employeeNew.getProfileImageUrl());
    }

    /**
     * Add an employee to current Employee's follower list
     *
     * @param id employee id
     * @param followerDTO post body
     * @return followerDTO
     * @throws URISyntaxException exception
     */
    @PostMapping("/employees/{id}/followers")
    public ResponseEntity<FollowerDTO> addFollower(
        @PathVariable Long id, @Valid @RequestBody FollowerDTO followerDTO)
        throws URISyntaxException {
        Optional<Employee> employee = employeeRepository.findById(id);
        if(!employee.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Follower follower = followerMapper.toEntity(followerDTO);
        follower.setEmployee(employee.get());
        followerRepository.save(follower);

        return ResponseEntity.created(new URI("/api/employees/" + employee.get().getId()))
            .body(followerMapper.toDto(follower));
    }

    /**
     * Get all followers for the employee
     * @param id id of the employee
     * @return list of followers
     */
    @GetMapping("/employees/{id}/followers")
    public List<FollowerDTO> getAllFollowing(@PathVariable Long id) {
        Optional<Employee> employee = employeeRepository.findById(id);
        List<FollowerDTO> result = null;
        if(employee.isPresent()) {
            Set<Follower> followers = employee.get().getFollowers();
            result = followers.stream().map(e -> followerMapper.toDto(e)).collect(Collectors.toList());
        }
        return result;
    }

    @DeleteMapping("/employees/{id}/followers")
    public ResponseEntity<Void> unfollow(
        @PathVariable Long id, @Valid @RequestBody FollowerDTO followerDTO) {
        Optional<Long> followerRecordId =
            followerRepository.getFollower(id, followerDTO.getFollower());
        ResponseEntity<Void> response;
        if (followerRecordId.isPresent()) {
            followerRepository.deleteById(followerRecordId.get());
            response = ResponseEntity.ok()
                .headers(HeaderUtil.createEntityDeletionAlert(RESOURCE_NAME, id.toString()))
                .build();
        } else {
            response = ResponseEntity.notFound().build();
        }
        return response;
    }

    @GetMapping("/employees/{id}/feed")
    public ResponseEntity<FeedDTO> getTweetFeed(@PathVariable Long id, Pageable pageable) {
        Optional<Employee> employee = employeeRepository.findById(id);
        if(!employee.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Set<Follower> followers = employee.get().getFollowers();
        List<Long> followerIds =
            followers.stream().map(f -> f.getFollower().getId()).collect(Collectors.toList());

        pageable =
            PageRequest.of(pageable.getPageNumber(), 100, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Tweet> tweets = tweetRepository.getTweetsFromFollowers(followerIds, pageable);
        HttpHeaders headers =
            PaginationUtil.generatePaginationHttpHeaders(tweets, "/api/employees/" + id + "/feed");

        return new ResponseEntity<>(
            FeedDTO.builder().tweets(tweetMapper.toDto(tweets.getContent())).build(), headers, HttpStatus.OK);
    }
}
