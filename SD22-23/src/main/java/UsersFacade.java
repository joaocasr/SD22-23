import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class UsersFacade {
    private Map<String, User> allUsers;
    ReentrantLock l = new ReentrantLock();

    public UsersFacade(){
        this.allUsers = new HashMap<>();
        User user = new User("joao","joaopass",false);
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

    public void getUsers (DataOutputStream out) throws IOException {
        out.writeInt(this.allUsers.values().size());
        for(User user : allUsers.values()) {
            user.serialize(out);
            out.flush();
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
        dataOutputStream.writeUTF(u.getUsername());
        dataOutputStream.writeUTF(u.getPassword());
        dataOutputStream.writeBoolean(u.isLogged());
        dataOutputStream.writeUTF(";");
    }

    public void carregarUsers(User u) throws IOException {
        //DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream("users.bin")));

    }

    public boolean criarUser(String username,String password){
        boolean b = false;


        try {
            l.lock();
            if(!(this.allUsers.containsKey(username)))
            {
                this.allUsers.put(username,new User(username,password));
                b=true;
            }
            return b;
        }finally {
            l.unlock();
        }
    }

    public boolean removerUser(String username,String password){
        boolean b = false;

        if (!(existeUser(username))) return false;

        try {
            l.lock();
            if((this.allUsers.containsKey(username)))
            {
                this.allUsers.remove(username);
                b=true;
            }
            return b;
        }finally {
            l.unlock();
        }
    }

}
