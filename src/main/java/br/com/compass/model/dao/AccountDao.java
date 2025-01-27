package br.com.compass.model.dao;

import br.com.compass.model.entities.Account;
import br.com.compass.model.entities.enums.AccountType;

import java.util.List;

public interface AccountDao {

    void insert(AccountType accountType, Integer user_id);
    List<Account> findByUserId(Integer user_id);
}
