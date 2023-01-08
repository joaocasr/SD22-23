import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class UsersFacade {
    private Map<String, User> allUsers;
    ReadWriteLock l = new ReentrantReadWriteLock();

    public UsersFacade(){
        this.allUsers = new HashMap<>();
        User user = new User("joao","joaopass");
        this.allUsers.put(user.getUsername(),user);
    }

    //cenario: 2 utilizadores autenticam-se ao mesmo tempo e possuem a mesma password. 1 deles autentica-se mal e outra autentica-se bem
    //pode acontecer de a conta que introduziu mal a password se autenticar com sucesso ->evitar com locks

    public boolean login(String username,String password){
        boolean b = false;
        try {
            l.readLock().lock();
            if (this.allUsers.get(username).getPassword().equals(password)) {
                b=true;
            }
            return b;
        }finally {
            l.readLock().unlock();
        }
    }

    public boolean registarUser(String username,String password){
        boolean b = false;
        try {
            l.writeLock().lock();
            if(!(this.allUsers.containsKey(username)))
            {
                this.allUsers.put(username,new User(username,password));
                b=true;
            }
            return b;
        }finally {
            l.writeLock().unlock();
        }
    }

    public boolean existeUser(String username){
        boolean b = false;
        try {
            l.readLock().lock();
            if (this.allUsers.containsKey(username)) {
                b=true;
            }
            return b;
        }finally {
            l.readLock().unlock();
        }
    }

    public Map<String,User> getAllUsers(){
        try{
            l.readLock().lock();
            return this.allUsers;
        }finally {
            l.readLock().unlock();
        }
    }

    public void guardarUser(User u) throws IOException {
        DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream("users.bin")));
        u.serialize(dataOutputStream);
    }

    public void carregarUsers() throws IOException {
        DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream("users.bin")));
        while (dataInputStream.read()!=-1){
            String username = dataInputStream.readUTF();
            String passsword = dataInputStream.readUTF();
            String delimiter = dataInputStream.readUTF();
            User u = new User(username,passsword);
            this.allUsers.put(username,u);
        }
    }

}
