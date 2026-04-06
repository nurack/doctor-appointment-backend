package com.anurag.datapi.notification.service;

import com.anurag.datapi.enums.NotificationType;
import com.anurag.datapi.notification.dto.NotificationDTO;
import com.anurag.datapi.notification.entity.Notification;
import com.anurag.datapi.notification.repo.NotificationRepo;
import com.anurag.datapi.users.entity.User;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService{

    private final NotificationRepo notificationRepo;
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Override
    @Async
    public void sendMail(NotificationDTO notificationDTO, User user) {

        try{

            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    mimeMessage,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );

            helper.setTo(notificationDTO.getRecipient());
            helper.setFrom(user.getEmail());
            helper.setSubject(notificationDTO.getSubject());

            if(notificationDTO.getTemplateName() != null) {
                Context context = new Context();
                context.setVariables(notificationDTO.getTemplateVariable());
                String htmlContent = templateEngine.process(notificationDTO.getTemplateName(),context);
                helper.setText(htmlContent, true);
            } else {
                helper.setText(notificationDTO.getMessage(), true);
            }

            javaMailSender.send(mimeMessage);
            log.info("Email sent");

            Notification notification = Notification.builder()
                    .recipient(notificationDTO.getRecipient())
                    .subject(notificationDTO.getSubject())
                    .message(notificationDTO.getMessage())
                    .type(NotificationType.EMAIL)
                    .user(user)
                    .build();

            notificationRepo.save(notification);

        } catch (Exception e) {
            log.info(e.getMessage());
        }


    }
}
