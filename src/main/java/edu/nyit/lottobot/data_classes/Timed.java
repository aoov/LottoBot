package edu.nyit.lottobot.data_classes;

import edu.nyit.lottobot.timer_tasks.TimerRunnable;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Abstract class for games to inherit a timer easily.
 */
public abstract class Timed {

        public Timer start(Game game, Timer timer){
                TimerTask t = new TimerRunnable(game);
                timer.schedule(t, 0, 1000);
                return timer;
        }

        public void stop(Timer timer){
                timer.cancel();
        }



}
