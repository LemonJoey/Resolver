package net.envirocraft.commands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.envirocraft.Bot;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

public class Command extends ListenerAdapter {

    public static JsonArray MESSAGES = new JsonArray();
    public static File messageFile = new File(Bot.localPath+File.separator+"messagefile.json");

    public Command(){
        try {
            JsonArray loadMessage = new Gson().fromJson(
                    new String(
                            new FileInputStream(messageFile).readAllBytes()
                    ),
                    JsonArray.class
            );
            if(loadMessage!=null){
                MESSAGES = loadMessage;
            }
        } catch (IOException ignored) {}
    }

    public static Message getMessage(){
        URL whatismyip = null;
        try {
            whatismyip = new URL("http://checkip.amazonaws.com");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        BufferedReader in = null;
        try {
            assert whatismyip != null;
            in = new BufferedReader(new InputStreamReader(
                    whatismyip.openStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String ip = null; //you get the IP as a String
        try {
            assert in != null;
            ip = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(ip);
        MessageBuilder mb = new MessageBuilder();
        File file = new File("logo.png");
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(new Color(27, 87, 135));
        eb.setThumbnail("https://cdn.cloudflare.steamstatic.com/steam/apps/1604030/header.jpg?t=1655371713");
        eb.setTitle("vrising.envirocraft.net");
        eb.addField("IP+Port:","%s:27031".formatted(ip),false);
        eb.addField("Password:","macrodeeznuts",false);
        eb.setTimestamp(Instant.now());
        mb.setEmbeds(eb.build());
        mb.setActionRows(ActionRow.of(Button.secondary("1","Refresh")));
        return mb.build();
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        Message message = event.getMessage();
        MessageChannel channel = event.getChannel();
        String msg = message.getContentDisplay();
        if (msg.equals("!resolve")) {
            Message sentmessage = channel.sendMessage(getMessage()).complete();
            JsonObject messageData = new JsonObject();
            messageData.addProperty("guild",sentmessage.getGuild().getId());
            messageData.addProperty("guildchannel",sentmessage.getGuildChannel().getId());
            messageData.addProperty("messageid",sentmessage.getId());
            MESSAGES.add(messageData);
            save();
        }
    }

    public void save(){
        try {
            messageFile.createNewFile();
            new FileOutputStream(messageFile).write(
                    new GsonBuilder()
                            .setPrettyPrinting()
                            .create()
                            .toJson(MESSAGES)
                            .getBytes(StandardCharsets.UTF_8)
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("file saved");
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getComponentId().equals("1")) {
            event.getMessage().editMessage(getMessage()).complete();
            event.deferEdit().queue();
        }
    }
}





