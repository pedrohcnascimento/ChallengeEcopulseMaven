package br.com.EcoPulse.repository;

import br.com.EcoPulse.repository.dao.MissionDao;

/** Fachada de compatibilidade; a execução JDBC fica centralizada no MissionDao. */
public class MissionRepository extends MissionDao {
}
