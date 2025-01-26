package br.com.compass.model.dao.impl;

import br.com.compass.db.DB;
import br.com.compass.model.dao.UserDao;
import br.com.compass.model.entities.User;

import java.sql.*;

public class UserDaoJDBC implements UserDao {

    private Connection conn = null;

    public UserDaoJDBC(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void insert(User user) {
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            String sql = "INSERT INTO users (name, birth_date, cpf, phone, password) VALUES (?, ?, ?, ?, ?)";

            conn = DB.getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            // Set the parameters from the User object
            stmt.setString(1, user.getName());
            stmt.setDate(2, Date.valueOf(user.getBirthDate())); // Assuming birthDate is a LocalDate
            stmt.setString(3, user.getCpf());
            stmt.setString(4, user.getPhone());
            stmt.setString(5, user.getPassword());

            // Execute the query
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                // Retrieve the generated key (ID)
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    user.setId(id);
                }
            } else {
                System.out.println("No rows affected. User insertion failed.");
            }


        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close resources
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
