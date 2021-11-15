package edu.nyit.lottobot.handlers;

import edu.nyit.lottobot.Main;
import edu.nyit.lottobot.data_classes.LotteryType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class GameCreation extends ListenerAdapter {
    private final long userID;
    private final Main main;
    private long tempChannelID;
    private int step;
    private final long guildID;
    private LinkedList<String> input;
    private final JDA jda;
    private LotteryType lotteryType;
    private long time;
    private ArrayList<Long> allowedRoles;
    private long botChannelID;


    public GameCreation(long userID, long guildID, int number, long botChannelID, Main main) {
        this.userID = userID;
        this.step = 0;
        this.guildID = guildID;
        this.input = new LinkedList<>();
        this.main = main;
        this.botChannelID = botChannelID;
        this.jda = main.getJda();
        this.allowedRoles = new ArrayList<>();
        jda.getGuildById(guildID).createTextChannel("game-creation-" + number)
                .addPermissionOverride(jda.getGuildById(guildID).getPublicRole(), null, EnumSet.of(Permission.VIEW_CHANNEL))
                .addMemberPermissionOverride(userID, EnumSet.of(Permission.VIEW_CHANNEL), null)
                .queue(textChannel -> {
                    tempChannelID = textChannel.getIdLong();
                    MessageBuilder mb = new MessageBuilder();
                    mb.append("Hello <@" + userID + ">,\nThis is the channel for creating a new LottoBot game!\nFeel free to cancel at anytime using the button below")
                            .setActionRows(ActionRow.of(Button.of(ButtonStyle.DANGER, "cancelcreation", "Cancel Creation", Emoji.fromUnicode("U+1F6D1"))));
                    textChannel.sendMessage(mb.build()).queue();
                    jda.addEventListener(this);
                    stepOne();
                });
    }

    //Type of raffle
    private void stepOne() {
        step = 1;
        MessageBuilder messageBuilder = new MessageBuilder();
        messageBuilder.append("---------\n");
        messageBuilder.append("Please select one of the following types");
        messageBuilder.append("``` Raffle\n Power Ball```");
        sendMessage(messageBuilder.build());
        boolean valid = false;
        while (!valid) {
            while (input.isEmpty()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            String s = input.pop().toLowerCase(Locale.ROOT);
            switch (s) {
                case "raffle" -> {
                    lotteryType = LotteryType.RAFFLE;
                    valid = true;
                }
                case "power ball" -> {
                    lotteryType = LotteryType.POWER_BALL;
                    valid = true;
                }
                default -> {
                    main.getMessageHandler().pingSelfDestructMessage(userID, tempChannelID, 9L, "Incorrect value, please try again.");
                }
            }
        }
        sendMessage("Game Type set to: " + lotteryType.toString());
        stepTwo();
    }

    //The length
    public void stepTwo() {
        step = 2;
        sendMessage("Next, enter how long, in seconds, you want the game to go on for");
        boolean valid = false;
        while (!valid) {
            while (input.isEmpty()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            String s = input.pop();
            try {
                time = Long.parseLong(s);
                valid = true;
            } catch (NumberFormatException exception) {
                main.getMessageHandler().pingSelfDestructMessage(userID, tempChannelID, 5, "Entered value is not valid please try again.");
            }
        }
        sendMessage("Game Time set to: " + time);
        stepThree();
    }

    //Allowed roles
    public void stepThree() {
        step = 3;
        sendMessage("Next, select which roles are allowed to join this event \nPing the roles with seperated by a space.");
        boolean valid = false;
        while (!valid) {
            while (input.isEmpty()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            String[] s = input.pop().split(" ");
            for (int i = 0; i < s.length; i++) {
                s[i] = s[i].substring(s[i].indexOf('&') + 1, s[i].length() - 1);
            }
            for (String string : s) {
                if (string.equals("@everyon")) {
                    allowedRoles = null;
                    valid = true;
                    break;
                } else {
                    try {
                        allowedRoles.add(Long.parseLong(string));
                    } catch (NumberFormatException exception) {
                        valid = false;
                        main.getMessageHandler().pingSelfDestructMessage(userID, tempChannelID, 5, "Invalid input, please try again.");
                        continue;
                    }
                    valid = true;
                }
            }
        }
        StringBuilder z = new StringBuilder();
        z.append("Allowed Roles: ");
        if(allowedRoles == null){
            z.append("<@&everyone>");
        }else {
            for (long x : allowedRoles) {
                z.append("<@&" + x + "> ");
            }
        }
        stepFour();
    }

    public void stepFour(){
        step = 4;
        sendMessage("Game creation finished. Channel will now delete itself.");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        main.getLotteryManager().startGame(lotteryType, botChannelID, userID, guildID, allowedRoles, time);
        end();
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if (event.getChannel().getIdLong() == tempChannelID && !event.getAuthor().isBot()) {
            input.push(event.getMessage().getContentRaw());
        }
        super.onGuildMessageReceived(event);
    }

    @Override
    public void onButtonClick(@NotNull ButtonClickEvent event) {
        if (event.getButton().getId().equals("cancelcreation")) {
            end();
        }
        super.onButtonClick(event);
    }

    public void end() {
        main.getJda().removeEventListener(this);
        jda.getGuildById(guildID).getTextChannelById(tempChannelID).delete().queue();
        main.getLotteryManager().getGameCreations().remove(this);
    }

    public void sendMessage(String s) {
        jda.getTextChannelById(tempChannelID).sendMessage(s).queue();
    }

    public void sendMessage(Message message) {
        jda.getTextChannelById(tempChannelID).sendMessage(message).queue();
    }
}
