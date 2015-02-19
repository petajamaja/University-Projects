package players;
import java.util.Scanner;

public class PlayerComputer extends Player{
    
    public PlayerComputer(int[][] myBoard,int[][] myHits, Scanner scan) {
        super(myBoard,myHits,scan);
    }
    
    /**
     * Function intended to set ships of PlayerComputer.
     * It proceeds by setting coordinates until they are valid.
     * Control methods inherited.
     * 
     * @param numberOfDocks 
     */
    public void setShipsOfType(int numberOfDocks) {
        for (int counter = 1; counter <= 5 - numberOfDocks; counter++) {
            do {
                do {
                    this.beginningCoordinate[0] = setCoordinate(0);
                    this.beginningCoordinate[1] = setCoordinate(1);
                } while (!this.controlValidityOfCoordinate(this.beginningCoordinate));
                do {
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
        }
    }

    public int setCoordinate(int j) {
        double coordinate;
        coordinate = Math.random() * 10;
        if (coordinate > 9) {
            coordinate = 9.0;
        }
        if (coordinate < 0) {
            coordinate = 0.0;
        }
        return ((int) coordinate);
    }

    public int[] randomMove() {
        int[] hitSquare = new int[2];
        do {
            hitSquare[0] = setCoordinate(0);
            hitSquare[1] = setCoordinate(1);

        } while (this.haveIShotThereBefore(hitSquare));
        return (hitSquare);
    }
    /**
     * Function intended to find out rival ship's direction.
     * Called in case rival's ship has been touched by latterHit.
     * 
     * @param latterHit
     * @return 
     */
    public int[] moveAround(int[] latterHit) {
        int[] hit = new int[2];
        int x = latterHit[0];
        int y = latterHit[1];
        int[] square = new int[2];
        
        // right check
        if (y < 9 && x <= 9 && x >= 0 && y >= 0) {
            square[0] = x;
            square[1] = y + 1;
            if (!this.haveIShotThereBefore(square)) {
                hit[0] = square[0];
                hit[1] = square[1];
            }
            
            if (x < 9 && y <= 9 && y >= 0 && x >= 0) {
                square[0] = x + 1;
                square[1] = y;
                if (!this.haveIShotThereBefore(square)) {
                    hit[0] = square[0];
                    hit[1] = square[1];
                }
            }

            if (x >= 1 && y <= 9 && y >= 0 && x <= 9) {
                square[0] = x - 1;
                square[1] = y;
                if (!this.haveIShotThereBefore(square)) {
                    hit[0] = square[0];
                    hit[1] = square[1];
                }
            }

        }
        if (y >= 1 && x <= 9 && y <= 9 && x >= 0) {
            square[0] = x;
            square[1] = y - 1;
            if (!this.haveIShotThereBefore(square)) {
                hit[0] = square[0];
                hit[1] = square[1];
            }
        }
        return (hit);
    }
    /**
     * Function intended to proceed shooting in certain direction
     * found out in function moveAround().
     * 
     * @param direction1 
     * @param direction2
     * @param latterHit
     * @return 
     */

    public int[] moveForwardIn(int direction1, int direction2, int[] latterHit) {

        int[] hit = new int[2];
        // direction1 - left-right or up-down
        // direction2 - left or right, up or down
        
        while (true) {
            int increment = 1;
            int decrement = -1;
            
            // moving right
            if (direction1 == 1 && direction2 == 1) {
                hit[0] = latterHit[0];
                if (latterHit[1] + increment - 1 != 9) {
                    do {
                        hit[1] = latterHit[1] + increment;
                        ++increment;
                    } while (latterHit[1] + increment - 1 != 9 && this.haveIShotThereBefore(hit));
                    if (!this.haveIShotThereBefore(hit)) {
                        break;
                    } else {
                        direction2 = 2;
                    }
                } else {
                    direction2 = 2;
                }
            }
            
            // moving left
            if (direction1 == 1 && direction2 == 2) {

                hit[0] = latterHit[0];
                if (latterHit[1] + decrement + 1 != 0) {
                    do {
                        hit[1] = latterHit[1] + decrement;
                        --decrement;
                    } while (latterHit[1] + decrement + 1 != 0 && this.haveIShotThereBefore(hit));
                    if (!this.haveIShotThereBefore(hit)) {
                        break;
                    } else {
                        direction2 = 1;
                    }
                } else {
                    direction2 = 1;
                }
            }
            
            // moving down
            if (direction1 == 2 && direction2 == 1) {

                hit[1] = latterHit[1];
                if (latterHit[0] + increment - 1 != 9) {
                    do {
                        hit[0] = latterHit[0] + increment;
                        ++increment;
                    } while (latterHit[0] + increment - 1 != 9 && this.haveIShotThereBefore(hit));
                    if (!this.haveIShotThereBefore(hit)) {
                        break;
                    } else {
                        direction2 = 2;
                    }
                } else {
                    direction2 = 2;
                }
            }
            
            // moving up
            if (direction1 == 2 && direction2 == 2) {
                hit[1] = latterHit[1];
                if (latterHit[0] + decrement + 1 != 0) {
                    do {
                        hit[0] = latterHit[0] + decrement;
                        --decrement;
                    } while (latterHit[0] + decrement + 1 != 0 && this.haveIShotThereBefore(hit));
                    if (!this.haveIShotThereBefore(hit)) {
                        break;
                    } else {
                        direction2 = 1;
                    }

                } else {
                    direction2 = 1;
                }
            }

        }
        return (hit);

    }
    /**
     * Function in which ComputerPlayer asks HumanPlayer 
     * if he has touched/killed/missed human's ship.
     * 
     * @param hit 
     */
    public void controlShotResults(int[] hit) {
        String a = null;
        System.out.printf("\n MY SHOT : %d - %d \n", hit[0], hit[1]);
        System.out.println("For EXIT to main menu press 'e' . For CONTINUE press ANY OTHER KEY.");
        a = scan.next();
        if (a.equals("e")) {
            game.Dispatcher.battle.setSuspendFlag(false);
        } else {
            game.Dispatcher.battle.setSuspendFlag(true);
            System.out.println("*****************************************************************************");
            System.out.println("* Please tell me if I have touched or killed your ship (or maybe I missed?) *");
            System.out.println("*                    For \"KILLED\" press 2                                 *");
            System.out.println("*                    For \"TOUCHED\" press 1                                *");
            System.out.println("*                    For \"MISSED\" press 0                                 *");
            System.out.println("*****************************************************************************");
            while (!scan.hasNextInt()) {
                scan.next();
            }
            int result = scan.nextInt();

            switch (result) {
                case 0:
                    this.setMyHitResult(hit, 0);
                    break;
                case 1:
                    this.setMyHitResult(hit, 1);
                    break;
                case 2: {
                    this.setMyHitResult(hit, 2);

                }

                break;
                default:
                    break;
            }
        }

    }
    
    /**
     * Computer intelligence increaser.
     * Surrounds killed ship by miss-values.
     */
    public void surroundKilledByMissed() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (this.myHits[i][j] == 2) {
                    
                    int x = i;
                    int y = j;
                    int direction = 1;
                    
                    switch (direction) {
                        case 1:
                            if (x - 1 >= 0 && this.myHits[x - 1][y] == -1) {
                                this.myHits[x - 1][y] = 0;
                            }

                        case 2:
                            if (x + 1 <= 9 && this.myHits[x + 1][y] == -1) {
                                this.myHits[x + 1][y] = 0;
                            }




                        case 3:
                            if (y - 1 >= 0 && this.myHits[x][y - 1] == -1) {
                                this.myHits[x][y - 1] = 0;
                            }



                        case 4:
                            if (y + 1 <= 9 && this.myHits[x][y + 1] == -1) {
                                this.myHits[x][y + 1] = 0;
                            }

                        case 5:
                            if (y + 1 <= 9 && x + 1 <= 9 && this.myHits[x + 1][y + 1] == -1) {
                                this.myHits[x + 1][y + 1] = 0;
                            }
                        case 6:
                            if (y + 1 <= 9 && x - 1 >= 0 && this.myHits[x - 1][y + 1] == -1) {
                                this.myHits[x - 1][y + 1] = 0;
                            }
                        case 7:
                            if (x + 1 <= 9 && y - 1 >= 0 && this.myHits[x + 1][y - 1] == -1) {
                                this.myHits[x + 1][y - 1] = 0;
                            }
                        case 8:
                            if (x - 1 >= 0 && y - 1 >= 0 && this.myHits[x - 1][y - 1] == -1) {
                                this.myHits[x - 1][y - 1] = 0;
                            }
                        default:
                            break;
                    }
                }
            }

        }
    }  
        
    }

