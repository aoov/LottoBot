package edu.nyit.lottobot.data_classes;

import edu.nyit.lottobot.Main;
import edu.nyit.lottobot.timer_tasks.TimerRunnable;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.internal.requests.Route;
import org.joda.time.DateTime;

import javax.annotation.Nullable;
import java.util.*;

public class Lottery {

    private long messageID;
    private final String lotteryName;
    private final JDA jda;
    private final long botChannelID;
    private final long guildID;
    private final long[] allowedRoles;
    private final long startedBy;
    private final Main main;
    private final LotteryType lotteryType;
    private final DateTime startDate;
    private long timeLeft;
    private boolean active;
    private long prizePool;
    private HashMap<String, Long> participants;
    private volatile User user;
    private long winner;

    public Lottery(long guildID, long botChannelID, long startedBy, LotteryType lotteryType, long prizePool, long time, @Nullable long[] allowedRoles, String name, Main main) {
        this.jda = main.getJda();
        this.guildID = guildID;
        this.main = main;
        this.active = true;
        this.startedBy = startedBy;
        this.lotteryType = lotteryType;
        this.prizePool = prizePool;
        this.botChannelID = botChannelID;
        this.startDate = new DateTime();
        this.timeLeft = time;
        this.participants = new HashMap<>();
        this.lotteryName = name;
        this.allowedRoles = allowedRoles;
    }

    public void PrintLottery() {
        if (user == null) {
            jda.retrieveUserById(startedBy).queue(usr -> {
                user = usr;
            });
        }
        while (user == null) {
            Thread.onSpinWait();
        }
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("Current Payout: **__" + prizePool + " tickets__**")
                .setAuthor("Started by " + user.getName(), null, user.getAvatarUrl())
                .setFooter("Use \"/enter  " + lotteryName + "\" to enter tickets!")
                .addField("Time left:", timeLeft + "", true)
                .addField("Lottery Type", lotteryType.toString(), true)
                .setImage("https://media2.giphy.com/media/Ps8XflhsT5EVa/giphy.gif");
        if (allowedRoles == null) {
            embedBuilder.setDescription("Allowed Roles: @everyone");
        } else {
            StringBuilder sb = new StringBuilder();
            for (Long l : allowedRoles) {
                sb.append("<@&").append(l).append(">, ");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.deleteCharAt(sb.length() - 1);
            embedBuilder.setDescription("Allowed Roles: " + sb);
        }
        if (messageID == 0) {
            jda.getTextChannelById(botChannelID).sendMessageEmbeds(embedBuilder.build()).queue(message -> {
                this.messageID = message.getIdLong();
            });
        } else {
            jda.getTextChannelById(botChannelID).editMessageEmbedsById(messageID, embedBuilder.build()).queue();
        }
    }

    public void startTimer() {
        Timer timer = new Timer();
        TimerTask t = new TimerRunnable(this);
        timer.schedule(t, 0, 1000);
    }

    public void finishLottery() {
        chooseWinner();
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("**Lottery Ended! Final Payout:** __" + prizePool + " Tickets__")
                .setAuthor("Started by " + user.getName(), null, user.getAvatarUrl())
                .setDescription("**:partying_face: Lottery over! The winner was: <@"+ winner + "> :partying_face:**")
                .setImage("https://c.tenor.com/KgIC_rUjd08AAAAC/scrooge-donald-duck.gif");
        jda.getTextChannelById(botChannelID).editMessageEmbedsById(messageID, eb.build()).queue();
    }

    public void chooseWinner() {
        ArrayList<Long> participantsPool = new ArrayList<>();
        for (String l : participants.keySet()) {
            long amount = participants.get(l);
            for (int i = 0; i < amount; i++) {
                participantsPool.add(Long.parseLong(l));
            }
            Random random = new Random();
            int chosen = random.nextInt(participantsPool.size());
            winner = participantsPool.get(chosen);
        }
    }

    public void addTickets(Long id, long tickets) {
        if (participants.containsKey(id+"")) {
            participants.put(id+"", participants.get(id+"") + tickets);
        }else{
            participants.put(id+"", tickets);
        }
        prizePool += tickets;
    }

    public boolean removeTickets(long id, long tickets) {
        if (participants.containsKey(id+"") && participants.get(id+"") < tickets) {
            return false;
        } else {
            participants.put(id+"", participants.get(id+"") - tickets);
            return true;
        }
    }

    public long getTickets(Long id) {
        if (participants.containsKey(id+"")) {
            return participants.get(id+"");
        } else {
            return 0;
        }
    }

    public HashMap<String, Long> getParticipants() {
        return participants;
    }

    public void setParticipants(HashMap<String, Long> participants) {
        this.participants = participants;
    }

    public void setMessageID(long messageID) {
        this.messageID = messageID;
    }

    public long getMessageID() {
        return messageID;
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
