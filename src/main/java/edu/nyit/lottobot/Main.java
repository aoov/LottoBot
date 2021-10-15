package edu.nyit.lottobot;

import edu.nyit.lottobot.handlers.MessageHandlers;
import edu.nyit.lottobot.managers.DataManager;
import edu.nyit.lottobot.managers.LotteryManager;
import edu.nyit.lottobot.managers.PaymentManager;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.util.Scanner;

public class Main extends ListenerAdapter {

    private DataManager dataManager;
    private LotteryManager lotteryManager;
    private PaymentManager paymentManager;

    public Main(DataManager dataManager, LotteryManager lotteryManager, PaymentManager paymentManager) {
        this.dataManager = dataManager;
        this.lotteryManager = lotteryManager;
        this.paymentManager = paymentManager;
    }

    public static void main(String[] args) throws LoginException, InterruptedException {
        Main main = new Main(new DataManager(), new LotteryManager(), new PaymentManager());
        JDA jda = JDABuilder.createDefault("ODk1MzEzODY0Mzc4NDE3MjAy.YV2wAw.zc4n8ywGUhCQIuUAXuqOWCAL3bc")
                .setActivity(Activity.watching("the odds"))
                .addEventListeners(new MessageHandlers(main))
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
    /**
     * Utilizes the jda object to iterate through guilds and create the bot channel if not found.
     * Adds and saves data relating to server id and channel ids via DataManager.
     * @see JDA
     * @see DataManager
     */
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
}
