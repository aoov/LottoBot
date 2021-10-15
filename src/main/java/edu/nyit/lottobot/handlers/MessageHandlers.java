package edu.nyit.lottobot.handlers;

import edu.nyit.lottobot.Main;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.concurrent.Task;
import org.jetbrains.annotations.NotNull;

import java.util.Timer;
import java.util.TimerTask;

public class MessageHandlers extends ListenerAdapter {

    private final Main main;

    public MessageHandlers(Main main) {
        this.main = main;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        //Determines if message is in the proper channel.
        if (main.getDataManager().isBotChannel(event.getGuild().getIdLong(), event.getChannel().getIdLong())) {
            Message msg = event.getMessage();
            if (!msg.getAuthor().isBot()) {
                Timer timer = new Timer(true);
                timer.schedule(new TimerClass(15, msg), 1000,1000 );
                super.onMessageReceived(event);
                System.out.println("test");
                //DM Test
                if (msg.getContentRaw().equals("!ping")) {
                    event.getAuthor().openPrivateChannel().queue(privateChannel ->
                    {
                        privateChannel.sendMessage("DM Test").queue();
                    });

                    //Ping pong
                    MessageChannel channel = event.getChannel();
                    long time = System.currentTimeMillis();
                    channel.sendMessage("Pong!") /* => RestAction<Message> */
                            .queue(response /* => Message */ -> {
                                response.editMessageFormat("Pong: %d ms", System.currentTimeMillis() - time).queue();
                            });
                    super.onMessageReceived(event);
                }
            }
        }
    }

    class TimerClass extends TimerTask {
        private long time;
        Message msg;
        Message response;

        TimerClass(long time, Message msg) {
            this.time = time;
            this.msg = msg;
            msg.getChannel().sendMessage("Timer Starting").queue(response -> {
                this.response = response;
            });
        }

        public void run() {
            System.out.println(time);
            if (time > 0) {
                time--;
                response.editMessageFormat("Time left: " + time).queue();
            }else{
                this.cancel();
            }
        }
    }
}


