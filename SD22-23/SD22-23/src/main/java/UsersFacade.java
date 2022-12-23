import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class UsersFacade {
    private Map<String, User> allUsers;
    ReentrantLock l = new ReentrantLock();

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
            l.lock();
            if (this.allUsers.get(username).getPassword().equals(password)) {
                b=true;
            }
            return b;
        }finally {
            l.unlock();
        }
    }

    public boolean registarUser(String username,String password){
        boolean b = false;
        try {
            l.lock();
            if(!(this.existeUser(username)))
            {
                this.allUsers.put(username,new User(username,password));
                b=true;
            }
            return b;
        }finally {
            l.unlock();
        }
    }

    public boolean existeUser(String username){
        boolean b = false;
        try {
            l.lock();
            if (this.allUsers.containsKey(username)) {
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
