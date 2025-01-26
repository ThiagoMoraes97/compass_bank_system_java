package br.com.compass.model.dao;

import br.com.compass.db.DB;
import br.com.compass.model.dao.impl.UserDaoJDBC;

public class DaoFactory {

    public static UserDao createUserDao() {
        return new UserDaoJDBC(DB.getConnection());
    }
}
