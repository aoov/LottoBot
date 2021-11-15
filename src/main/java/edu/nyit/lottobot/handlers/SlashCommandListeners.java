package edu.nyit.lottobot.handlers;

import edu.nyit.lottobot.Main;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class SlashCommandListeners extends ListenerAdapter {
    private Main main;

    public SlashCommandListeners(Main main) {
        this.main = main;
    }

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        System.out.println(event.getCommandPath());
        if(event.getCommandPath().equals("lottobot/create")){
            event.reply("Game creation beginning").setEphemeral(true).queue();
            main.getLotteryManager().startGameCreation(event.getGuild().getIdLong(), event.getUser().getIdLong(), event.getTextChannel().getIdLong());
        }
        super.onSlashCommand(event);
    }
}
