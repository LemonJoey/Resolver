package net.envirocraft.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.envirocraft.Bot;
import net.envirocraft.commands.Command;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;

import java.util.Date;
import java.util.Objects;

public class UpdateJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobKey jobKey = context.getJobDetail().getKey();
        System.out.println("running job");
        for(JsonElement messageData: Command.MESSAGES) {
            try{
                Guild guild = Bot.jda.getGuildById(messageData.getAsJsonObject().get("guild").getAsString());
                System.out.println(guild.getId());
                TextChannel textChannel = guild.getTextChannelById(messageData.getAsJsonObject().get("guildchannel").getAsString());
                System.out.println(textChannel.getId());
                Message message = textChannel.retrieveMessageById(messageData.getAsJsonObject().get("messageid").getAsString()).complete();
                System.out.println(message.getId());
                assert message != null;
                message.editMessage(Command.getMessage()).queue();
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }
    }
}
