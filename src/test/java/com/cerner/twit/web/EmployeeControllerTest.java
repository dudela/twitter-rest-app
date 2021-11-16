package com.cerner.twit.web;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cerner.twit.domain.Employee;
import com.cerner.twit.model.EmployeeDTO;
import com.cerner.twit.model.mappers.EmployeeMapper;
import com.cerner.twit.model.mappers.FollowerMapper;
import com.cerner.twit.model.mappers.TweetMapper;
import com.cerner.twit.repository.EmployeeRepository;
import com.cerner.twit.repository.FollowerRepository;
import com.cerner.twit.repository.TweetRepository;
import com.cerner.twit.web.rest.EmployeeController;
import java.util.Arrays;
import java.util.Optional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@WebMvcTest(controllers = EmployeeController.class)
public class EmployeeControllerTest {

    @Autowired
    static MockMvc mockMvc;

    @MockBean
    static EmployeeRepository employeeRepository;

    @MockBean
    static EmployeeMapper employeeMapper;

    @MockBean
    FollowerMapper followerMapper;

    @MockBean
    TweetMapper tweetMapper;

    @MockBean
    FollowerRepository followerRepository;

    @MockBean
    TweetRepository tweetRepository;

    //@MockBean
    //static Pageable pageable;

    static Page<Employee> employeePage;

    static private Employee employee;

    static AutoCloseable autoCloseable;

    @BeforeAll
    static void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(EmployeeControllerTest.class);
        EmployeeController employeeController =
            new EmployeeController(employeeRepository, employeeMapper);
        mockMvc = MockMvcBuilders.standaloneSetup(employeeController).build();

        employee = createEntity();
        //Page<Employee> employeePage = new PageImpl<>(Arrays.asList(employee));

        //when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        //given(employeeRepository.findAll(any(Pageable.class))).willReturn(employeePage);
        //when(employeePage.getContent()).thenReturn(Arrays.asList(employeeMock));
        //when(employeeMapper.toDto(employeeMock)).thenReturn(getEntityDTO());
    }

    @AfterAll
    static void tearDown() throws Exception {
        autoCloseable.close();
    }

    public static Employee createEntity() {
        return Employee.builder()
            .id(1L)
            .accountName("test")
            .email("test")
            .fullName("test")
            .gender("MALE")
            .profileImageUrl("https")
            .phone("213")
            .build();
    }

    public static EmployeeDTO getEntityDTO() {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(1L);
        dto.setAccountName("test");
        dto.setEmail("test");
        return dto;
    }

    @Test
    public void getEmployee() throws Exception {
        //given(employeeRepository.findAll(any(Pageable.class))).willReturn(employeePage);
        given(employeeRepository.findById(1L)).willReturn(Optional.of(employee));
        mockMvc.perform(get("/api/employees/{id}", 1L))
            .andDo(print())
            //.contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
            //.andExpect(jsonPath("$.id", is(1L)));
    }
}
