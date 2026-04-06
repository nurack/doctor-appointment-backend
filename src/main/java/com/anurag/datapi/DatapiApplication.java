package com.anurag.datapi;

import com.anurag.datapi.notification.dto.NotificationDTO;
import com.anurag.datapi.notification.service.NotificationService;
import com.anurag.datapi.users.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@RequiredArgsConstructor
public class DatapiApplication {

	//private final NotificationService notificationService;

	public static void main(String[] args) {
		SpringApplication.run(DatapiApplication.class, args);
	}

	@Bean
	CommandLineRunner runner() {
		return args -> {
			NotificationDTO notificationDTO = NotificationDTO.builder()
					.recipient("anurag22311@gmail.com")
					.subject("testing email")
					.message("heyhhihihih iiiiii")
					.build();

			//gnotificationService.sendMail(notificationDTO, new User());
		};


	}

}
