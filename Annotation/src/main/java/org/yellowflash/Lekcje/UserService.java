public interface UserService {

    void createUser(String email);

    void listUsers() throws InterruptedException;

    void healthCheck();
}