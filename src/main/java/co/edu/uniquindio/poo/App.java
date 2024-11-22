package co.edu.uniquindio.poo;

import java.util.ArrayList;
import java.util.List;

interface Command {
    void execute();
    void undo();
}

// --- CLASE BASE PARA LOS PERSONAJES ---
abstract class Character implements Observer, Subject {
    private String name;
    private int health;
    private boolean isAttacking;
    private List<Observer> observers;
    private List<Command> history; // Historial de comandos

    public Character(String name, int health) {
        this.name = name;
        this.health = health;
        this.isAttacking = false;
        this.observers = new ArrayList<>();
        this.history = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
        notifyObservers(); // Notifica cambios en la salud
    }

    public boolean isAttacking() {
        return isAttacking;
    }

    public void setAttacking(boolean attacking) {
        isAttacking = attacking;
        notifyObservers(); // Notifica cambios en el estado de ataque
    }

    public void executeCommand(Command command) {
        command.execute();
        history.add(command); // Agregar al historial
    }

    public void undoLastCommand() {
        if (!history.isEmpty()) {
            Command command = history.remove(history.size() - 1);
            command.undo();
        }
    }

    public void showHistory() {
        System.out.println("Historial de comandos de " + name + ":");
        for (Command command : history) {
            System.out.println("- " + command.getClass().getSimpleName());
        }
    }

    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update();
        }
    }

    @Override
    public abstract void update(); // Cada tipo de personaje define su reacción
    
        public abstract void handleRequest(String request);
    }
    
    // --- CLASES ESPECÍFICAS PARA COMMAND ---
    class AttackCommand implements Command {
        private Character target;
    
        public AttackCommand(Character target) {
            this.target = target;
        }
    
        @Override
        public void execute() {
            System.out.println("Ejecutando ataque contra " + target.getName());
            target.setHealth(target.getHealth() - 20); // Reduce salud
        }
    
        @Override
        public void undo() {
            System.out.println("Deshaciendo ataque contra " + target.getName());
            target.setHealth(target.getHealth() + 20); // Restaura salud
        }
    }
    
    class HealCommand implements Command {
        private Character target;
    
        public HealCommand(Character target) {
            this.target = target;
        }
    
        @Override
        public void execute() {
            System.out.println("Ejecutando curación para " + target.getName());
            target.setHealth(target.getHealth() + 30); // Aumenta salud
        }
    
        @Override
        public void undo() {
            System.out.println("Deshaciendo curación para " + target.getName());
            target.setHealth(target.getHealth() - 30); // Reduce salud
        }
    }
    
    // --- CLASE PARA EL ESTADO DEL CAMPO DE BATALLA ---
    class Battlefield implements Subject {
        private String state;
        private List<Observer> observers;
    
        public Battlefield() {
            this.state = "Estable"; // Estado inicial
            this.observers = new ArrayList<>();
        }
    
        public String getState() {
            return state;
        }
    
        public void setState(String state) {
            this.state = state;
            notifyObservers(); // Notifica cambios en el estado del campo de batalla
        }
    
        @Override
        public void addObserver(Observer observer) {
            observers.add(observer);
        }
    
        @Override
        public void removeObserver(Observer observer) {
            observers.remove(observer);
        }
    
        @Override
        public void notifyObservers() {
            for (Observer observer : observers) {
                observer.update();
            }
        }
    }
    
    // --- CLASES ESPECÍFICAS PARA LOS PERSONAJES ---
    class Healer extends Character {

        private Character nextHandler;

        public Healer(String name, int health) {
            super(name, health);
        }
    
        @Override
        public void update() {
            if (getHealth() < 30) {
                System.out.println("Sanador " + getName() + ": Detecta salud crítica. Preparando curación.");
            }
        }

        @Override
        public void handleRequest(String request) {
            if ("Curacion ".equalsIgnoreCase(request)|| "Asistencia".equalsIgnoreCase(request)) {
                System.out.println("Sanador: " + getName() + ": Maneja la solicitud de Curacion O Asistencia.");
            } else if (nextHandler != null) {
                nextHandler.handleRequest(request);
        }
        }
        public void setNextHandler(Character nextHandler) {
            this.nextHandler = nextHandler;
        }
    }
    
    class Mage extends Character {
        private Character nextHandler;

        public Mage(String name, int health) {
            super(name, health);
        }
    
        @Override
        public void update() {
            if (getHealth() < 30) {
                System.out.println("Mago " + getName() + ": Detecta salud crítica. Preparando hechizo de protección.");
            }
        }

        @Override
        public void handleRequest(String request) {
            if ("Ataque Magico".equalsIgnoreCase(request)) {
                System.out.println("Mago " + getName() + ": Maneja la solicitud de ataque Magico.");
            } else if (nextHandler != null) {
                nextHandler.handleRequest(request);
        }
        }
        public void setNextHandler(Character nextHandler) {
            this.nextHandler = nextHandler;
        }
    }
    
    class Warrior extends Character {
        private Character nextHandler; // Para la cadena de responsabilidad
    
        public Warrior(String name, int health) {
            super(name, health);
        }
    
        public void setNextHandler(Character nextHandler) {
            this.nextHandler = nextHandler;
        }
    
        public void handleRequest(String request) {
            if ("ataque físico".equalsIgnoreCase(request)) {
                System.out.println("Guerrero " + getName() + ": Maneja la solicitud de ataque físico.");
            } else if (nextHandler != null) {
                nextHandler.handleRequest(request);
        }
    }

    @Override
    public void update() {
        if (isAttacking()) {
            System.out.println("Guerrero " + getName() + ": Detecta ataque. Preparando defensa.");
        }
    }
}

