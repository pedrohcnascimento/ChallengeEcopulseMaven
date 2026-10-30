package br.com.EcoPulse.test;

import br.com.EcoPulse.config.ConnectionFactory;
import br.com.EcoPulse.domain.User;
import br.com.EcoPulse.service.UserService;

import java.time.LocalDate;

/** Classe executável de demonstração e teste manual do CRUD em T_SOUL_USUARIO. */
public final class UserCrudTest {
    private UserCrudTest() { }

    public static void main(String[] args) {
        ConnectionFactory.initializeDatabase();
        UserService service = new UserService();
        long suffix = System.currentTimeMillis() % 100000000L;
        String email = "teste." + suffix + "@ecopulse.com";
        String login = "maria." + suffix;
        String cpf = String.format("0%010d", suffix);

        User user = new User(
                null, "demo-001", "MariaPublic", login, null,
                "Maria da Silva", LocalDate.of(2001, 3, 15), cpf, "Brasileira", email, null, null, null);
        user = service.create(user, "SenhaSegura123");

        require(user.getId() != null, "CREATE falhou");
        System.out.println("CREATE: " + user.getId());
        require(service.findById(user.getId()).isPresent(), "READ falhou");
        System.out.println("READ: OK");
        user.setUsername("Maria EcoPulse");
        service.update(user);
        require(service.findById(user.getId()).orElseThrow().getUsername().equals("Maria EcoPulse"), "UPDATE falhou");
        System.out.println("UPDATE: OK");
        require(service.delete(user.getId()), "DELETE falhou");
        require(service.findById(user.getId()).isEmpty(), "DELETE não removeu");
        System.out.println("DELETE: OK");
        System.out.println("CRUD executado com sucesso.");
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
