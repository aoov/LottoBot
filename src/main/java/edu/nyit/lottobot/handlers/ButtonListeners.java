package edu.nyit.lottobot.handlers;

import edu.nyit.lottobot.Main;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class ButtonListeners extends ListenerAdapter {
    private Main main;

    public ButtonListeners(Main main) {
        this.main = main;
    }

    @Override
    public void onButtonClick(@NotNull ButtonClickEvent event) {
        if (event.getComponentId().equals("enterTickets")) {

            event.reply("test").setEphemeral(true).queue();

        }
        super.onButtonClick(event);
    }
}
