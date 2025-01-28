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

        if (user_id == null || accountType == null) {
            throw new IllegalArgumentException("Invalid input: userId and accountType cannot be null.");
        }

        String sql = "INSERT INTO accounts (user_id, account_type) VALUES (?, ?)";

        try {
            stmt = conn.prepareStatement(sql);


            stmt.setInt(1, user_id);
            stmt.setString(2, accountType.name().toUpperCase());

            stmt.executeUpdate();

        } catch (SQLIntegrityConstraintViolationException e) {
            throw new DbException("Account already exists for the given user and account type.");
        } catch (SQLException e) {
            throw new DbException("Database error: Unable to insert account.");
        } finally {
            DB.closeStatement(stmt);
        }
    }

    @Override
    public List<Account> findByUserId(Integer user_id) {

        if (user_id == null) {
            throw new IllegalArgumentException("Invalid input: userId cannot be null.");
        }

        ResultSet rs = null;

        String sql = "SELECT * FROM accounts WHERE user_id = ?";

        try {
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
            throw new DbException("Database error: Unable to fetch accounts.");
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

        String sql = "UPDATE accounts SET balance = balance + ? WHERE user_id = ? AND account_type = ?";

        try{
            stmt = conn.prepareStatement(sql);

            stmt.setDouble(1, balance);
            stmt.setInt(2, user_id);
            stmt.setString(3, accountType.name());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DbException("No account found for the given user and account type.");
            }

        } catch (SQLException e) {
            throw new DbException("Database error: Unable to deposit.");
        } finally {
            DB.closeStatement(stmt);
        }
    }

    @Override
    public void withdraw(Integer user_id, AccountType accountType, Double balance) {

        if (user_id == null || accountType == null || balance == null || balance <= 0) {
            throw new IllegalArgumentException("Invalid input: user_id, accountType, and amount must be valid and amount > 0.");
        }

        String sql = "UPDATE accounts SET balance = balance - ? WHERE user_id = ? AND account_type = ?";

        try{
            stmt = conn.prepareStatement(sql);

            stmt.setDouble(1, balance);
            stmt.setInt(2, user_id);
            stmt.setString(3, accountType.name());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DbException("No account found for the given user and account type.");
            }

        } catch (SQLIntegrityConstraintViolationException e) {
            throw new DbException("Insufficient balance.");
        } catch (SQLException e) {
            throw new DbException("Database error: Unable to withdraw.");
        } finally {
            DB.closeStatement(stmt);
        }
    }
};


