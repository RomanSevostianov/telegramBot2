package pro.sky.telegrambot.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity // указывает Hibernate,что таблицу нужно хранить в базе данных
@Data
@EqualsAndHashCode
@NoArgsConstructor// конструктор без параметров
@AllArgsConstructor//консктруктор, использующий все поля класса
@Table(name = "Informing_task") //задаем имя таблицы в БД
public class NotificationTask {

    //индефикатор напоминаний
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    //задача которую нужно запланировать
    @Column(name = "message", nullable = false)
    private String message;

    //индификатор пользователя
    @Column(name = "chat_id", nullable = false)
    private long chatId;

    //дата и время планируемой задачи
    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime;


}
