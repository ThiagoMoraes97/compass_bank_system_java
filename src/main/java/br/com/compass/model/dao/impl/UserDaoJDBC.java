package br.com.compass.model.dao.impl;

import br.com.compass.db.DB;
import br.com.compass.db.DbException;
import br.com.compass.model.dao.UserDao;
import br.com.compass.model.entities.Account;
import br.com.compass.model.entities.User;
import br.com.compass.model.entities.enums.AccountType;

import java.sql.*;
import java.time.LocalDate;

public class UserDaoJDBC implements UserDao {

    private final Connection conn;
    PreparedStatement stmt = null;
    ResultSet rs = null;

    public UserDaoJDBC(Connection conn) {
        this.conn = conn;
    }

    @Override
    public int insert(String name, LocalDate dateOfBirth, String cpf, String phone, String password) {

        try {
            String sql = "INSERT INTO users (name, birth_date, cpf, phone, password) VALUES (?, ?, ?, ?, ?)";

            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            // Set the parameters from the User object
            stmt.setString(1, name);
            stmt.setDate(2, Date.valueOf(dateOfBirth)); // Assuming birthDate is a LocalDate
            stmt.setString(3, cpf);
            stmt.setString(4, phone);
            stmt.setString(5, password);

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

    @Override
    public User findByCPF(String cpf) {

        try {
            String sql = "SELECT users.*, accounts.account_type, accounts.id AS account_id, accounts.balance AS account_balance " +
                    "FROM users " +
                    "LEFT JOIN accounts ON users.id = accounts.user_id " +
                    "WHERE users.cpf = ?";

            stmt = conn.prepareStatement(sql);
            stmt.setString(1, cpf);
            rs = stmt.executeQuery();

            User user = null;

            while (rs.next()) {
                if (user == null) {
                    user = new User();
                    user.setId(rs.getInt("id"));
                    user.setName(rs.getString("name"));
                    Date sqlDate = rs.getDate("birth_date");
                    if (sqlDate != null) {
                        LocalDate birthDate = sqlDate.toLocalDate();
                        user.setBirthDate(birthDate);
                    }
                    user.setCpf(rs.getString("cpf"));
                    user.setPhone(rs.getString("phone"));
                    user.setPassword(rs.getString("password"));
                }

                String accountTypeString = rs.getString("account_type");
                if (accountTypeString != null) {
                    AccountType accountType = AccountType.valueOf(accountTypeString);
                    Account account = new Account();
                    account.setId(rs.getInt("account_id"));
                    account.setAccountType(accountType);
                    account.setUser(user);
                    account.setBalance(rs.getDouble("account_balance"));
                    user.addAccount(account);
                }
            }

            return user;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(stmt);
            DB.closeResultSet(rs);
        }
    }
}
