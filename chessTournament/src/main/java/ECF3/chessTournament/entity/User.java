package ECF3.chessTournament.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String firstname;
    private String lastname;
    private String username;
    private String password;
    private boolean isAdmin;
    private int score;
    @ManyToMany
    private List<Game> gameList;

    public User(String firstname, String lastname, String username, String password, boolean isAdmin, List<Game> gameList) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.password = password;
        this.isAdmin = isAdmin;
        this.score = 0;
        this.gameList = gameList;
    }
}
