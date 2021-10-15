package edu.nyit.lottobot.managers;

public class DataManager {

    public DataManager() {
    }

    /**
     * Returns whether the channel is designated for the bot.
     * Should check against database to determine if the channel is a lotto-bot channel.
     * @param serverID Guild/Server ID in long format type.
     * @param channelID Channel ID in long format type
     */
    public boolean isBotChannel(long serverID, long channelID) {
        return true; //Temp Value
    }


}
