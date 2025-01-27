package br.com.compass.model.dao.impl;

import br.com.compass.db.DB;
import br.com.compass.db.DbException;
import br.com.compass.model.dao.AccountDao;
import br.com.compass.model.entities.Account;
import br.com.compass.model.entities.enums.AccountType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDaoJDBC implements AccountDao {

    private final Connection conn;
    PreparedStatement stmt = null;

    public AccountDaoJDBC(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void insert(AccountType accountType, Integer user_id) {

        try {
            String sql = "INSERT INTO accounts (user_id, account_type) VALUES (?, ?)";

            stmt = conn.prepareStatement(sql);

            // Set the parameters from the User object
            stmt.setInt(1, user_id);
            stmt.setString(2, accountType.name().toUpperCase());

            // Execute the query
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(stmt);
        }
    }

    @Override
    public List<Account> findByUserId(Integer user_id) {
        ResultSet rs = null;

        try {
            String sql = "SELECT * FROM accounts WHERE user_id = ?";

            stmt = conn.prepareStatement(sql);
            stmt.setString(1, user_id.toString());
            rs = stmt.executeQuery();

            List<Account> accounts = new ArrayList<>();

            while (rs.next()) {
                Account account = new Account();
                account.setId(rs.getInt("id"));
                account.setAccountType(AccountType.valueOf(rs.getString("account_type")));
                accounts.add(account);
            }

            if (accounts.isEmpty()) {
                return null;
            }

            return accounts;

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(stmt);
            DB.closeResultSet(rs);
        }
    }

    @Override
    public void deposit(Integer user_id, AccountType accountType, Double balance) {

        if (user_id == null || accountType == null || balance == null || balance <= 0) {
            throw new IllegalArgumentException("Invalid input: user_id, accountType, and amount must be valid and amount > 0.");
        }

        try{
            String sql = "UPDATE accounts SET balance = balance + ? WHERE user_id = ? AND account_type = ?";

            stmt = conn.prepareStatement(sql);

            stmt.setDouble(1, balance);
            stmt.setInt(2, user_id);
            stmt.setString(3, accountType.name());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(stmt);
        }
    }

    @Override
    public void withdraw(Integer user_id, AccountType accountType, Double balance) {

        if (user_id == null || accountType == null || balance == null || balance <= 0) {
            throw new IllegalArgumentException("Invalid input: user_id, accountType, and amount must be valid and amount > 0.");
        }

        try{
            String sql = "UPDATE accounts SET balance = balance - ? WHERE user_id = ? AND account_type = ?";

            stmt = conn.prepareStatement(sql);

            stmt.setDouble(1, balance);
            stmt.setInt(2, user_id);
            stmt.setString(3, accountType.name());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(stmt);
        }
    }

}
