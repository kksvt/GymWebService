package uj.wmii.jwzp.cron;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uj.wmii.jwzp.gymclass.GymClassService;

import java.time.LocalDate;

@Component
public class GymClassInstanceCreatorScheduler {

    private static final Logger gymClassInstanceCreatorSchedulerLogger = LoggerFactory.getLogger(GymClassInstanceCreatorScheduler.class);

    private final GymClassService gymClassService;

    @Autowired
    public GymClassInstanceCreatorScheduler(GymClassService gymClassService){
        this.gymClassService = gymClassService;
    }

    //runs every sunday at 11:59 pm
    @Scheduled(cron = "0 59 23 * * SUN")
    public void checkMembershipExpiration(){
        LocalDate currentDate = LocalDate.now();
        gymClassInstanceCreatorSchedulerLogger.info("Started creating instances for all gym classes on: {}", currentDate);
        gymClassService.createGymClassInstancesForCurrentWeek();
        gymClassInstanceCreatorSchedulerLogger.info("Gym class instances for this week have been created.");
    }
}
