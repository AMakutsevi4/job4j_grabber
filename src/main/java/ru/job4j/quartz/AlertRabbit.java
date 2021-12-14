package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit {
    static Connection connection;

    private static Properties readProperties() {
        Properties config = new Properties();
         try (FileInputStream in = new FileInputStream("./src/main/resources/rabbit.properties")) {
            config.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return config;
    }

    private static Connection initConnection(Properties config) throws ClassNotFoundException, SQLException {
        Class.forName(config.getProperty("driver"));
        connection = DriverManager.getConnection(readProperties().getProperty("url"),
                readProperties().getProperty("username"),
                readProperties().getProperty("password"));
        return connection;
    }

    public static void main(String[] args) {
        Properties config = readProperties();
          try (Connection cn = initConnection(config)) {
            List<Long> store = new ArrayList<>();
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDataMap data = new JobDataMap();
            data.put("store", cn);
            JobDetail job = newJob(Rabbit.class)
                    .usingJobData(data)
                    .build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(Integer.parseInt(readProperties().getProperty("interval")))
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
            Thread.sleep(10000);
            scheduler.shutdown();
            System.out.println(store);
        } catch (Exception se) {
            se.printStackTrace();
        }
    }

    public static class Rabbit implements Job {

        public Rabbit() {
            System.out.println(hashCode());
        }

        @Override
        public void execute(JobExecutionContext context) {
            System.out.println("Rabbit runs here ...");
            Connection connection = (Connection) context.getJobDetail().getJobDataMap().get("store");
            try (PreparedStatement statement = connection.prepareStatement("insert into rabbit(create_date) values (?)")) {
                LocalDateTime dateTime = LocalDateTime.now()
                        .withHour(19).withMinute(0).withSecond(0).withNano(0);
                statement.setString(1, String.valueOf(Timestamp.valueOf(dateTime)));
                statement.execute();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
