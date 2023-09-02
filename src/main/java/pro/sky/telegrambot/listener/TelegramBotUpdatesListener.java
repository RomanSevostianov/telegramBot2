package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.entity.NotificationTask;
import pro.sky.telegrambot.service.NotificationTaskService;

import javax.annotation.PostConstruct;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.time.LocalDateTime.parse;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {
    private NotificationTaskService notificationTaskService;
    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private final Pattern pattern = Pattern
            .compile("(\\d{1,2}\\.\\d{1,2}\\.\\d{4} \\d{1,2}:\\d{2})\\s+([А-я\\d\\s.,!?:]+)");

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public TelegramBotUpdatesListener(NotificationTaskService notificationTaskService, TelegramBot telegramBot) {
        this.notificationTaskService = notificationTaskService;
        this.telegramBot = telegramBot;
    }

    @Autowired
    private TelegramBot telegramBot;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        try {
            updates.forEach(update -> {
                logger.info("Processing update: {}", update);

                Message message = update.message();
                Long chatId = message.from().id();
                String text = message.text();

                if ("/start".equals(text)) {
                    sendMessage(chatId, "Привет!\n" +
                            "Я помогу тебе запланировать задачу.\n" +
                            "Отправь ее в формате: \n" +
                            "20.08.2023 12:32 ");
                } else
                    if (text != null)
                {
                    Matcher matcher = pattern.matcher(text);

                    if (matcher.find())
                    {
                        LocalDateTime dateTime = parse(matcher.group(1));

                        if (Objects.isNull(dateTime)) {
                            sendMessage(chatId, "Некорректный формат даты и/или времени!");
                        } else {
                            String txt = matcher.group(2);
                            NotificationTask notificationTask = new NotificationTask();
                            notificationTask.setChatId(chatId);
                            notificationTask.setMessage(txt);
                            notificationTask.setDateTime(dateTime);
                            notificationTaskService.save(notificationTask);
                            sendMessage(chatId, "Задача успешно запланирована!");
                        }
                    } else {
                        sendMessage(chatId, "Некорректный формат сообщения!");
                    }
                }

            });
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    @Nullable
    private LocalDateTime parse(String dateTime) {

        try {
            return LocalDateTime.parse(dateTime, dateTimeFormatter);
        } catch (DateTimeException e) {
            return null;
        }
    }

    private void sendMessage(long chatId, String message) {
        SendMessage sendMessage = new SendMessage(chatId, message);
        SendResponse sendResponse = telegramBot.execute(sendMessage);
        if (!sendResponse.isOk()) {
            logger.error("Error during sending message: {}", sendResponse.description());
        }
    }

}
