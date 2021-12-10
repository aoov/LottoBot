package edu.nyit.lottobot.timer_tasks;

import edu.nyit.lottobot.data_classes.Game;
import edu.nyit.lottobot.data_classes.RaffleLottery;

import java.util.TimerTask;


/**
 * Class used to run a timer for games.
 */
public class TimerRunnable extends TimerTask {

    private final Game game;

    public TimerRunnable(Game game) {
        this.game = game;
    }

    @Override
    public void run() {
        if (game.isActive()) {
            if (game.getTimeLeft() > 0) {
                game.setTimeLeft(game.getTimeLeft() - 1);
                if(game.getTimeLeft() % 5 == 0){
                    game.print();
                }
            } else {
                game.setActive(false);
                game.finish();
                this.cancel();
            }
        }else{
            this.cancel();
        }
    }
}
