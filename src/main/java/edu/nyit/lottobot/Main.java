package edu.nyit.lottobot;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import edu.nyit.lottobot.data_classes.Account;
import edu.nyit.lottobot.data_classes.RaffleLottery;
import edu.nyit.lottobot.data_classes.LotteryType;
import edu.nyit.lottobot.handlers.ButtonListeners;
import edu.nyit.lottobot.handlers.MessageHandlers;
import edu.nyit.lottobot.handlers.SlashCommandListeners;
import edu.nyit.lottobot.managers.DataManager;
import edu.nyit.lottobot.managers.LotteryManager;
import edu.nyit.lottobot.managers.PaymentManager;


import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;


import javax.security.auth.login.LoginException;
import java.util.Scanner;


public class Main extends ListenerAdapter {

    private DataManager dataManager;
    private LotteryManager lotteryManager;
    private PaymentManager paymentManager;
    private JDA jda;
    private MessageHandlers messageHandler;

    public Main(JDA jda) {
        this.jda = jda;
        dataManager = new DataManager(this);
            lotteryManager = new LotteryManager(this);
        paymentManager = new PaymentManager();
        messageHandler = new MessageHandlers(this);
    }


    public static void main(String[] args) throws LoginException, InterruptedException {
        JDA jda = JDABuilder.createDefault("")
                .setActivity(Activity.watching("the odds"))
                .build();
        jda.awaitReady();
        jda.updateCommands().queue();
        //Above code creates the JDA Object for interface with discord, bot key should go in createDefault("") to authenticate the bot
        Main main = new Main(jda); //Create the main instance to connect the various classes
        jda.addEventListener(new ButtonListeners(main)); //Adds a button listener for future button events
        jda.addEventListener(new SlashCommandListeners(main));
        jda.addEventListener(new MessageHandlers(main));
        createCommands(jda);
        while (!main.getDataManager().isReady()) { //Spaghetti code to wait for the asynch database to be ready

        }
        System.out.println("Firebase ready");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        while(!input.equalsIgnoreCase("exit")){
            input = scanner.nextLine();
        }
        main.getDataManager().saveAllAccounts();
        main.getDataManager().saveAllRafflesToDatabase();
        jda.shutdown();
    }

    public static void createCommands(JDA jda) {
        CommandData commandData = new CommandData("lottobot", "Base LottoBot Commands");
        commandData.addSubcommands(new SubcommandData("create", "Create LottoBot game"));
        commandData.addSubcommands(new SubcommandData("help", "Get bot help"));
        commandData.addSubcommands(new SubcommandData("balance", "Get account balance"));
        commandData.addSubcommands(new SubcommandData("daily", "Get Daily Tickets"));
        jda.getGuildById(895321750932447263L).upsertCommand(commandData).queue();
        System.out.println("Created commands");
    }

    /**
     * Utilizes the jda object to iterate through guilds and create the bot channel if not found.
     * Adds and saves data relating to server id and channel ids via DataManager.
     *
     * @see JDA
     * @see DataManager
     */
    public static void createChannel(JDA jda) {
        //Iterates through all the servers with the bot
        for (Guild guild : jda.getGuilds()) {
            //Looks for designated bot channel
            boolean found = false;
            for (TextChannel textChannel : guild.getTextChannels()) {
                if (textChannel.getName().equals("lotto-bot")) {
                    found = true;
                }
            }
            //If not found creates one.
            if (!found) {
                guild.createTextChannel("lotto-bot");
            }
        }
    }

    /*
    Accessor and Mutator Methods
     */
    public DataManager getDataManager() {
        return dataManager;
    }

    public void setDataManager(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    public LotteryManager getLotteryManager() {
        return lotteryManager;
    }

    public void setLotteryManager(LotteryManager lotteryManager) {
        this.lotteryManager = lotteryManager;
    }

    public PaymentManager getPaymentManager() {
        return paymentManager;
    }

    public void setPaymentManager(PaymentManager paymentManager) {
        this.paymentManager = paymentManager;
    }

    public JDA getJda() {
        return jda;
    }

    public void setJda(JDA jda) {
        this.jda = jda;
    }

    public MessageHandlers getMessageHandler() {
        return messageHandler;
    }

    public void setMessageHandler(MessageHandlers messageHandler) {
        this.messageHandler = messageHandler;
    }


}
