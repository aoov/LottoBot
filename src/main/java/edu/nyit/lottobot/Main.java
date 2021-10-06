package edu.nyit.lottobot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;

public class Main extends ListenerAdapter {

    public static void main(String[] args) throws LoginException, InterruptedException {
        System.out.println("Starting up bot");
        JDA jda = JDABuilder.createDefault("[Insert Token]")
                .setActivity(Activity.watching("the odds"))
                .addEventListeners(new Main())
                .build();
        // optionally block until JDA is ready
        jda.awaitReady();

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
