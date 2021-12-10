package edu.nyit.lottobot.managers;

import edu.nyit.lottobot.Main;
import edu.nyit.lottobot.data_classes.LotteryType;
import edu.nyit.lottobot.data_classes.RaffleLottery;
import edu.nyit.lottobot.handlers.GameCreation;

import java.util.ArrayList;

public class LotteryManager {

    private ArrayList<GameCreation> gameCreations;
    private final Main main;

    public LotteryManager(Main main) {
        gameCreations = new ArrayList<>();
        this.main = main;
    }

    public void startGame(LotteryType lotteryType, long botChannelID, long userID, long guildID, ArrayList<Long> allowedRoles, long time) {
        if(lotteryType.equals(LotteryType.RAFFLE)){
            RaffleLottery raffleLottery = new RaffleLottery(guildID, botChannelID, userID, 0, time, allowedRoles,main, true);
            raffleLottery.print();
            while(raffleLottery.getMessageID() == 0){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            raffleLottery.start();
            main.getDataManager().getRaffleLotteries().put(raffleLottery.getUniqueKey(), raffleLottery);
        }
    }

    public synchronized void startGameCreation(long guildID, long userID, long botChannelID) {
        gameCreations.add(new GameCreation(userID, guildID, gameCreations.size(), botChannelID, main));
    }

    public ArrayList<GameCreation> getGameCreations() {
        return gameCreations;
    }
}
