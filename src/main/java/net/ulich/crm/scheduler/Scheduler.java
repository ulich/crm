package net.ulich.crm.scheduler;

import io.jmix.core.DataManager;
import io.jmix.core.FetchPlan;
import io.jmix.core.security.SystemAuthenticator;
import net.ulich.crm.entity.ScheduledEmail;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.concurrent.TimeUnit;

@Component
public class Scheduler {
    private final DataManager dataManager;
    private final SystemAuthenticator systemAuthenticator;
    private final LeadEmailService leadEmailService;

    public Scheduler(DataManager dataManager, SystemAuthenticator systemAuthenticator, LeadEmailService leadEmailService) {
        this.dataManager = dataManager;
        this.systemAuthenticator = systemAuthenticator;
        this.leadEmailService = leadEmailService;
    }

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.MINUTES)
    public void schedule() {
        systemAuthenticator.withSystem(() -> {
            start();
            return null;
        });
        leadEmailService.processQueuedEmails();
    }

    private void start() {
        var emailsToBeSent = dataManager.load(ScheduledEmail.class)
                .query("select s from ScheduledEmail s where s.sentDate is null and s.plannedSendDate < :now")
                .fetchPlan(fetchPlanBuilder -> fetchPlanBuilder.addFetchPlan(FetchPlan.BASE)
                        .add("lead", FetchPlan.BASE)
                        .add("emailTemplate", FetchPlan.BASE)
                        .add("emailTemplate.attachments", FetchPlan.BASE))
                .parameter("now", OffsetDateTime.now())
                .maxResults(1)
                .list();

        emailsToBeSent.forEach(this::handleEmail);
    }

    private void handleEmail(ScheduledEmail email) {
        leadEmailService.sendEmailToLead(email.getEmailTemplate(), email.getLead());

        if (email.getRecurringEmail() != null) {
            var newScheduledEmail = leadEmailService.createRecurringEmail(email.getLead(), email.getRecurringEmail(), LocalDate.now());
            dataManager.save(newScheduledEmail);
        }

        email.setSentDate(OffsetDateTime.now());
        dataManager.save(email);
    }
}
