package pro.sky.telegrambot.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import pro.sky.telegrambot.entity.NotificationTask;

//findAllNotificationByCurrentDate
import java.time.LocalDateTime;
import java.util.List;


public interface NotificationTaskRepository extends JpaRepository <NotificationTask,Long> {
 @Query(value = "SELECT * from notification_task where notification_time = date_trunc('second', CURRENT_TIMESTAMP)", nativeQuery = true)
 List<NotificationTask> findAllNotificationByCurrentDate ();

}
