package br.com.compass.model.dao.impl;

import br.com.compass.db.DB;
import br.com.compass.db.DbException;
import br.com.compass.model.dao.AccountDao;
import br.com.compass.model.entities.Account;

import java.sql.*;

public class AccountDaoJDBC implements AccountDao {

    private final Connection conn;

    public AccountDaoJDBC(Connection conn) {
        this.conn = conn;
    }

    @Override
    public int insert(Account account) {
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            String sql = "INSERT INTO accounts (user_id, account_type, balance) VALUES (?, ?, ?)";

            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            // Set the parameters from the User object
            stmt.setInt(1, account.getUser().getId());
            stmt.setString(2, account.getAccountType().name());
            stmt.setDouble(3, account.getBalance());

            // Execute the query
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                // Retrieve the generated key (ID)
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(stmt);
            DB.closeResultSet(rs);
        }
        return 0;
    }

}
