package edu.nyit.lottobot.data_classes;

public class Account {
    private final long accountID;
    private long balance;

    public Account(){
        accountID = -1;
        balance = 0;
    }

    public Account(long accountID, long balance) {
        this.accountID = accountID;
        this.balance = balance;
    }

    public long getAccountID() {
        return accountID;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public void addBalance(long balance){
        this.balance += balance;
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountID=" + accountID +
                ", balance=" + balance +
                '}';
    }
}
