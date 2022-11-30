import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class UsersFacade {
    private Map<String, User> allUsers;
    ReentrantLock l = new ReentrantLock();

    public UsersFacade(UsersFacade usersFacade){
        this.allUsers= usersFacade.getAllUsers();
    }

    //cenario: 2 utilizadores autenticam-se ao mesmo tempo e possuem a mesma password. 1 deles autentica-se mal e outra autentica-se bem
    //pode acontecer de a conta que introduziu mal a password se autenticar com sucesso ->evitar com locks

    public boolean login(String username,String password){
        boolean b = false;
        try {
            l.lock();
            if (this.allUsers.containsKey(username) && this.allUsers.get(username).getPassword().equals(password)) {
                b=true;
            }
            return b;
        }finally {
            l.unlock();
        }
    }

    public Map<String,User> getAllUsers(){
        try{
            l.lock();
            return this.allUsers;
        }finally {
            l.unlock();
        }
    }

    public void writeUser(String path,User user) throws IOException{
        FileOutputStream f = new FileOutputStream(path);
        ObjectOutputStream on = new ObjectOutputStream(f);
        on.writeObject(user);
    }

    /**
    public UsersFacade getAllUsers(String path) throws IOException,ClassNotFoundException  {
        FileInputStream f = new FileInputStream(path);
        ObjectInputStream in = new ObjectInputStream(f);
        UsersFacade uf = (UsersFacade) in.readObject();
        in.close();
        return uf;
    }*/

}
