package edu.nyit.lottobot.timer_tasks;

import edu.nyit.lottobot.data_classes.Lottery;
import edu.nyit.lottobot.data_classes.LotteryType;

import java.util.TimerTask;

public class TimerRunnable extends TimerTask {

    private final Lottery lottery;

    public TimerRunnable(Lottery lottery) {
        this.lottery = lottery;
    }

    @Override
    public void run() {
        if (lottery.isActive()) {
            if (lottery.getTimeLeft() > 0) {
                lottery.setTimeLeft(lottery.getTimeLeft() - 1);
                lottery.PrintLottery();
            } else {
                lottery.setActive(false);
                lottery.finishLottery();
                this.cancel();
            }
        }else{
            this.cancel();
        }
    }
}
