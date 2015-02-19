package players;
import java.util.Scanner;
import display.Display;
import game.Dispatcher.*;
import game.Game.*;

public class PlayerHuman extends Player{
    
    private Display display;
    public int[] docksLeft = {4, 3, 2, 1};
    public int[] sDocksLeft = new int[4];

    public PlayerHuman(int[][] myBoard, int[][] myHits, Scanner scan, Display display) {
        super(myBoard, myHits, scan);
        this.display = display;
    }

    public void setShipsOfType(int numberOfDocks) {

        String a;
        for (int counter = 5 - numberOfDocks - this.docksLeft[numberOfDocks - 1]; counter < 5 - numberOfDocks; counter++) {

            System.out.println("For EXIT to main menu press 'e' . For CONTINUE press ANY OTHER KEY .");

            a = scan.next();
            if (a.equals("e")) {
                game.Dispatcher.battle.setSuspendFlag(false);
                break;
            } else {
                game.Dispatcher.battle.progress++;
                System.out.printf("***************\n"
                        + "* Ship № %d   *\n"
                        + "***************\n", counter + 1);
                System.out.println("********************************************************\n"
                        + "* So where does your ship begin and where does it end? *\n"
                        + "********************************************************\n");

                do {
                    System.out.print(this.message);
                    do {
                        System.out.print(validityMessage);
                        this.beginningCoordinate[0] = setCoordinate(0);
                        this.beginningCoordinate[1] = setCoordinate(1);
                    } while (!this.controlValidityOfCoordinate(this.beginningCoordinate));
                    do {
                        System.out.print(validityMessage);
                        this.endCoordinate[0] = setCoordinate(0);
                        this.endCoordinate[1] = setCoordinate(1);
                    } while (!this.controlValidityOfCoordinate(this.endCoordinate));
                    this.exchangeBeginningAndEndCoordinates();
                } while (!this.controlWholeShipPosition(numberOfDocks, this.beginningCoordinate, this.endCoordinate));

                for (int i = this.beginningCoordinate[0]; i <= this.endCoordinate[0]; i++) {
                    for (int j = this.beginningCoordinate[1]; j <= this.endCoordinate[1]; j++) {
                        this.myBoard[i][j] = 1;
                    }
                }
                this.display.displayShip(this.myBoard);
                this.docksLeft[numberOfDocks - 1]--;

            }
        }
    }
    
    public int setCoordinate(int j) {
        int coordinate;
        while (true) {
            while (!this.scan.hasNextInt()) {
                System.out.println("Please enter a number!\b");
                scan.next();
            }
            coordinate = this.scan.nextInt();
            if (coordinate >= 0 && coordinate <= 9) {
                break;
            } else {
                System.out.println("Please enter a positive number not larger than 9!\b");
            }

        }
        return (coordinate);

    }
    
    public int[] move() {
        String a;
        int[] square = new int[2];
        System.out.println("For EXIT to main menu press 'e' . For CONTINUE press ANY OTHER KEY.");
        a = scan.next();
        if (a.equals("e")) {
            game.Dispatcher.battle.setSuspendFlag(false);
        } else {
            System.out.println("\n************************************************\n"
                    + "* Please enter the coordinates of a square      *\n"
                    + "* where you suppose to find your rival's ship.  *\n"
                    + "************************************************\n");
            game.Dispatcher.battle.setSuspendFlag(true);
            while (true) {
                System.out.println("RAW: ");
                int coordinate1 = this.setCoordinate(0);
                System.out.println("COLUMN: ");
                int coordinate2 = this.setCoordinate(1);
                square[0] = coordinate1;
                square[1] = coordinate2;

                if (!this.haveIShotThereBefore(square)) {
                    break;
                } else {
                    System.out.println("You have already shot there!\b");
                }
            }
        }
        return (square);

    }

