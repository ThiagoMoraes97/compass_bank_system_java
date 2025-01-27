package br.com.compass.model.dao;

import br.com.compass.db.DB;
import br.com.compass.model.dao.impl.AccountDaoJDBC;
import br.com.compass.model.dao.impl.TransactionDaoJDBC;
import br.com.compass.model.dao.impl.UserDaoJDBC;

public class DaoFactory {

    public static UserDao createUserDao() {
        return new UserDaoJDBC(DB.getConnection());
    }

    public static AccountDao createAccountDao() {
        return new AccountDaoJDBC(DB.getConnection());
    }

    public static TransactionDao createTransactionDao() {
        return new TransactionDaoJDBC(DB.getConnection());
    }
}
