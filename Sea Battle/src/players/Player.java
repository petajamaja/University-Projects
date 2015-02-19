package players;
import java.util.Scanner;

public abstract class Player{
    
    public int[][] myBoard;
    public int[][] myHits;
        
    Scanner scan;
    int[]  beginningCoordinate = new int[2];
    int[]  endCoordinate = new int[2];
    String moveDirectionUpDown;
    String moveDirectionLeftRight;
    int shipType;
    protected String message = "";
    protected String validityMessage = "";
    protected String message1 = "A ship can't be placed on a diagonal!\b\n";
    protected String message2 = "Your ship touches another ship! Please change the last two coordinates!\b\n";
    protected String message3 = "You have entered wrong coordinates: your ship is longer/shorter than expected!\b\n";
    
    Player(int[][] myBoard, int[][] myHits,Scanner scan){
        this.myBoard = myBoard;
        this.myHits = myHits;
        this.scan = scan;
    }
        
    public void setNumberOfDocksAs(int docks){
        this.shipType = docks;
    }
    public int getNumberOfDocks(){
        return(this.shipType);
    }
    public void setMyBoard(int[][] board){
        this.myBoard = board;
    }
    public void setMyHitResult(int[] hit, int value){
        this.myHits[hit[0]][hit[1]] = value;
    }
    
    /**
     * General control method.
     * Used to detect if there is no obstacle for setting a ship.
     * 
     * @param coordinate
     * @return 
     */
    public boolean controlValidityOfCoordinate(int[]coordinate){
        
        validityMessage = "";
        boolean isAvailable = true;
        int x = coordinate[0];
        int y = coordinate[1];

        
            if ((x >= 0 && x <= 9) && (y >= 0 && y <= 9)) {
                if (this.myBoard[x][y] == 1) {
                    isAvailable = false;
                }
            }
            if (x >= 1 && y >= 1 && x <= 9 && y <=9 ) {
                if (this.myBoard[x - 1][y - 1] == 1) {
                    isAvailable = false;
                }
            }
            if (x >= 1 && y < 9 && x<=9 && y >= 0) {
                if (this.myBoard[x - 1][y + 1] == 1) {
                    isAvailable = false;
                }
            }
            if (y >= 1 && x < 9 && y <= 9 && x >= 0) {
                if (this.myBoard[x + 1][y - 1] == 1) {
                    isAvailable = false;
                }
            }
            if (x < 9 && (y <= 9 && y >= 0) && x >= 0) {
                if (this.myBoard[x + 1][y] == 1) {
                    isAvailable = false;
                }
            }
            if (x < 9 && y < 9 && x >= 0 && y >= 0) {
                if (this.myBoard[x + 1][y + 1] == 1) {
                    isAvailable = false;
                }
            }
            if (y < 9 && (x <= 9 && x >= 0) && y >= 0) {
                if (this.myBoard[x][y + 1] == 1) {
                    isAvailable = false;
                }
            }
            if (x >= 1 && (y <= 9 && y >= 0 ) && x <= 9) {
                if (this.myBoard[x - 1][y] == 1) {
                    isAvailable = false;
                }
            }
            if (y >= 1 && x <= 9 && y <=9 && x >= 0) {
                if (this.myBoard[x][y - 1] == 1) {
                    isAvailable = false;
                }
            }

        
        if(!isAvailable) validityMessage = message2; 
        return(isAvailable);        
    }
    
    public boolean controlCoordinate(int coordinate){
        if(coordinate <= 9 && coordinate >= 0) return(true);
        else return(false);
    }
    
