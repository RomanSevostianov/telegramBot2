package pro.sky.telegrambot.listener;


import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.entity.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;


import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@EnableScheduling
public class TelegramBotUpdatesListener implements UpdatesListener {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    @Autowired
    private NotificationTaskRepository notificationTaskRepository;

    @Autowired
    private TelegramBot telegramBot;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);

            String messageText = update.message().text();
            Long chatId = update.message().chat().id();

            Pattern pattern = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)");
            Matcher matcher = pattern.matcher(messageText);


            switch (messageText) {
                case "/start":

                    String textToSend = "Привет";
                    SendMessage message = new SendMessage(chatId, textToSend);
                    SendResponse response = telegramBot.execute(message);

                    break;

                default:

                    if (matcher.matches()) {

                        String textToSend2 = "Сохранение";
                        SendMessage message2 = new SendMessage(chatId, textToSend2);
                        SendResponse response2 = telegramBot.execute(message2);

                        save(update, matcher);
                    }
                    else {
                        String textToSend3 = "Введите коректные данные";
                        SendMessage message3 = new SendMessage(chatId, textToSend3);
                        SendResponse response3 = telegramBot.execute(message3);
                    }
            }

        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void save(Update update, Matcher matcher) {

        NotificationTask notificationTask = new NotificationTask();
        notificationTask.setChatId(update.message().chat().id());
        notificationTask.setFirstName(update.message().chat().firstName());
        notificationTask.setNotificationTime(LocalDateTime.parse(matcher.group(1),

                DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
        notificationTask.setNotificationText(matcher.group(3));

        notificationTaskRepository.save(notificationTask);
    }

    @Scheduled(cron = "0 0/1 * * * *")
    public void findNotificationTaskByTime() {

        List<NotificationTask> notificationList = notificationTaskRepository.findAllNotificationByCurrentDate();
        notificationList.stream().forEach(e -> {
            SendMessage message = new SendMessage(e.getChatId(), e.getNotificationText());
            telegramBot.execute(message);
        });
    }


}