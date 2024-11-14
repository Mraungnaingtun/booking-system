package com.logant.BookingSystem.Quartz;

import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail refundCreditsJobDetail() {
        return JobBuilder.newJob(RefundCreditsJob.class)
                .withIdentity("refundCreditsJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger refundCreditsJobTrigger() {
        return TriggerBuilder.newTrigger()
                .forJob(refundCreditsJobDetail())
                .withIdentity("refundCreditsTrigger")
                .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(6, 0))
                .build();
    }
}
