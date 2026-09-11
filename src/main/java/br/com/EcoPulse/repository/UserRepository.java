package br.com.EcoPulse.repository;

import br.com.EcoPulse.repository.dao.UserDao;

/** Fachada de compatibilidade; a execução JDBC fica centralizada no UserDao. */
public class UserRepository extends UserDao {
}
