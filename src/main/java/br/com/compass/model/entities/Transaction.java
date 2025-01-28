package br.com.compass.model.entities;

import br.com.compass.model.entities.enums.TransactionType;

import java.time.LocalDate;


public class Transaction {

    private int id;
    private TransactionType transactionType;
    private Double amount;
    private LocalDate timestamp;

    public Transaction() {
    }

    public Transaction(int id, TransactionType transactionType, Double amount, LocalDate timestamp) {
        this.id = id;
        this.transactionType = transactionType;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public LocalDate getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDate timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return String.format(
                "| %-10s | %-10.2f | %-16s |%n",
                transactionType, amount, timestamp.toString()
        );
    }
}
