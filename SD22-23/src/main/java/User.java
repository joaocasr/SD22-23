import java.io.*;

public class User implements Serializable {
    private String username;
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(User u){
        this.username = u.getUsername();
        this.password = u.getPassword();
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


    void serialize (DataOutputStream out) throws IOException {
        out.writeUTF(this.username);
        out.writeUTF(this.password);
        out.writeUTF(";");
    }

    static User deserialize (DataInputStream in) throws IOException {
        String username = in.readUTF();
        String password = in.readUTF();
        boolean logged = in.readBoolean();
        return new User(username,password);
    }

}
