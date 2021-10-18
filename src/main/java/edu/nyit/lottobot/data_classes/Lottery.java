package edu.nyit.lottobot.data_classes;

import edu.nyit.lottobot.Main;
import edu.nyit.lottobot.handlers.TimerRunnable;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Period;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Lottery {

    private long messageID;
    private final JDA jda;
    private final long botChannelID;
    private final long guildID;
    private final long startedBy;
    private final Main main;
    private final LotteryType lotteryType;
    private final DateTime startDate;
    private long timeLeft;
    private boolean active;
    private long prizePool;
    private Timer timer;



    public Lottery(long guildID, long botChannelID, long startedBy, LotteryType lotteryType, long prizePool, long time, Main main) {
        this.jda = main.getJda();
        this.guildID = guildID;
        this.main = main;
        this.active = true;
        this.startedBy = startedBy;
        this.lotteryType = lotteryType;
        this.prizePool = prizePool;
        this.botChannelID = botChannelID;
        this.startDate = new DateTime();
        timer = new Timer();
        this.timeLeft = time;
    }

    public void PrintLottery() {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("__Current Payout__: " + prizePool + " tickets")
                .setAuthor("Started by Aaron")
                .setFooter("Use /enter to enter tickets!")
                .addField("Time left:", timeLeft + "",false)
                .setImage("https://media2.giphy.com/media/Ps8XflhsT5EVa/giphy.gif");

        if (messageID == 0) {
            jda.getTextChannelById(botChannelID).sendMessageEmbeds(embedBuilder.build()).queue(message -> {
                this.messageID = message.getIdLong();
            });
        } else {
            jda.getTextChannelById(botChannelID).editMessageEmbedsById(messageID, embedBuilder.build()).queue();
        }
    }

    public void startTimer(){
        TimerTask t = new TimerRunnable(this);
        timer.schedule(t, 0, 1000);
    }

    public void setMessageID(long messageID) {
        this.messageID = messageID;
    }

    public long getMessageID() {
        return messageID;
    }

    public JDA getJda() {
        return jda;
    }

    public long getBotChannelID() {
        return botChannelID;
    }

    public long getGuildID() {
        return guildID;
    }

    public long getStartedBy() {
        return startedBy;
    }

    public Main getMain() {
        return main;
    }

    public LotteryType getLotteryType() {
        return lotteryType;
    }

    public DateTime getStartDate() {
        return startDate;
    }

    public long getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(long timeLeft) {
        this.timeLeft = timeLeft;
    }

    public Timer getTimer() {
        return timer;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public long getPrizePool() {
        return prizePool;
    }

    public void setPrizePool(long prizePool) {
        this.prizePool = prizePool;
    }
}
