package vitmo.entities;

import javax.persistence.*;

@Entity
@Table(name="posts")
public class Post{
    private int id;
    private String name;
    private String content;
    private User user;

    public Post() {
    }

    public Post(String name, String content){
        this.name = name;
        this.content = content;
    }

    public Post(String name, String content, User user){
        this.name = name;
        this.content = content;
        this.user = user;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinTable(name = "users_posts", joinColumns = @JoinColumn(name = "post_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"))
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return String.format("Post [id=%d, name='%s']%n", id, name);
    }
}