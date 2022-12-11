import java.io.*;
import java.util.concurrent.locks.ReentrantLock;

public class User implements Serializable {
    private String username;
    private String password;
    ReentrantLock l = new ReentrantLock();

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(User u){
        this.username = u.getUsername();
        this.password = u.getPassword();
    }

    public String getUsername() {
        try {
            l.lock();
            return username;
        }finally {
            l.unlock();
        }
    }

    public void setUsername(String username) {
        try {
            l.lock();
            this.username = username;
        }finally {
            l.unlock();
        }
    }

    public String getPassword() {
        try {
            l.lock();
            return password;
        }finally {
            l.unlock();
        }
    }

    public void setPassword(String password) {
        try {
            l.lock();
            this.password = password;
        }finally {
            l.unlock();
        }
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
