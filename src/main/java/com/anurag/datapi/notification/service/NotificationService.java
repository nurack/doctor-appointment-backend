package com.anurag.datapi.notification.service;

import com.anurag.datapi.notification.dto.NotificationDTO;
import com.anurag.datapi.users.entity.User;

public interface NotificationService {

    void sendMail(NotificationDTO notificationDTO, User user);

}
