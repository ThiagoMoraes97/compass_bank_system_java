package br.com.compass.model.dao;

import br.com.compass.model.entities.User;

import java.time.LocalDate;

public interface UserDao {

    int insert(String name, LocalDate dateOfBirth, String cpf, String phone, String password);
    User findByCPF(String cpf);
}
