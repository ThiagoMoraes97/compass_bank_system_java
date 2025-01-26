package br.com.compass.model.dao;

import br.com.compass.model.entities.User;

public interface UserDao {

    int insert(User user);
    User findByCPF(String cpf);
}
