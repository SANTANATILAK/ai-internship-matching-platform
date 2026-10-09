package com.tilak.internship_platform.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://localhost:5174",
        "http://localhost:5175",
        "http://127.0.0.1:5173",
        "http://127.0.0.1:5174",
        "http://127.0.0.1:5175"
})
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @GetMapping
    public List<Map<String, Object>> getNotifications(Principal principal) {
        return List.of(
            Map.of(
                "id", 1,
                "title", "Resume Analysis Complete",
                "message", "Your ATS score and matching opportunities have been calculated.",
                "type", "SYSTEM",
                "isRead", false,
                "createdAt", LocalDateTime.now().minusHours(2)
            ),
            Map.of(
                "id", 2,
                "title", "Hourly Opportunity Sync",
                "message", "Verified top enterprise openings synced from Google, Microsoft, and partner feeds.",
                "type", "SYNC",
                "isRead", true,
                "createdAt", LocalDateTime.now().minusHours(1)
            )
        );
    }

    @PutMapping("/{id}/read")
    public Map<String, Object> markAsRead(@PathVariable Long id) {
        return Map.of("success", true, "message", "Notification marked as read");
    }

    @PutMapping("/read-all")
    public Map<String, Object> markAllAsRead() {
        return Map.of("success", true, "message", "All notifications marked as read");
    }
}
