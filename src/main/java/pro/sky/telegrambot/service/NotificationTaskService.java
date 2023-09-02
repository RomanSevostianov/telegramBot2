package pro.sky.telegrambot.service;

import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import pro.sky.telegrambot.entity.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

@Service
@AllArgsConstructor
public class NotificationTaskService {

    private final NotificationTaskRepository notificationTaskRepository;

    public void save(NotificationTask notificationTask) {
        notificationTaskRepository.save(notificationTask);
    }
}
