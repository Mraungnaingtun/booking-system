package com.logant.BookingSystem.Quartz;

import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.logant.BookingSystem.Entity.ClassSchedule;
import com.logant.BookingSystem.Entity.Waitlist;
import com.logant.BookingSystem.Service.BookingService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RefundCreditsJob implements Job {

    @Autowired
    private BookingService bookingService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("Quartz job started: refunding credits for waitlisted users after class ends.");

        List<ClassSchedule> endedClasses = bookingService.getEndedClasses();

        if (endedClasses.isEmpty()) {
            log.info("No ended classes found. No credits to refund.");
            return;
        }

        for (ClassSchedule endedClass : endedClasses) {
            List<Waitlist> waitlistUsers = bookingService.getWaitlist(endedClass);
            for (Waitlist waitlistUser : waitlistUsers) {
                try {
                    bookingService.refundCreditsToWaitlistUser(waitlistUser, endedClass);
                    log.info("Refunded credits to waitlist user: {}", waitlistUser.getUser().getUserName());
                } catch (Exception e) {
                    log.error("Error refunding credits for user: {}", waitlistUser.getUser().getUserName(), e);
                }
            }
        }

        log.info("Quartz job completed: credits refunded to all eligible waitlisted users.");
    }
}
