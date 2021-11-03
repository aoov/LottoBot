package edu.nyit.lottobot.data_classes;

import com.google.firebase.database.DatabaseReference;
import edu.nyit.lottobot.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;
import org.joda.time.DateTime;

import javax.annotation.Nullable;
import java.util.*;

public class RaffleLottery extends Timed implements Game {

    private String uniqueKey;
    private long messageID;
    private final JDA jda;
    private final long botChannelID;
    private final long guildID;
    private ArrayList<Long> allowedRoles;
    private final long startedBy;
    private final Main main;
    private final LotteryType lotteryType;
    private long timeLeft;
    private boolean active;
    private long prizePool;
    private HashMap<String, Long> participants;
    private volatile User user;
    private long winner;

    public RaffleLottery(JDA jda, long botChannelID, long guildID, long[] allowedRoles, long startedBy, Main main, LotteryType lotteryType) {
        this.jda = jda;
        this.botChannelID = botChannelID;
        this.guildID = guildID;
        this.allowedRoles = new ArrayList<>();
        if (allowedRoles != null) {
            for (long l : allowedRoles) {
                this.allowedRoles.add(l);
            }
        }
        this.startedBy = startedBy;
        this.main = main;
        this.lotteryType = lotteryType;
    }


    public RaffleLottery(long guildID, long botChannelID, long startedBy, long prizePool, long time, @Nullable long[] allowedRoles, Main main) {
        this.jda = main.getJda();
        this.guildID = guildID;
        this.main = main;
        this.active = true;
        this.startedBy = startedBy;
        this.lotteryType = LotteryType.RAFFLE;
        this.prizePool = prizePool;
        this.botChannelID = botChannelID;
        this.timeLeft = time;
        this.participants = new HashMap<>();
        this.allowedRoles = new ArrayList<>();
        if (allowedRoles != null) {
            for (long l : allowedRoles) {
                this.allowedRoles.add(l);
            }
        }
        generateUniqueKey();
    }

    public RaffleLottery(long guildID, long botChannelID, long startedBy, long prizePool, long time, @Nullable ArrayList<Long> allowedRoles, Main main) {
        this.jda = main.getJda();
        this.guildID = guildID;
        this.main = main;
        this.active = true;
        this.startedBy = startedBy;
        this.lotteryType = LotteryType.RAFFLE;
        this.prizePool = prizePool;
        this.botChannelID = botChannelID;
        this.timeLeft = time;
        this.participants = new HashMap<>();
        this.allowedRoles = new ArrayList<>();
        if (allowedRoles != null) {
            this.allowedRoles = allowedRoles;
        }

    }
    public void generateUniqueKey() {
        if (uniqueKey != null && !uniqueKey.isEmpty()) {
            System.out.println("Key already generated");
            return;
        }
        DatabaseReference lotRef = main.getDataManager().getFirebaseDatabase().getReference().child("data").child("lotteries").child("raffles");
        DatabaseReference pushRef = lotRef.push();
        uniqueKey = pushRef.getKey();
    }

    public void print() {
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
                .setFooter("ID: " + uniqueKey)
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
        MessageBuilder mb = new MessageBuilder();
        mb.setEmbeds(embedBuilder.build());
        mb.setActionRows(ActionRow.of(
                Button.of(ButtonStyle.SUCCESS, "enterTickets","Enter Tickets")
        ));

        //Send new message or edit
        if (messageID == 0) {
            jda.getTextChannelById(botChannelID).sendMessage(mb.build()).queue(message -> {
                this.messageID = message.getIdLong();
            });
        } else {
            jda.getTextChannelById(botChannelID).editMessageById(messageID, mb.build()).queue();
        }
    }

    public void start() {
        super.start(this);
    }

    public void finish() {
        chooseWinner();
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("**Lottery Ended! Final Payout:** __" + prizePool + " Tickets__")
                .setAuthor("Started by " + user.getName(), null, user.getAvatarUrl())
                .setFooter("ID: " + uniqueKey)
                .setDescription("**:partying_face: Lottery over! The winner was: <@" + winner + "> :partying_face:**")
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

    public void save() {
        main.getDataManager().saveRaffle(this);
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

    public String getUniqueKey() {
        return uniqueKey;
    }

    public ArrayList<Long> getAllowedRoles() {
        return allowedRoles;
    }

    public User getUser() {
        return user;
    }

    public long getWinner() {
        return winner;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setWinner(long winner) {
        this.winner = winner;
    }
}
