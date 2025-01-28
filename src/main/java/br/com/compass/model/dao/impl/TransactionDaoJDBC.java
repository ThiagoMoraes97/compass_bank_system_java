package br.com.compass.model.dao.impl;

import br.com.compass.model.dao.AccountDao;
import br.com.compass.model.dao.DaoFactory;
import br.com.compass.model.dao.TransactionDao;
import br.com.compass.model.dao.UserDao;
import br.com.compass.model.entities.Transaction;
import br.com.compass.model.entities.User;
import br.com.compass.model.entities.enums.AccountType;
import br.com.compass.model.entities.enums.TransactionType;

import br.com.compass.db.DB;
import br.com.compass.db.DbException;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransactionDaoJDBC implements TransactionDao {

    private final Connection conn;
    PreparedStatement stmt = null;
    AccountDao accountDao = DaoFactory.createAccountDao();
    UserDao userDao = DaoFactory.createUserDao();

    public TransactionDaoJDBC(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void saveTransaction(int user_id, int account_id, TransactionType transactionType, Double amount) {
        try{

            String sql = "INSERT INTO transactions (user_id, account_id, transaction_type, amount) VALUES (?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, user_id);
            stmt.setInt(2, account_id);
            stmt.setString(3, transactionType.name());
            stmt.setDouble(4, amount);

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(stmt);
        }
    }

    @Override
    public void saveTransferTransaction(int user_id, int account_id, TransactionType transactionType, Double amount, Integer destination_account_id) {
        String sql = "INSERT INTO transactions (user_id, account_id, transaction_type, amount, destination_account_id) VALUES (?, ?, ?, ?, ?)";

        try{

            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, user_id);
            stmt.setInt(2, account_id);
            stmt.setString(3, transactionType.name());
            stmt.setDouble(4, amount);
            stmt.setInt(5, destination_account_id);

            stmt.executeUpdate();

            if (!conn.getAutoCommit()) {
                conn.commit();
            }

        } catch (SQLException e) {
            throw new DbException(e.getMessage());

        } finally {
            DB.closeStatement(stmt);
        }
    }

    @Override
    public void transferTransaction(int user_id, AccountType userAccountType, Double amount, int destination_user_id, AccountType destinationAccountType){

        try{
            conn.setAutoCommit(false);
            accountDao.withdraw(user_id, userAccountType, amount);
            accountDao.deposit(destination_user_id, destinationAccountType, amount);
            conn.commit();
        } catch ( SQLException e ) {
            try{
                conn.rollback();
                throw new DbException("Transaction rolled back! Caused by: " + e.getMessage());
            } catch ( SQLException e1 ) {
                throw new DbException("Error trying to rollback! Caused by: " + e.getMessage());
            }
        }
    };

    @Override
    public List<Transaction> listAllTransactions(User user){
        ResultSet rs = null;
        try {
            String sql = "SELECT * FROM transactions WHERE user_id = ? ORDER BY transaction_date ASC;";

            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, user.getId());
            rs = stmt.executeQuery();

            List<Transaction> transactions = new ArrayList<>();


            while (rs.next()) {
                Transaction transaction = new Transaction();
                transaction.setId(rs.getInt("id"));
                transaction.setTransactionType(TransactionType.valueOf(rs.getString("transaction_type")));
                transaction.setAmount(rs.getDouble("amount"));
                Date sqlDate = rs.getDate("transaction_date");
                if (sqlDate != null) {
                    LocalDate transactionDate = sqlDate.toLocalDate();
                    transaction.setTimestamp(transactionDate);
                }
                transactions.add(transaction);
            }

            if (transactions.isEmpty()) {
                return null;
            }

            return transactions;

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(stmt);
            DB.closeResultSet(rs);
        }
    }
}
