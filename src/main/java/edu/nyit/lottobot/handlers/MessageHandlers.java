package edu.nyit.lottobot.handlers;

import edu.nyit.lottobot.Main;
import edu.nyit.lottobot.data_classes.LotteryType;
import edu.nyit.lottobot.data_classes.RaffleLottery;
import edu.nyit.lottobot.managers.DataManager;
import edu.nyit.lottobot.timer_tasks.SelfDestructTask;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class MessageHandlers extends ListenerAdapter {

    private final Main main;

    public MessageHandlers(Main main) {
        this.main = main;
    }

    // No use
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        //Determines if message is in the proper channel.
        if (main.getDataManager().isBotChannel(event.getGuild().getIdLong(), event.getChannel().getIdLong()) && !event.getAuthor().isBot()) {
            HashMap<Long,String> addingTickets = main.getDataManager().getAddingTickets();
            if(addingTickets.containsKey(event.getAuthor().getIdLong())){
                long amount = 0L;
                try{
                    amount = Long.parseLong(event.getMessage().getContentRaw());
                }catch (NumberFormatException exception){
                    pingSelfDestructMessage(event.getAuthor().getIdLong(), event.getChannel().getIdLong(), 5, "Incorrect format please try again.");
                    return;
                }
                if(main.getDataManager().getAccount(event.getAuthor().getIdLong()).getBalance() < amount){
                    pingSelfDestructMessage(event.getAuthor().getIdLong(), event.getChannel().getIdLong(), 5, "You do not have enough tickets for this!");
                    return;
                }
                if(main.getDataManager().getGame(addingTickets.get(event.getAuthor().getIdLong())).getLotteryType().equals(LotteryType.RAFFLE)){
                    main.getDataManager().getGame(addingTickets.get(event.getAuthor().getIdLong())).addParticipant(event.getAuthor().getIdLong(), amount);
                }
                addingTickets.remove(event.getAuthor().getIdLong());
            }
            event.getMessage().delete().queue();
        }
    }

    /**
     * Sends a message that pings the user and also deletes itself.
     *
     * @param userID User to ping
     * @param channelID Channel to send the message in
     * @param time Time in seconds until message deletion
     * @param message Actual message to send
     */
    public void pingSelfDestructMessage(long userID, long channelID, long time, String message){
        Timer t = new Timer();
        var wrapper = new Object() {
            TimerTask task;
        };
        main.getJda().getTextChannelById(channelID).sendMessage("<@" + userID + ">" + ": " + message).queue(sentMessage -> {
            wrapper.task = new SelfDestructTask(time, channelID, sentMessage.getIdLong(), main.getJda());
            t.schedule(wrapper.task, 0, 1000);
        });
    }
}


