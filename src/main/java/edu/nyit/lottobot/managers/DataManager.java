package edu.nyit.lottobot.managers;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;
import edu.nyit.lottobot.Main;
import edu.nyit.lottobot.data_classes.Account;
import edu.nyit.lottobot.data_classes.RaffleLottery;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

public class DataManager {
    private boolean ready;
    private HashMap<Long, Account> accounts;
    private HashMap<String, RaffleLottery> raffleLotteries;
    private final FirebaseDatabase firebaseDatabase;
    private DatabaseReference accountReference;
    private DatabaseReference raffleLotteryReference;
    private Main main;

    public DataManager(Main main) {
        setupFirebase();
        accounts = new HashMap<>();
        raffleLotteries = new HashMap<>();
        this.main = main;
        firebaseDatabase = FirebaseDatabase.getInstance();
      //  updateLocalAccounts(); //Load raffles from database
     //   updateLocalRaffles(); //Load raffles from database
    }

    //Sets up and connects to the firebase database.
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
                //System.out.println(document);
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

    /**
     * Method to save an account object to the database
     *
     * @param account Account object to save
     */
    public void saveAccount(Account account) {
        DatabaseReference accounts = firebaseDatabase.getReference("data").child("accounts");
        accounts.child(account.getAccountID() + "").setValueAsync(account);
    }

    /**
     * Saves all local account objects to the database
     */
    public void saveAllAccounts() {
        for (Account account : accounts.values()) {
            saveAccount(account);
        }
    }

    /**
     * Method to a save RaffleLottery object to the database
     *
     * @param raffleLottery RaffleLottery object to save
     */
    public void saveRaffleToDatabase(RaffleLottery raffleLottery) {
        DatabaseReference lotteries = firebaseDatabase.getReference("data/lotteries/raffles");
        lotteries.child(raffleLottery.getUniqueKey()).setValueAsync(raffleLottery);
    }

    /**
     * Updates local RaffleLottery object list from database
     *
     * @return boolean value when complete
     */
    public boolean updateLocalRaffles() {
        this.raffleLotteryReference = firebaseDatabase.getReference("data/lotteries/raffles");
        CountDownLatch countDownLatch = new CountDownLatch(1);
        raffleLotteryReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    boolean active = (boolean) dataSnapshot.child("active").getValue();
                    long botChannelID = (long) dataSnapshot.child("botChannelID").getValue();
                    long guildID = (long) dataSnapshot.child("guildID").getValue();
                    long messageID = (long) dataSnapshot.child("messageID").getValue();
                    long prizePool = (long) dataSnapshot.child("prizePool").getValue();
                    ArrayList<Long> allowedRoles = (ArrayList<Long>) dataSnapshot.child("allowedRoles").getValue();
                    HashMap<String, Long> participants = (HashMap<String, Long>) dataSnapshot.child("participants").getValue();
                    long startedBy = dataSnapshot.child("startedBy").getValue(Long.class);
                    long timeLeft = dataSnapshot.child("timeLeft").getValue(Long.class);
                    String uniqueKey = dataSnapshot.child("uniqueKey").getValue(String.class);
                    long winner = dataSnapshot.child("winner").getValue(Long.class);
                    RaffleLottery raffleLottery = new RaffleLottery(guildID, botChannelID, startedBy, prizePool, timeLeft, allowedRoles, main, false);
                    if (!participants.isEmpty()) {
                        raffleLottery.setParticipants(participants);
                    }
                    raffleLottery.setUniqueKey(uniqueKey);
                    raffleLottery.setWinner(winner);
                    raffleLottery.setMessageID(messageID);
                    raffleLottery.setActive(active);
                    raffleLotteries.put(raffleLottery.getUniqueKey(), raffleLottery);
                }
                countDownLatch.countDown();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                countDownLatch.countDown();
            }
        });
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Updates local Account list from database
     *
     * @return boolean value upon completion
     */
    public boolean updateLocalAccounts() {
        accountReference = firebaseDatabase.getReference("data").child("accounts");
        CountDownLatch countDownLatch = new CountDownLatch(1);
        accountReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Account temp = dataSnapshot.getValue(Account.class);
                    if (temp != null && temp.getAccountID() != -1) {
                        accounts.put(temp.getAccountID(), temp);
                    }
                }
                countDownLatch.countDown();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                countDownLatch.countDown();
            }
        });
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Method to get RaffleLottery from raffleID (Unique Key)
     *
     * Checks locally then updates to check from database
     *
     * @param raffleID Unique key of the RaffleLottery object
     * @return RaffleLottery object, null if nonexistent
     */
    public RaffleLottery getRaffleLottery(String raffleID) {
        if (raffleLotteries.containsKey(raffleID)) {
            return raffleLotteries.get(raffleID);
        }
        updateLocalRaffles();
        return raffleLotteries.getOrDefault(raffleID, null);
    }

    /**
     * Method to get RaffleLottery from message id (Unique message ID)
     *
     * Checks locally then updates to check from database
     *
     * @param messageID Unique messageID of the RaffleLottery object
     * @return RaffleLottery object, null if nonexistent
     */
    public RaffleLottery getRaffleLottery(Long messageID) {
        for (RaffleLottery raffleLottery : raffleLotteries.values()) {
            if (raffleLottery.getMessageID() == messageID) {
                return raffleLottery;
            }
        }
        updateLocalRaffles();
        for (RaffleLottery raffleLottery : raffleLotteries.values()) {
            if (raffleLottery.getMessageID() == messageID) {
                return raffleLottery;
            }
        }
        return null;
    }

    /**
     * Method to retrieve the account of specific userID
     * <p>
     * Checks locally then updates to check from database
     *
     * @param userID ID of desired User
     * @return Account object of user, null if nonexistent
     */
    public Account getAccount(long userID) {
        if (accounts.containsKey(userID)) {
            return accounts.get(userID);
        }
        updateLocalAccounts();
        return accounts.getOrDefault(userID, null);
    }

    /*
    Getters and Setters
     */
    public FirebaseDatabase getFirebaseDatabase() {
        return firebaseDatabase;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public HashMap<Long, Account> getAccounts() {
        return accounts;
    }

    public HashMap<String, RaffleLottery> getRaffleLotteries() {
        return raffleLotteries;
    }

    public DatabaseReference getAccountReference() {
        return accountReference;
    }

    public DatabaseReference getRaffleLotteryReference() {
        return raffleLotteryReference;
    }
}
