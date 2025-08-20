package edu.jose.vazquez.process;
import edu.jose.vazquez.models.Game;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class GameManager {
    private HashMap<String, Game> games;
    private Stack<Game> gameHistory;
    private Queue<Game> updateQueue;

    public GameManager() {
        this.games = new HashMap<>();
        this.gameHistory = new Stack<>();
        this.updateQueue = new LinkedList<>();
        addGame("Persona 5", "JRPG", 2016);
        addGame("The Legend of Zelda: Breath of the Wild", "Action-Adventure", 2017);
        addGame("Super Mario Odyssey", "Platformer", 2017);
        addGame("Dark Souls", "Action RPG", 2011);
        addGame("The Witcher 3: Wild Hunt", "Action RPG", 2015);
        addGame("Overwatch", "First-Person Shooter", 2016);
        addGame("Minecraft", "Sandbox", 2011);
        addGame("Stardew Valley", "Simulation RPG", 2016);
        addGame("Celeste", "Platformer", 2018);
        addGame("Hollow Knight", "Metroidvania", 2017);
    }

    public void addGame(String name, String genre, int releaseYear) {
        Game newGame = new Game(name, genre, releaseYear);
        games.put(name, newGame);
        gameHistory.push(newGame);
    }

    public void removeGame(String name) {
    String keyToRemove = null;
    for (String key : games.keySet()) {
        if (key.equalsIgnoreCase(name)) {
            keyToRemove = key;
            System.out.println("╔══════════════════════════════════════════════════╗");
            System.out.println("║        ✅ JUEGO ELIMINADO EXITOSAMENTE  ✅      ║");
            System.out.println("╚══════════════════════════════════════════════════╝");
            System.out.println("  ♦ Nombre: " + keyToRemove);
            break;
        }
    }

    if (keyToRemove == null) {
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║          ❌   JUEGO NO ENCONTRADO  ❌           ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
        return;
    }
    Game removedGame = games.get(keyToRemove);
    gameHistory.push(removedGame);
    games.remove(keyToRemove);
    }

    public Game getGames(String name) {
        return games.get(name);
    }

public void updateGame(String name, String genre, int releaseYear) {
    String keyToUpdate = null;
    for (String key : games.keySet()) {
        if (key.equalsIgnoreCase(name)) {
            keyToUpdate = key;
            break;
        }
    }

    if (keyToUpdate != null) {
        Game game = games.get(keyToUpdate);
        game.setGenre(genre);
        game.setReleaseYear(releaseYear);
        updateQueue.add(game); 
        //System.out.println("Actualización programada para: " + game.getName());
    } 
}
public void searchGame(String name) {
    Game foundGame = null;

    for (String key : games.keySet()) {
        if (key.equalsIgnoreCase(name)) {
            foundGame = games.get(key);
            break;
        }
    }

    if (foundGame != null) {
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║            ✅ JUEGO ENCONTRADO  ✅              ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
        System.out.println("  ♦ Nombre: " + foundGame.getName());
        System.out.println("  ♦ Género: " + foundGame.getGenre());
        System.out.println("  ♦ Año de lanzamiento: " + foundGame.getReleaseYear());
    } else {
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║           ❌  JUEGO NO ENCONTRADO  ❌           ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
    }
}

    public void listGames() {
        if (games.isEmpty()) {
            System.out.println("❌ No hay juegos disponibles. ❌");
        } else {
            games.values().forEach(game -> System.out.println("═════════════════════════════════" + "\n"+"  ♦ Nombre: " + game.getName() + "\n" + "  ♦ Género: " + game.getGenre() + "\n" + "  ♦ Año de lanzamiento: " + game.getReleaseYear() + "\n" + "═════════════════════════════════"));
        }
    }

    public void sortGamesByName() {
        games.values().stream()
            .sorted(Comparator.comparing(Game::getName))
            .forEach(game -> System.out.println(("═════════════════════════════════" + "\n"+"  ♦ Nombre: " + game.getName() + "\n" + "  ♦ Género: " + game.getGenre() + "\n" + "  ♦ Año de lanzamiento: " + game.getReleaseYear() + "\n" + "═════════════════════════════════")));
    }

    public void sortGamesByGenre() {
        games.values().stream()
            .sorted(Comparator.comparing(Game::getGenre))
            .forEach(game -> System.out.println(("═════════════════════════════════" + "\n"+"  ♦ Nombre: " + game.getName() + "\n" + "  ♦ Género: " + game.getGenre() + "\n" + "  ♦ Año de lanzamiento: " + game.getReleaseYear() + "\n" + "═════════════════════════════════")));
    }


    public void sortGamesByReleaseYear() {
        games.values().stream()
            .sorted(Comparator.comparingInt(Game::getReleaseYear))
            .forEach(game -> System.out.println("═════════════════════════════════"+"\n"+game.getName() + "\n" + game.getReleaseYear()+"\n"+"═════════════════════════════════"));
    }

    public Game getGameIgnoreCase(String name) {
    if (name == null) return null;
    for (String key : games.keySet()) {
        if (key.equalsIgnoreCase(name)) {
            return games.get(key);
        }
    }
    return null;
}

    public void undoLastAction() {
        if (!gameHistory.isEmpty()) {
            Game lastGame = gameHistory.pop();
            if (games.containsKey(lastGame.getName())) {
                // Si existe, significa que fue agregado antes, ahora lo eliminamos
                games.remove(lastGame.getName());
                System.out.println("Deshacer acción: eliminado " + lastGame.getName());
            } else {
                // Si no existe, significa que fue eliminado antes, ahora lo agregamos de nuevo
                games.put(lastGame.getName(), lastGame);
                System.out.println("Deshacer acción: agregado " + lastGame.getName());
            }
        } else {
            System.out.println("No hay acciones para deshacer.");
        }
    }

    public void processNextUpdate() {
        if (!updateQueue.isEmpty()) {
            Game nextGame = updateQueue.poll();
            System.out.println("Procesando actualización de: " + nextGame.getName() + " -> Género: " + nextGame.getGenre() + ", Año: " + nextGame.getReleaseYear());
        } else {
            System.out.println("No hay actualizaciones pendientes.");
        }
    }
    
}
