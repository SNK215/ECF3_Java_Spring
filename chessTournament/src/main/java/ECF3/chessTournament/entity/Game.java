package ECF3.chessTournament.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToMany(cascade = CascadeType.ALL)
    private List<User> userList;
    private boolean hasBeenPlayed;

    //result = "A" : userList.get(0) est le vainqueur
    //result = "B" : userList.get(1) est le vainqueur
    //result = "N" : match nul
    private String result;

    public Game(List<User> userList) {
        this.userList = userList;
        this.hasBeenPlayed = false;
        this.result = "";
    }
}
