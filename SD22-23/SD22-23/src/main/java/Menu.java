import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Menu {
    public interface Handler {
        public void execute() throws InterruptedException;
    }


    public interface PreCondition {
        public boolean validate();
    }

    private static Scanner scanner = new Scanner(System.in);
    private List<String> options;
    private List<Handler> handlers;
    private List<PreCondition> preC;
    private boolean exit;

    public Menu() {
        this.options = new ArrayList<>();
        this.handlers = new ArrayList<>();
        this.preC = new ArrayList<>();
        this.exit = false;
    }

    public Menu(List<String> options) {
        this.options = options;
        this.handlers = new ArrayList<>();
        this.preC = new ArrayList<>();
        this.exit = false;

        for (String option : this.options) {
            this.preC.add(() -> false);
            this.handlers.add(() -> System.out.println("\nOpção não implementada!"));
        }
    }

    // Setters
    public void setHandlers(int opt, Handler handler) {
        this.handlers.set(opt - 1, handler);
    }

    public void setPreCondition(int opt, PreCondition preC) {
        this.preC.set(opt - 1, preC);
    }

    public void setOptions(List<String> options) {
        this.options = new ArrayList<>(options);
        this.preC = new ArrayList<>();
        this.handlers = new ArrayList<>();

        for (String option : this.options) {
            this.preC.add(() -> true);
            this.handlers.add(() -> System.out.print("\nOpção não implementada!"));
        }
    }

    public void setExit(boolean exit) {
        this.exit = exit;
    }

    // Método: Adiciona um handler
    public void addHandler(int opt, Handler handler) {
        this.handlers.add(opt - 1, handler);
    }

    // Método: Execução do menu
    public void run(int x) throws InterruptedException {
        int option;
        do {
            show();
            option = readOption();

            if (option > 0 && !this.preC.get(option - 1).validate())
                System.out.print("\nOpção indisponível!");
            else if (option > 0) {
                this.handlers.get(option - 1).execute();
            }
            if(x==1){
                this.handlers.get(5).execute();
                this.handlers.get(6).execute();
            }
        } while (this.exit == false);
    }


    // Método: Apresentar todas as opções do menu
    public void show() {
        System.out.println("********************* GESTÃO DE FROTA DE TROTINETES- Braga *********************");
        for (int i = 0; i < this.options.size(); i++) {
            if(i==5 || i==6){}
            else if(i != (this.options.size()-1)) {
                System.out.print("| * ");
                System.out.print(i + 1);
                System.out.print(" -");
                System.out.print(this.options.get(i));
            }
            if(i == (this.options.size()-1)){
                if(i!=6) System.out.print("| * ");
                if(i!=6) System.out.print(i + 1);
                if(i!=6) System.out.print(" -");
                System.out.print(this.options.get(i));
                System.out.print("\n_______________________________________________________________________________");
            }
        }
    }

    public int readOption() {
        int option;

        System.out.print("\n>>> ");
        try {
            String optionST = scanner.nextLine();
            option = Integer.parseInt(optionST);
        } catch (NumberFormatException e) {
            option = -1;
        }

        if (option < 0 || option > this.options.size()) {
            System.out.print("\nOpção inválida! Tente outra vez...\n");
            option = -1;
        }

        return option;
    }
}