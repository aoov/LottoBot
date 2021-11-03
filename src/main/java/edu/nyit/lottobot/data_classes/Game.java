package edu.nyit.lottobot.data_classes;
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
}
