import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class UsersFacade {
    private Map<String, User> allUsers;
    private static ReentrantLock l;
    private static ReentrantReadWriteLock lock;

    public UsersFacade(){
        this.allUsers = new HashMap<>();
        User user1 = new User("joao","joaopass");
        User user2 = new User("alex","alexpass");
        User user3 = new User("joel","joelpass");


        this.allUsers.put(user1.getUsername(),user1);
        this.allUsers.put(user2.getUsername(),user2);
        this.allUsers.put(user3.getUsername(),user3);
        l = new ReentrantLock();
        lock =  new ReentrantReadWriteLock();
    }

    // lock para questoes de segurança .caso 1 utilizador estiver a tentar a invadir uma conta de outro utilizador inserindo sempre passwords incorretas, pode acontecer
    // de no mesmo momento o utilizador dessa conta introduzir a sua pass correta e não conseguir entrar e o atacante que introduziu mal a palavra passe conseguiu entrar

    public boolean login(String username,String password){
        boolean b=false;
        try {
            System.out.println("locked?"+l.isLocked()+" "+username);
            l.lock();
            System.out.println("ENTREI NA SC-LOGIN");
            if (this.allUsers.get(username).getPassword().equals(password)) {
                b=true;
            }
            return b;
        }finally {
            l.unlock();
            System.out.println("libertei lock-LOGIN");
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
            System.out.println("locked?"+l.isLocked()+" "+username);
            l.lock();
            System.out.println("ENTREI NA SC-EXISTE");
            if (this.allUsers.containsKey(username)) {
                b=true;
            }
            return b;
        }finally {
            l.unlock();
            System.out.println("LIBERTEI LOCK-EXISTE");
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
