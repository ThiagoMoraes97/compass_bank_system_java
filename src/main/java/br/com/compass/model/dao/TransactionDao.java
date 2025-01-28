package br.com.compass.model.dao;

import br.com.compass.model.entities.Transaction;
import br.com.compass.model.entities.User;
import br.com.compass.model.entities.enums.AccountType;
import br.com.compass.model.entities.enums.TransactionType;

import java.util.List;

public interface TransactionDao {

    void saveTransaction(int user_id, int account_id, TransactionType transactionType, Double amount);
    void saveTransferTransaction(int user_id, int account_id, TransactionType transactionType, Double amount, Integer destination_account_id);
    void transferTransaction(int user_id, AccountType userAccountType, Double amount, int destination_user_id, AccountType destinationAccountType);
    List<Transaction> listAllTransactions(User user);

}
