package com.cerner.twit;

import com.cerner.twit.domain.Employee;
import com.cerner.twit.repository.EmployeeRepository;
import com.cerner.twit.repository.TweetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class TwitApplication {

	@Autowired
	EmployeeRepository employeeRepository;

	@Autowired
	TweetRepository tweetRepository;

	@Component
	class DataSetup implements ApplicationRunner {
		@Override
		public void run(ApplicationArguments args) throws Exception {
			Employee emp = Employee.builder()
				.accountName("chandu")
				.fullName("Chandrakanth Dudela")
				.gender("Male")
				.email("ckanth502@gmail.com")
				.phone("2516452341")
				.profileImageUrl("http://imgur.com/xtryuh").build();
			Employee emp2 = Employee.builder()
				.accountName("sindhu")
				.fullName("Sindhu Madhuri")
				.gender("Female")
				.email("sindhu318@gmail.com")
				.phone("2516452341")
				.profileImageUrl("http://imgur.com/xtryuh").build();
			Employee emp3 = Employee.builder()
				.accountName("chandu502")
				.fullName("Chandu Dudela")
				.gender("Male")
				.email("ckanth5021@gmail.com")
				.phone("2516452341")
				.profileImageUrl("http://imgur.com/xtryuh").build();

			employeeRepository.save(emp);
			employeeRepository.save(emp2);
			employeeRepository.save(emp3);
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(TwitApplication.class, args);
	}

}
