package net.envirocraft;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.envirocraft.commands.Command;
import net.envirocraft.utils.Config;
import net.envirocraft.utils.UpdateJob;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.net.URISyntaxException;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;


public class Bot {

    public static JDA jda = null;
    public static String localPath = null;

    public static void main(String[] args) throws LoginException, URISyntaxException, InterruptedException, SchedulerException {

        long startTime = System.currentTimeMillis();

        System.out.println("Starting Bot...");

        File jarFile = new File(Bot.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
        localPath = jarFile.getParent() + File.separator;

        Config.load();

        jda = JDABuilder.createDefault(Config.get("token").getAsString())
                .addEventListeners(new Command())
                .build();

        jda.awaitReady();

        System.out.println("Finished Loading! (" + ((float)(System.currentTimeMillis() - startTime)) / 1000 + " sec)");

        SchedulerFactory sf = new StdSchedulerFactory();
        Scheduler sched = sf.getScheduler();

        JobDetail job = newJob(UpdateJob.class)
                .withIdentity("job1", "group1")
                .build();

        CronTrigger trigger = newTrigger()
                .withIdentity("trigger1", "group1")
                .withSchedule(cronSchedule("0 20 7 * * ?"))
                .build();

        sched.scheduleJob(job, trigger);
        sched.start();
    }
}
