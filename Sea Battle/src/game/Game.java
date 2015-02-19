package game;
import players.PlayerComputer;
import players.PlayerHuman;
import java.util.Scanner;
import display.Display;
import java.io.*;

public class Game {
      
      protected PlayerHuman playerA;
      protected PlayerComputer playerB;
      protected int[][] boardA = new int[10][10];
      protected int[][] boardB = new int[10][10];
      protected int[][] hitsA = new int[10][10];
      protected int[][] hitsB = new int[10][10];
      
      Scanner scan = new Scanner(System.in); 
      Display display = new Display();
      
      // attributes needed as "control points" for loading or returning
      protected int stav;
      protected int situation;
      public int progress;
      public boolean suspendFlag = true;
      
      // attributes needed for loading a saved game
      private int savedStav = 0;
      private int savedSituation = 0;
      private int savedProgress = 0;
      private int[][] sBoardA = new int[10][10];
      private int[][] sBoardB = new int[10][10];
      private int[][] sHitsA = new int[10][10];
      private int[][] sHitsB = new int[10][10];
                             
     
      public Game() {
          
          this.playerA = new PlayerHuman(boardA,hitsA,scan,display);
          this.playerB = new PlayerComputer(boardB,hitsB,scan);
          this.playerA.myBoard = this.fillBeginningMassiveValues(this.playerA.myBoard);
          this.playerA.myHits = this.fillBeginningMassiveValues(this.playerA.myHits);
          this.playerB.myBoard = this.fillBeginningMassiveValues(this.playerB.myBoard);
          this.playerB.myHits = this.fillBeginningMassiveValues(this.playerB.myHits);
      }
      public  void setSuspendFlag(boolean newValue){
          this.suspendFlag = newValue;
      }
      
      public int getStav(){
          return(this.stav);
      }
      public int getSituation(){
          return(this.situation);
      }
      public int getProgress(){
          return(this.progress);
      }
      
      /**
       * 
       * Most important function.
       * Responsible for game process.
       * 
       * @param boardA
       * @param boardB
       * @param hitsA
       * @param hitsB
       * @param sDocksLeft
       * @param stav
       * @param situation
       * @param progress 
       */
    public void run(int[][] boardA, int[][] boardB,
            int[][] hitsA, int[][] hitsB, int[] sDocksLeft, int stav, int situation,
            int progress) {

        // these settings are needed only to avoid NullPointerException
        // when running the game after loading or returning

        if (boardA != null) {
            this.playerA.setMyBoard(boardA);
        }
        if (boardB != null) {
            this.playerB.setMyBoard(boardB);
        }
        if (hitsA != null) {
            this.playerA.myHits = hitsA;
        }
        if (hitsB != null) {
            this.playerB.myHits = hitsB;
        }
        if (sDocksLeft != null) {
            this.playerA.docksLeft = sDocksLeft;
        }
        this.stav = stav;
        this.situation = situation;
        this.progress = progress;

        this.display.actualizeDisplay(this.playerA.myBoard, this.playerA.myHits,
                this.playerB.myBoard, this.playerB.myHits);
        this.display.print();

        // PlayerHuman sets ships
        if (this.getProgress() <= 10) {
            for (int docks = 4; docks > 0; docks--) {
                if (!this.continueRunning()) {
                    break;
                }
                System.out.printf("\n**************************************************\n"
                        + "* Please choose your %d-dock-ship coordinates =)  *\n"
                        + "**************************************************\n", docks);
                this.playerA.setShipsOfType(docks);
            }
        }

        // PlayerComputer sets ships
        if (this.getProgress() == 10 && this.continueRunning()) {
            for (int docks = 4; docks > 0; docks--) {
                System.out.printf("\n********************************************************\n"
                        + "* Wait a bit, please. Computer's setting %d-dock-ships  *\n"
                        + "********************************************************\n", docks);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ie) {
                };
                this.playerB.setShipsOfType(docks);
            }
            this.progress++;
        }


        int[] hitHelp1 = new int[2];
        int[] hit = new int[2];
        int direction1 = 0;
        int direction2 = 1;

