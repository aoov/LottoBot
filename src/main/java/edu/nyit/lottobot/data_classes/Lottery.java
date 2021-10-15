package edu.nyit.lottobot.data_classes;

import net.dv8tion.jda.api.entities.User;
import org.joda.time.Duration;

public class Lottery {

    private final long guildID;
    private final User startedBy;
    private final LotteryType lotteryType;
    private long prizePool;
    private Duration durationLeft;

    public Lottery(long guildID, User startedBy, LotteryType lotteryType, long prizePool, Duration durationLeft) {
        this.guildID = guildID;
        this.startedBy = startedBy;
        this.lotteryType = lotteryType;
        this.prizePool = prizePool;
        this.durationLeft = durationLeft;
    }
}