// --- INTERFACES Y CLASES PARA OBSERVER ---
interface Observer {
    void update();
}

interface Subject {
    void addObserver(Observer observer);
    void removeObserver(Observer observer);
    void notifyObservers();
}
// --- Interfaces para estrategias ---
interface CombatStrategy {
    void execute(Character character);
}

interface SpellStrategy {
    void cast(Character character);
}

// --- Estrategias concretas para guerreros ---
class OffensiveCombat implements CombatStrategy {
    @Override
    public void execute(Character character) {
        System.out.println(character.getName() + " adopta una estrategia ofensiva y ataca con fuerza.");
    }
}

class DefensiveCombat implements CombatStrategy {
    @Override
    public void execute(Character character) {
        System.out.println(character.getName() + " adopta una estrategia defensiva y se prepara para bloquear ataques.");
    }
}

// --- Estrategias concretas para magos ---
class FireSpell implements SpellStrategy {
    @Override
    public void cast(Character character) {
        System.out.println(character.getName() + " lanza un hechizo de fuego contra el enemigo.");
    }
}

class ShieldSpell implements SpellStrategy {
    @Override
    public void cast(Character character) {
        System.out.println(character.getName() + " conjura un hechizo de escudo para protegerse.");
    }
}

// --- Extensiones en los personajes ---
class StrategistWarrior extends Warrior {
    private CombatStrategy combatStrategy;

    public StrategistWarrior(String name, int health) {
        super(name, health);
    }

    public void setCombatStrategy(CombatStrategy strategy) {
        this.combatStrategy = strategy;
    }

    public void executeCombatStrategy() {
        if (combatStrategy != null) {
            combatStrategy.execute(this);
        } else {
            System.out.println(getName() + " no tiene una estrategia de combate seleccionada.");
        }
    }
}

class StrategistMage extends Mage {
    private SpellStrategy spellStrategy;

    public StrategistMage(String name, int health) {
        super(name, health);
    }

    public void setSpellStrategy(SpellStrategy strategy) {
        this.spellStrategy = strategy;
    }

    public void castSpell() {
        if (spellStrategy != null) {
            spellStrategy.cast(this);
        } else {
            System.out.println(getName() + " no tiene un hechizo seleccionado.");
        }
    }
}

// --- CLASE PRINCIPAL ---
public class App {
    public static void main(String[] args) {

        
        // Crear personajes y campo de batalla
        Character healer = new Healer("Sanador", 100);
        Character mage = new Mage("Mago", 80);
        Character warrior = new Warrior("Guerrero", 120);
        Battlefield battlefield = new Battlefield();

        // Configurar observadores
        healer.addObserver(healer);
        mage.addObserver(mage);
        warrior.addObserver(warrior);

        battlefield.addObserver(healer);
        battlefield.addObserver(mage);
        battlefield.addObserver(warrior);

        // Configurar cadena de responsabilidad
        ((Warrior) warrior).setNextHandler(healer);
        healer.addObserver(mage);

        // Simular eventos
        System.out.println("--- Simulación: Observer y Chain of Responsibility ---");
        warrior.handleRequest("ataque físico");
        healer.setHealth(25); // Salud crítica
        battlefield.setState("Peligroso");

        System.out.println("\n--- Simulación: Command ---");
        Command attack = new AttackCommand(healer);
        warrior.executeCommand(attack);
        healer.showHistory();
        warrior.undoLastCommand();

        StrategistWarrior warrior2 = new StrategistWarrior("Guerrero Estratégico", 120);
        StrategistMage mage2= new StrategistMage("Mago Estratégico", 80);

        // Seleccionar estrategias para el guerrero
        System.out.println("--- Estrategias de Guerrero ---");
        warrior2.setCombatStrategy(new OffensiveCombat());
        warrior2.executeCombatStrategy();

        warrior2.setCombatStrategy(new DefensiveCombat());
        warrior2.executeCombatStrategy();

        // Seleccionar hechizos para el mago
        System.out.println("\n--- Hechizos de Mago ---");
        mage2.setSpellStrategy(new FireSpell());
        mage2.castSpell();

        mage2.setSpellStrategy(new ShieldSpell());
        mage2.castSpell();
    }
}
    
    