        // shooting cycle
        if (this.getProgress() == 11 && this.continueRunning()) {
            while (!playerA.isWinner() && !playerB.isWinner() && this.continueRunning()) {

                switch (this.stav) {
                    case 1: {
                        // PlayerHuman move
                        int[] square = this.playerA.move();
                        if (!this.continueRunning()) {
                            break;
                        }
                        int shipType = this.playerA.calculateUnknownShipType(square[0], square[1], this.playerB.myBoard);
                        this.playerA.controlShotResults(shipType, square, this.playerB.myBoard);
                        this.display.displayHit(this.playerA.myHits);
                        if (this.playerA.myHits[square[0]][square[1]] == 0) {
                            this.stav = 2;
                            break;
                        }
                    }
                    break;
                    case 2: {
                        // PlayerComputer move
                        hit = this.moveSwitcher(this.situation, direction1, direction2, hitHelp1);
                        playerB.controlShotResults(hit);
                        if (!this.continueRunning()) {
                            this.stav = 2;
                            break;
                        }

                        // if Computer has missed:
                        if (this.playerB.myHits[hit[0]][hit[1]] == 0) {
                            this.stav = 1;
                        } else {

                            // if Computer has killed:
                            if (this.playerB.myHits[hit[0]][hit[1]] == 2) {
                                playerB.replaceTouchedByKilled(hit, this.playerA.myBoard);
                                playerB.surroundKilledByMissed();
                                this.situation = 1;
                                hitHelp1 = hit;
                            } else {

                                // if it is Computer's second touch:
                                if (this.playerB.myHits[hit[0]][hit[1]] == 1 && this.playerB.myHits[hitHelp1[0]][hitHelp1[1]] == 1) {
                                    this.situation = 3;
                                    direction1 = calculateDirection(hitHelp1, hit);
                                    hitHelp1 = hit;
                                } else {

                                    // if it is a single touch
                                    this.situation = 2;
                                    hitHelp1 = hit;
                                }

                            }
                        }

                        display.displayOpponentHit(this.playerB.myHits);
                    }
                    break;
                    default:
                        break;
                }

                if (playerA.isWinner()) {
                    System.out.println("\n********************************************\n"
                            + "*  Congratulations! You've won this game!   *\n"
                            + "********************************************\n ");
                }
                if (playerB.isWinner()) {
                    System.out.println("\n**********************************************************************************\n"
                            + "* Unfortunately, I have won.You could play one more game and finally defeat me!   *\n"
                            + "**********************************************************************************\n");
                }
            }
        }

    }

    /**
     * Game saved to a user specified file.
     * 
     * @throws IOException 
     */
    public void saveGame() throws IOException {
        System.out.println("\n************************************************************************\n"
                + "* Please enter the name of a file where you want to save current game!  *\n"
                + "************************************************************************\n");
        String filename = scan.next();
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename));

        out.writeObject(this.boardA);
        out.writeObject(this.boardB);
        out.writeObject(this.hitsA);
        out.writeObject(this.hitsB);
        out.writeObject(this.playerA.docksLeft);
        out.writeInt(this.stav);
        out.writeInt(this.situation);
        out.writeInt(this.progress);
        out.close();
    }

    /**
     * First downloads input values needed to run(),
     * then calls this function with these parametres.
     * 
     * @throws IOException
     * @throws ClassNotFoundException 
     */
    public void loadGame() throws IOException, ClassNotFoundException {
        FileInputStream instream = null;
        System.out.println("\n*****************************************************************\n"
                + "* Please enter the name of gamefile which you want to download!  *\n"
                + "*****************************************************************\n");
        String filename = scan.next();
        try {
            instream = new FileInputStream(filename);
        } catch (FileNotFoundException e) {
            System.out.printf("File %s doesn't exist!\b", filename);
        }

        ObjectInputStream in = new ObjectInputStream(instream);

        this.sBoardA = (int[][]) in.readObject();
        this.sBoardB = (int[][]) in.readObject();
        this.sHitsA = (int[][]) in.readObject();
        this.sHitsB = (int[][]) in.readObject();
        this.playerA.sDocksLeft = (int[]) in.readObject();
        this.savedStav = in.readInt();
        this.savedSituation = in.readInt();
        this.savedProgress = in.readInt();
        in.close();

        this.run(this.sBoardA, this.sBoardB, this.sHitsA, this.sHitsB,
                this.playerA.sDocksLeft, this.savedStav, this.savedSituation, this.savedProgress);
    }

    /**
     * Function intended to switch between Computer move types.
     * 
     * @param stav
     * @param direction1
     * @param direction2
     * @param latterHit
     * @return 
     */
    public int[] moveSwitcher(int stav, int direction1, int direction2, int[] latterHit) {
        int[] square = new int[2];
        switch (stav) {
            case 1:
                square = playerB.randomMove();

                break;
            case 2:
                square = playerB.moveAround(latterHit);

                break;
            case 3:
                square = playerB.moveForwardIn(direction1, direction2, latterHit);

                break;
            default:
                break;

        }
        return (square);

    }

    /**
     * At the beginning all fields contain -1.
     * 
     * @param massive
     * @return 
     */
    private int[][] fillBeginningMassiveValues(int[][] massive) {
        for (int i = 0; i < massive.length; i++) {
            for (int j = 0; j < massive[0].length; j++) {
                massive[i][j] = -1;
            }
        }
        return (massive);
    }

    /**
     * Calculates upDown or leftRight direction1 
     * for Computer's moveForwardIn() function.
     * 
     * @param hit1
     * @param hit2
     * @return 
     */
    public int calculateDirection(int[] hit1, int[] hit2) {
        int direction = 0;
        if (hit1[0] == hit2[0]) {
            direction = 1;    // horizontal
        }
        if (hit1[1] == hit2[1]) {
            direction = 2;    // vertical
        }
        return (direction);
    }

    public boolean continueRunning() {
        boolean continueRunMethod = true;
        if (this.suspendFlag == false) {
            continueRunMethod = false;
        }
        return (continueRunMethod);
    }
}
