package edu.nyit.lottobot.data_classes;

import javax.annotation.Nullable;

/**
 * Interface to require future games have these methods
 */
public interface Game {
    
    void print();

    void generateUniqueKey();

    String getUniqueKey();

    LotteryType getLotteryType();

    void finish();

    boolean isActive();

    long getTimeLeft();

    void save();

    void setTimeLeft(long timeLeft);

    void setActive(boolean value);

    void addParticipant(long user, Object object);
}
