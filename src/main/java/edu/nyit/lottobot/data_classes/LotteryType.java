package edu.nyit.lottobot.data_classes;

public enum LotteryType {
    POWER_BALL, RAFFLE;

    @Override
    public String toString() {
        switch (this) {
            case RAFFLE -> {
                return "Raffle";
            }
            case POWER_BALL -> {
                return "Power Ball";
            }
            default -> {
                return "FIX IN ENUM";
            }
        }
    }
}
