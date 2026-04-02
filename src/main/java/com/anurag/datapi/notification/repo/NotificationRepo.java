package com.anurag.datapi.notification.repo;

import com.anurag.datapi.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepo extends JpaRepository<Notification, Long> {
}
