package demo.cglibspringbootapp.entites;

public enum Role {
    ADMIN(1),
    SUPER_USER(2),
    MODERATOR(3),
    USER(4);

    private int id;

    Role(int id) {
        this.id = id;
    }

    public static Role getType(Integer id) {
        if (id == null) {
            return null;
        }

        for (Role role : Role.values()) {
            if (id.equals(role.getId())) {
                return role;
            }
        }
        throw new IllegalArgumentException("No matching type for id " + id);
    }

    public int getId() {
        return id;
    }
}