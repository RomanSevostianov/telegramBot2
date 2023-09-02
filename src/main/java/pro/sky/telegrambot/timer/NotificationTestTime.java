package pro.sky.telegrambot.timer;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.scheduling.annotation.Scheduled;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

public class NotificationTestTime {
    private NotificationTaskRepository notificationTaskRepository;
    private TelegramBot telegramBot;

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.MINUTES)
    public void task() {

      notificationTaskRepository.findAllByDateTime(
                LocalDateTime.now().truncatedTo(
                        ChronoUnit.MINUTES)
        ).forEach(notificationTask -> {
            telegramBot.execute(
                    new SendMessage(notificationTask.getChatId(),
                            "Вы просили напомнить о задаче: " + notificationTask.getMessage()));
            notificationTaskRepository.delete(notificationTask);
        });
    }
}
