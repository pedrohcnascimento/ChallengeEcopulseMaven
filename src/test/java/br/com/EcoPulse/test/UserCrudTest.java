package br.com.EcoPulse.test;

import br.com.EcoPulse.config.ConnectionFactory;
import br.com.EcoPulse.domain.User;
import br.com.EcoPulse.service.UserService;

/** Classe executável de demonstração e teste manual do CRUD, conforme o enunciado. */
public final class UserCrudTest {
    private UserCrudTest() { }
    public static void main(String[] args) {
        ConnectionFactory.initializeDatabase();
        UserService service = new UserService();
        String email = "teste." + System.currentTimeMillis() + "@ecopulse.com";
        User user = service.create(new User(null, "demo-001", "Maria Silva", email, null, null));
        require(user.getId() != null, "CREATE falhou"); System.out.println("CREATE: " + user.getId());
        require(service.findById(user.getId()).isPresent(), "READ falhou"); System.out.println("READ: OK");
        user.setUsername("Maria EcoPulse"); service.update(user); require(service.findById(user.getId()).orElseThrow().getUsername().equals("Maria EcoPulse"), "UPDATE falhou"); System.out.println("UPDATE: OK");
        require(service.delete(user.getId()), "DELETE falhou"); require(service.findById(user.getId()).isEmpty(), "DELETE não removeu"); System.out.println("DELETE: OK");
        System.out.println("CRUD executado com sucesso.");
    }
    private static void require(boolean condition, String message) { if (!condition) throw new AssertionError(message); }
}
