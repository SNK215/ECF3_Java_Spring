package ECF3.chessTournament.Repository;

import ECF3.chessTournament.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
    public User findByUsername(String username);

    public User findByUsernameAndPassword(String username, String password);

    public User findById(int id);

}
