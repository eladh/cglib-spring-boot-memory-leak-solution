package demo.cglibspringbootapp.entites;

public class Account {

    private int id;
    private String username;

    private String password;

    private Integer  role;

    private Person person;

    @Override
    public String toString() {
        return "Account{" +
            "id=" + id +
            ", username='" + username + '\'' +
            ", password='" + password + '\'' +
            ", role=" + role +
            ", person=" + person +
            '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return Role.getType(this.role);
    }

    public void setRole(Role role) {
        if (role == null) {
             this.role = null;
         } else {
             this.role = role.getId();
         }
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

}