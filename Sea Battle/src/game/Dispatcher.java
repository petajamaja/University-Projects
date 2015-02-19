package game;
import display.Menu;
import display.Menu.*;
import java.io.IOException;
import java.util.Scanner;
import game.Game.*;

/**
 * Class for enabling user choice.
 * Contents textual menu based on automatical switcher.
 */
public class Dispatcher extends Game {

    private int option = 0;
    private int loadOrStart = 0;
    public static Game battle;

    public void setOption(int option) {
        this.option = option;
    }

    public int getOption() {
        return (this.option);
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Dispatcher gameInterface = new Dispatcher();
        gameInterface.nonStaticMainSubstitute();
    }

    /**
     * Method needed to enable calling non-static methods.
     * 
     * @throws IOException
     * @throws ClassNotFoundException 
     */
    public void nonStaticMainSubstitute() throws IOException, ClassNotFoundException {

        Scanner scan = new Scanner(System.in);
        display.Menu menu = new display.Menu();

        while (getOption() != 5) {
            switch (getOption()) {
                // go to menu;
                case 0: {
                    if (!(battle == null)) {
                        battle.setSuspendFlag(true);
                    }
                    Menu.displayMenu();
                    this.setOption(scan.nextInt());
                }
                break;

                case 1: 
                // run new game or load game (depend on loadOrStart value);
                // the only place where Game class instance is created.
                {
                    battle = new Game();
                    if (this.loadOrStart == 0) {
                        battle.run(null, null, null, null, null, 1, 1, 0);
                    } else {
                        battle.loadGame();
                    }
                    this.loadOrStart = 0;
                    this.setOption(0);
                }
                break;

                case 2: {
                    // initial step to load a game
                    this.loadOrStart = 1;
                    this.setOption(1);
                }
                break;
                case 3: {   
                    // save game
                    battle.saveGame();
                    this.setOption(5);

                }
                break;
                case 4: {
                    // returning to current game
                    battle.setSuspendFlag(true);
                    battle.run(battle.playerA.myBoard, battle.playerB.myBoard,
                            battle.playerA.myHits, battle.playerB.myHits, battle.playerA.docksLeft,
                            battle.getStav(), battle.getSituation(), battle.getProgress());
                    this.setOption(0);
                }
                break;
                case 5:
                    // terminates the program
                    break;

                default:
                    this.setOption(0);
                    break;
            }
        }
    }
}
