package edu.nyit.lottobot.handlers;

import edu.nyit.lottobot.Main;
import edu.nyit.lottobot.timer_tasks.SelfDestructTask;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;

public class MessageHandlers extends ListenerAdapter {

    private final Main main;

    public MessageHandlers(Main main) {
        this.main = main;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        //Determines if message is in the proper channel.
        if (main.getDataManager().isBotChannel(event.getGuild().getIdLong(), event.getChannel().getIdLong())) {
            super.onMessageReceived(event);
        }
    }

    public void replySelfDestructMessage(long userID, long channelID, long time, String message) throws InterruptedException {
        Timer t = new Timer();
        var wrapper = new Object(){TimerTask task;};
        main.getJda().getTextChannelById(channelID).sendMessage("<@" + userID + ">" + ": " + message).queue(sentMessage -> {
            wrapper.task = new SelfDestructTask(time, channelID, sentMessage.getIdLong(), main.getJda());
            t.schedule(wrapper.task, 0, 1000);
        });

    }
}