    /**
     * Method checks if PlayerHuman killed/touched/missed.
     * It' an alternative of Computer's answer.
     * Checks all the fields around latterHit.
     * 
     * @param shipType
     * @param hit
     * @param rivalBoard 
     */
    public void controlShotResults(int shipType, int[] hit, int[][] rivalBoard) {
        if (rivalBoard[hit[0]][hit[1]] == -1) {
            this.setMyHitResult(hit, 0);
        } else {
            this.setMyHitResult(hit, 1);
            if (this.controlKilledTouched(hit, shipType, rivalBoard) == 'T') {
                this.setMyHitResult(hit, 1);

            } else {
                this.setMyHitResult(hit, 2);
                this.replaceTouchedByKilled(hit, rivalBoard);


            }

        }
    }

    /**
     * Function needed to detect kill-values.
     * In all directions checks for fields with rival's ship.
     * Check is held until the first empty field.
     * Then coincidences are counted.
     * If the number of coincidences equals the number of docks
     * (that have been counted by calculateUnknownShipType())
     * then the ship is killed.
     * 
     * @param square
     * @param shipType
     * @param rivalBoard
     * @return 
     */
    public char controlKilledTouched(int[] square, int shipType, int[][] rivalBoard) {
        int x = square[0];
        int y = square[1];
        int increment;
        int decrement;
        int coincidenceCounter = 1;
        int direction = 1;


        switch (direction) {

            case 1:
                increment = 1;     // up
                decrement = -1;
                while (x + decrement >= 0 && rivalBoard[x + decrement][y] != -1) {
                    if (this.isThrereACoincidence(x + decrement, y, rivalBoard)) {
                        coincidenceCounter++;
                        decrement--;
                    } else {
                        break;
                    }
                }

            case 2:
                increment = 1;    // down
                decrement = -1;
                while (x + increment <= 9 && rivalBoard[x + increment][y] != -1) {
                    if (this.isThrereACoincidence(x + increment, y, rivalBoard)) {
                        coincidenceCounter++;
                        increment++;
                    } else {
                        break;
                    }
                }


            case 3:
                increment = 1;     // left 
                decrement = -1;
                while (y + decrement >= 0 && rivalBoard[x][y + decrement] != -1) {
                    if (this.isThrereACoincidence(x, y + decrement, rivalBoard)) {
                        coincidenceCounter++;
                        decrement--;
                    } else {
                        break;
                    }
                }

            case 4:
                increment = 1;         // right
                decrement = -1;
                while (y + increment <= 9 && rivalBoard[x][y + increment] != -1) {
                    if (this.isThrereACoincidence(x, y + increment, rivalBoard)) {
                        coincidenceCounter++;
                        increment++;
                    } else {
                        break;
                    }
                }

            default:
                break;

        }
        if (coincidenceCounter == shipType) {
            return ('K');
        } else {
            return ('T');
        }


    }

    /**
     * Method neede to evaluate current rival's ship type.
     * Used only for killed/touched detection.
     * 
     * @param x
     * @param y
     * @param rivalBoard
     * @return 
     */
    public int calculateUnknownShipType(int x, int y, int[][] rivalBoard) {
        int direction = 1;
        int decrement;
        int increment;
        int dockCounter = 1;
        switch (direction) {
            case 1:
                increment = 1;
                decrement = -1;
                while (x + decrement >= 0 && rivalBoard[x + decrement][y] != -1) {
                    if (rivalBoard[x + decrement][y] == 1) {
                        dockCounter++;
                        decrement--;
                    } else {
                        break;
                    }
                }

            case 2:
                increment = 1;
                decrement = -1;
                while (x + increment <= 9 && rivalBoard[x + increment][y] != -1) {
                    if (rivalBoard[x + increment][y] == 1) {
                        dockCounter++;
                        increment++;
                    } else {
                        break;
                    }
                }


            case 3:
                increment = 1;
                decrement = -1;
                while (y + decrement >= 0 && rivalBoard[x][y + decrement] != -1) {
                    if (rivalBoard[x][y + decrement] == 1) {
                        dockCounter++;
                        decrement--;
                    } else {
                        break;
                    }
                }

            case 4:
                increment = 1;
                decrement = -1;
                while (y + increment <= 9 && rivalBoard[x][y + increment] != -1) {
                    if (rivalBoard[x][y + increment] == 1) {
                        dockCounter++;
                        increment++;
                    } else {
                        break;
                    }
                }

            default:
                break;

        }
        return (dockCounter);

    }
}
