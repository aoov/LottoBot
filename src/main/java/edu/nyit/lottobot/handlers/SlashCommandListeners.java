package edu.nyit.lottobot.handlers;

import edu.nyit.lottobot.Main;
import edu.nyit.lottobot.data_classes.Account;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public class SlashCommandListeners extends ListenerAdapter {
    private Main main;
    private HashSet<Long> daily;



    public SlashCommandListeners(Main main) {
        this.main = main;
        daily = new HashSet<>();
    }

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        System.out.println(event.getCommandPath());
        if(event.getCommandPath().equals("lottobot/create")){
            event.reply("Game creation beginning").setEphemeral(true).queue();
            main.getLotteryManager().startGameCreation(event.getGuild().getIdLong(), event.getUser().getIdLong(), event.getTextChannel().getIdLong());
        }

        if(event.getCommandPath().equals("lottobot/daily")){
            if(!daily.contains(event.getUser().getIdLong())){
                event.reply("You have been awarded 200 tickets.").setEphemeral(true).queue();
                if(main.getDataManager().getAccounts().containsKey(event.getUser().getIdLong())){
                    main.getDataManager().getAccount(event.getUser().getIdLong()).addBalance(200);
                }else{
                    main.getDataManager().getAccounts().put(event.getUser().getIdLong(), new Account(event.getUser().getIdLong(),200));
                }
                daily.add(event.getUser().getIdLong());
            }else{
                event.reply("You have already claimed your daily reward!").setEphemeral(true).queue();
            }
            return;
        }

        if(event.getCommandPath().equalsIgnoreCase("lottobot/balance")){
            if(main.getDataManager().getAccounts().containsKey(event.getUser().getIdLong())){
               event.reply("Account Balance: " + main.getDataManager().getAccount(event.getUser().getIdLong()).getBalance()).setEphemeral(true).queue();
            }else{
                event.reply("Account Balance: 0").setEphemeral(true).queue();
            }
        }

        super.onSlashCommand(event);
    }
}
