
public class UserServiceImpl implements UserService {

    @Override
    @Audit(operation = "CREATE_USER", critical = true)
    public void createUser(String email) {
        System.out.println("[UserService] Tworzenie użytkownika: " + email);
    }

    @Override
    @Audit(operation = "LIST_USERS")
    public void listUsers() throws InterruptedException {
        Thread.sleep(1000);
        System.out.println("[UserService] Pobieranie listy użytkowników");
    }

    @Override
    public void healthCheck() {
        System.out.println("[UserService] OK");
    }
}