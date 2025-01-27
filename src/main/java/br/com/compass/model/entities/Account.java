package br.com.compass.model.entities;

import br.com.compass.model.entities.enums.AccountType;

public class Account {

    private int id;
    private AccountType accountType;
    private Double balance;

    private User user;

    public Account() {
        this.balance = 0.00;
    }

    public Account(int id, AccountType accountType, User user) {
        this.id = id;
        this.accountType = accountType;
        this.balance = 0.00;
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void deposit(double amount) {
        this.balance += amount;
    }

    public void withdraw(double amount) {
        this.balance -= amount;
    }
}
