package com.tilak.internship_platform.service;

import com.tilak.internship_platform.entity.Notification;
import com.tilak.internship_platform.entity.User;
import com.tilak.internship_platform.exception.ResourceNotFoundException;
import com.tilak.internship_platform.repository.NotificationRepository;
import com.tilak.internship_platform.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    @Transactional
    public void markAsRead(Long notifId, Long userId) {
        Notification notif = notificationRepository.findById(notifId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notifId));
        if (notif.getUser().getId().equals(userId)) {
            notif.setRead(true);
            notificationRepository.save(notif);
        }
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        List<Notification> list = notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
        for (Notification n : list) {
            n.setRead(true);
        }
        notificationRepository.saveAll(list);
    }

    @Transactional
    public Notification createNotification(Long userId, String title, String message, String link) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Notification notif = Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .link(link)
                .isRead(false)
                .build();

        return notificationRepository.save(notif);
    }
}
