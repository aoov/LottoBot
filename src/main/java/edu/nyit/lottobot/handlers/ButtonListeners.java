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
            if (!main.getDataManager().getAccounts().containsKey(event.getMember().getUser().getIdLong())
                    || (main.getDataManager().getAccounts().get(event.getIdLong()) != null && main.getDataManager().getAccount(event.getIdLong()).getBalance() == 0)) {
                event.reply("You do not have any tickets!").setEphemeral(true).queue();
                return;
            }
            main.getDataManager().addingTicketsAdd(event.getMember().getUser().getIdLong(), event.getMessage().getEmbeds().get(0).getFooter().getText().replaceAll("ID: ", ""));
            event.reply("How many tickets would you like to enter?").setEphemeral(true).queue();
            return;
        }
        super.onButtonClick(event);
    }
}
