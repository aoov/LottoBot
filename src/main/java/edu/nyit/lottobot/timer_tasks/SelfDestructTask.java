package edu.nyit.lottobot.timer_tasks;

import edu.nyit.lottobot.Main;
import net.dv8tion.jda.api.JDA;

import java.util.TimerTask;

/**
 * Class used to track time for a message that deletes itself.
 */
public class SelfDestructTask extends TimerTask {
    private long time;
    private final long channelID;
    private final long messageID;
    private final JDA jda;


    public SelfDestructTask(long time, long channelID, long messageID, JDA jda) {
        this.time = time;
        this.channelID = channelID;
        this.jda = jda;
        this.messageID = messageID;
    }

    @Override
    public void run() {
        if (time > 0) {
            time--;
        } else {
            jda.getTextChannelById(channelID).retrieveMessageById(messageID).queue(retrieved -> {
                retrieved.delete().queue();
                this.cancel();
            });
        }
    }
}