    /**
     * Method intended to detect if something is wrongh with:
     * ship's length; ship's position; crosses.
     * 
     * @param shipType
     * @param beginning
     * @param end
     * @return 
     */
    public boolean controlWholeShipPosition(int shipType, int[] beginning, int[] end ){
        
        this.message = "";
        boolean isAvailable = true;
        int i;
        int[] coordinate = new int[2];
        
        // diagonal control
        if(beginning[0] != end[0] && beginning[1]!= end[1]) {
            isAvailable = false;
            this.message = this.message1;
        }
        else{
            // length control + calling controlValidityOfCoordinate()
            if(beginning[0]==end[0]){
                
                for(i = beginning[1]; i < beginning[1] + shipType; i++){
                    coordinate[0] = beginning[0];
                    coordinate[1] = i;
                    if(!this.controlValidityOfCoordinate(coordinate)){
                        isAvailable = false;
                        break;
                    }
                }if(coordinate[1]!=end[1]) {
                    isAvailable = false;
                    this.message = this.message3;
                    
                }
            }else{
                for( i = beginning[0]; i < beginning[0] + shipType;i++){
                    coordinate[0]=i;
                    coordinate[1]=beginning[1];
                    if(!this.controlValidityOfCoordinate(coordinate)){
                        isAvailable = false;
                        break;
                    }
                } 
                  if(coordinate[0]!=end[0]) {
                  isAvailable = false;
                  this.message = this.message3;
                }
            }
        }
        return(isAvailable);
    }
    
    /**
     * Function needed for correct displaying and detecting winners.
     * After killing the ship replaces previous touch-values by kill-values
     * 
     * @param killedSquare
     * @param rivalBoard 
     */
    public void replaceTouchedByKilled(int[] killedSquare,int[][]rivalBoard){
          int x = killedSquare[0];
          int y = killedSquare[1];        
          int direction = 1;
          int decrement;
          int increment;
          switch(direction){
               case 1:  increment = 1;
                        decrement = -1;      
                        while(x+decrement >= 0 && rivalBoard[x+decrement][y]!=-1){
                             if(rivalBoard[x+decrement][y] == 1){
                                   this.myHits[x+decrement][y] = 2;
                                   decrement--;
                             }
                             else break;
                            }
                        
               case 2:  increment = 1;
                        decrement = -1;
                        while(x+increment <=9 && rivalBoard[x+increment][y]!=-1){
                              if(rivalBoard[x+increment][y] == 1){
                                   this.myHits[x+increment][y] = 2; 
                                   increment++;
                             }
                               else  break;
                             }
                        
                        
               case 3:  increment = 1;
                        decrement = -1;
                        while(y+decrement >= 0 && rivalBoard[x][y+decrement]!=-1){
                             if(rivalBoard[x][y+decrement] == 1){
                                    this.myHits[x][y+decrement] = 2; 
                                    decrement--;
                             }
                             else break;
                             }
                       
               case 4:  increment = 1;
                        decrement = -1;
                        while(y+increment <=9 && rivalBoard[x][y+increment]!=-1){
                             if(rivalBoard[x][y+increment] == 1){
                                    this.myHits[x][y+increment] = 2;
                                    increment++;
                             }
                             else break;
                             }
                       
               default:
                        break;
          }
      }
    
    public boolean haveIShotThereBefore(int[] square){
         if (this.myHits[square[0]][square[1]] == -1)
             return(false);
         else 
             return(true);
    }
    
    /**
     * Function enables free order of coordinate setting.
     * Needed to avoid IndexOutOfBoundsException.
     */
    public void exchangeBeginningAndEndCoordinates(){
        if(this.beginningCoordinate[0]>this.endCoordinate[0]
                ||this.beginningCoordinate[1]>this.endCoordinate[1]){
            int[] copy = new int[2];
            copy = this.beginningCoordinate;
            this.beginningCoordinate = this.endCoordinate;
            this.endCoordinate = copy;
        }
    }
    
     /**
     * Function-helper for comparing values of two massives.
     * 
     * @param x
     * @param y
     * @return 
     */
    public boolean isThrereACoincidence(int x,int y,int[][] rivalBoard){
          if(this.myHits[x][y] == rivalBoard[x][y]) return true;
          else return false;
      }
    
    /**
     * Method uses killed-values-counter.
     * 
     * @return 
     */
    public boolean isWinner(){
          int numberKilled = 0;
          for(int i = 0; i < myHits.length; i++){
              for(int j = 0; j < myHits[0].length; j++){
                  if(myHits[i][j] == 2)
                      numberKilled ++;
              }
          }
          if(numberKilled == 20) return(true);
          else return(false);
      }     
     
 }   
    
    

    
