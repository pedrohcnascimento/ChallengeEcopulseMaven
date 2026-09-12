package br.com.EcoPulse.repository;

import br.com.EcoPulse.repository.dao.AvatarDao;

/** Fachada de compatibilidade; a execução JDBC fica centralizada no AvatarDao. */
public class AvatarRepository extends AvatarDao {
}
