package br.com.compass.model.dao;

import br.com.compass.model.entities.Transaction;
import br.com.compass.model.entities.enums.TransactionType;

public interface TransactionDao {

    void saveTransaction(int user_id, int account_id, TransactionType transactionType, Double amount);
    void saveTransferTransaction(int user_id, int account_id, TransactionType transactionType, Double amount, Integer destination_account_id);
}
