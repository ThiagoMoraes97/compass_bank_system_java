package br.com.compass.model.dao.impl;

import br.com.compass.db.DB;
import br.com.compass.db.DbException;
import br.com.compass.model.dao.UserDao;
import br.com.compass.model.entities.User;

import java.sql.*;
import java.time.LocalDate;

public class UserDaoJDBC implements UserDao {

    private final Connection conn;

    public UserDaoJDBC(Connection conn) {
        this.conn = conn;
    }

    @Override
    public int insert(String name, LocalDate dateOfBirth, String cpf, String phone, String password) {
        PreparedStatement stmt = null;
        ResultSet rs = null;

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
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT * FROM users WHERE cpf = ?";

            stmt = conn.prepareStatement(sql);
            stmt.setString(1, cpf);
            rs = stmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setCpf(rs.getString("cpf"));
                user.setPassword(rs.getString("password"));
                return user;
            }

            return null;

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(stmt);
            DB.closeResultSet(rs);
        }

    }
}
