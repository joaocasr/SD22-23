import java.io.Serializable;
import java.util.concurrent.locks.ReentrantLock;

public class User implements Serializable {
    private String username;
    private String password;
    private boolean logged;
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

    public boolean isLogged() {
        try {
            l.lock();
            return logged;
        }finally {
            l.unlock();
        }
    }

    public void setLogged(boolean logged) {
        try {
            l.lock();
            this.logged = logged;
        }finally {
            l.unlock();
        }
    }
}
