package edu.nyit.lottobot.managers;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;
import edu.nyit.lottobot.data_classes.Account;
import edu.nyit.lottobot.data_classes.Lottery;

import java.io.FileInputStream;

import java.io.IOException;

public class DataManager {
    private boolean ready;
    private final FirebaseDatabase firebaseDatabase;

    public DataManager() {
        setupFirebase();
        firebaseDatabase = FirebaseDatabase.getInstance();
    }

    //Sets up and connects to the firebase database
    public void setupFirebase() {
        FileInputStream serviceAccount = null;
        try {
            serviceAccount = new FileInputStream("C:\\Users\\xryda\\IdeaProjects\\LottoBot\\serviceAccountKey.json");
        } catch (Exception e) {
            e.printStackTrace();
        }
        FirebaseOptions.Builder options = null;
        try {
            assert serviceAccount != null;
            options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://lottobot-ee1b1-default-rtdb.firebaseio.com");
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        FirebaseApp.initializeApp(options.build());

        // As an admin, the app has access to read and write all data, regardless of Security Rules
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("restricted_access/secret_document");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object document = dataSnapshot.getValue();
                System.out.println(document);
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
        ready = true;
    }

    /**
     * Returns whether the channel is designated for the bot.
     * Should check against database to determine if the channel is a lotto-bot channel.
     *
     * @param serverID  Guild/Server ID in long format type.
     * @param channelID Channel ID in long format type
     */
    public boolean isBotChannel(long serverID, long channelID) {
        return true; //Temp Value
    }

    /**
     * Method to get the Channel ID of the designated bot channel
     *
     * @param guildID GuildID
     * @return the bot channel ID from given guild ID, -1 if non-existent
     */
    public long getBotChannelID(long guildID) {
        return 4343;
    }


    public void saveAccount(Account account) {
        DatabaseReference accounts = firebaseDatabase.getReference("data").child("accounts");
        accounts.child(account.getAccountID() + "").setValueAsync(account);
    }

    public void saveLottery(Lottery lottery) {
        DatabaseReference lotteries = firebaseDatabase.getReference("data/lotteries");
        lotteries.push().setValueAsync(lottery);
    }

    //Getter and setters

    public FirebaseDatabase getFirebaseDatabase() {
        return firebaseDatabase;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }
}
