package edu.nyit.lottobot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.util.Scanner;

public class Main extends ListenerAdapter {

    public static void main(String[] args) throws LoginException, InterruptedException {
        JDA jda = JDABuilder.createDefault("")
                .setActivity(Activity.watching("the odds"))
                .addEventListeners(new Main())
                .build();
        // optionally block until JDA is ready
        jda.awaitReady();
        createChannel(jda);
        String input = "";
        Scanner inputScanner = new Scanner(System.in);
        while(!input.equalsIgnoreCase("exit")){
            input = inputScanner.next();
        }
        jda.shutdown();


    }
    //Creates bot channel if not found
    public static void createChannel(JDA jda){
        //Iterates through all the servers with the bot
        for(Guild guild : jda.getGuilds()){
            //Looks for designated bot channel
            boolean found = false;
            for(TextChannel textChannel : guild.getTextChannels()){
                if(textChannel.getName().equals("lotto-bot")){
                    found = true;
                }
            }
            //If not found creates one.
            if(!found){
                guild.createTextChannel("lotto-bot");
            }


        }
    }


    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        Message msg = event.getMessage();
        //DM Test
        if (msg.getContentRaw().equals("!ping")) {
            event.getAuthor().openPrivateChannel().queue(privateChannel ->
            {
                privateChannel.sendMessage("DM Test").queue();
            });
            //Ping pong
            MessageChannel channel = event.getChannel();
            long time = System.currentTimeMillis();
            channel.sendMessage("Pong!") /* => RestAction<Message> */
                    .queue(response /* => Message */ -> {
                        response.editMessageFormat("Pong: %d ms", System.currentTimeMillis() - time).queue();
                    });
            super.onMessageReceived(event);
        }
    }
}
