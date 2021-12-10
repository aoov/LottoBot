package edu.nyit.lottobot.data_classes;

import com.google.firebase.database.DatabaseReference;
import edu.nyit.lottobot.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;

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
    private Timer t;

    /**
     * Default Constructor required by Firebase
     * Values for final variables
     *
     * @param jda          JDA object
     * @param botChannelID specific bot-channel
     * @param guildID      ID of the server/guild
     * @param allowedRoles List of allowed roles
     * @param startedBy    Who started the lottery
     * @param main         Instance of the Main class
     */
    public RaffleLottery(JDA jda, long botChannelID, long guildID, long[] allowedRoles, long startedBy, Main main) {
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
        this.lotteryType = LotteryType.RAFFLE;
        this.t = new Timer();
    }

    /**
     * Common constructor
     *
     * @param guildID      Guild/Server ID
     * @param botChannelID Channel ID of LottoBot
     * @param startedBy    ID of who started the Lottery
     * @param prizePool    Prize pool
     * @param time         Time for the lottery to run in seconds
     * @param allowedRoles Roles allowed to join this lottery
     * @param main         instance of Main
     */
    public RaffleLottery(long guildID, long botChannelID, long startedBy, long prizePool, long time, @Nullable long[] allowedRoles, Main main, boolean generateKey) {
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
        this.t = new Timer();
        if (allowedRoles != null) {
            for (long l : allowedRoles) {
                this.allowedRoles.add(l);
            }
        }
        if(generateKey){
            generateUniqueKey();
        }
    }

    /**
     * Common constructor with ArrayList roles
     *
     * @param guildID      Guild/Server ID
     * @param botChannelID Channel ID of LottoBot
     * @param startedBy    ID of who started the Lottery
     * @param prizePool    Prize pool
     * @param time         Time for the lottery to run in seconds
     * @param allowedRoles Roles allowed to join this lottery
     * @param main         instance of Main
     */
    public RaffleLottery(long guildID, long botChannelID, long startedBy, long prizePool, long time, @Nullable ArrayList<Long> allowedRoles, Main main, boolean generateKey) {
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
        this.t = new Timer();
        if(generateKey){
            generateUniqueKey();
        }
    }

    /**
     * Generates a Unique Key from Firebase and saves the lottery object under it.
     */
    public void generateUniqueKey() {
        if (uniqueKey != null && !uniqueKey.isEmpty()) {
            System.out.println("Key already generated");
            return;
        }
        DatabaseReference lotRef = main.getDataManager().getFirebaseDatabase().getReference().child("data").child("lotteries").child("raffles");
        DatabaseReference pushRef = lotRef.push();
        uniqueKey = pushRef.getKey();
        main.getDataManager().saveRaffleToDatabase(this);
    }


    /**
     * Prints the LotteryMessage of the bot
     *
     * @see EmbedBuilder
     * @see net.dv8tion.jda.api.entities.MessageEmbed
     * @see MessageBuilder
     * @see net.dv8tion.jda.api.interactions.components.Component
     * @see Button
     * @see ActionRow
     */
    public void print() {
        if (user == null) {
            jda.retrieveUserById(startedBy).queue(usr -> {
                user = usr;
            });
        }
        while (user == null) {
            Thread.onSpinWait();
        }
        //Above code retrieves user who started if not saved. Probably redundant.

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("Current Payout: **__" + prizePool + " tickets__**")
                .setAuthor("Started by " + user.getName(), null, user.getAvatarUrl())
                .setFooter("ID: " + uniqueKey)
                .addField("Time left:", timeLeft + "", true)
                .addField("Lottery Type", lotteryType.toString(), true)
                .setImage("https://media2.giphy.com/media/Ps8XflhsT5EVa/giphy.gif");
        if (allowedRoles == null || allowedRoles.isEmpty()) {
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
        //Above code creates the Embed message see

        MessageBuilder mb = new MessageBuilder();
        mb.setEmbeds(embedBuilder.build());
        mb.setActionRows(ActionRow.of(
                Button.of(ButtonStyle.SUCCESS, "enterTickets", "Enter Tickets")
        ));
        //Above code creates the message to send.

        if (messageID == 0) {
            jda.getTextChannelById(botChannelID).sendMessage(mb.build()).queue(message -> {
                this.messageID = message.getIdLong();
            });
        } else {
            jda.getTextChannelById(botChannelID).editMessageById(messageID, mb.build()).queue();
        }
        //Above code either sends the first message or edits the existing message for the specific lottery
    }

    /**
     * Starts the timer for the lottery
     *
     * @see Timed
     */
    public void start() {
        this.t = super.start(this, t);
    }
    public void stop(){
        super.stop(t);
    }

    /**
     * Finishes the lottery and calls chooseWinner
     * Edits the message the final message and should initialize payout by PaymentManager
     */
    public void finish() {
        chooseWinner();
        MessageBuilder mb = new MessageBuilder();
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("**Lottery Ended! Final Payout:** __" + prizePool + " Tickets__")
                .setAuthor("Started by " + user.getName(), null, user.getAvatarUrl())
                .setFooter("ID: " + uniqueKey)
                .setDescription("**:partying_face: Lottery over! The winner was: <@" + winner + "> :partying_face:**")
                .setImage("https://c.tenor.com/KgIC_rUjd08AAAAC/scrooge-donald-duck.gif");
        mb.setEmbeds(eb.build());
        jda.getTextChannelById(botChannelID).editMessageById(messageID, mb.build()).queue();
    }

    /**
     * Chooses the winner for the lottery
     * Places userIDs in arraylist for quantity of tickets and chooses at random.
     */
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

    @Override
    public void addParticipant(long user, Object object) {
        long amount = (Long) object;
        if(participants.containsKey(user)){
            participants.put(user+"", participants.get(user) + amount);
        }else{
            participants.put(user + "", amount);
        }
        main.getDataManager().getAccount(user).setBalance(main.getDataManager().getAccount(user).getBalance()-amount);
        prizePool += amount;
    }

    /**
     * Saves this RaffleLottery to the database
     */
    public void save() {
        main.getDataManager().saveRaffleToDatabase(this);
    }

    /*
    Getters and setters
     */
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

    public long getWinner() {
        return winner;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    public void setWinner(long winner) {
        this.winner = winner;
    }
}
