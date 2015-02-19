/**
 * Graphical representations. Only console!
 */
package display;

public class Display {

    private String[] display = new String[22];

    public Display() {
        this.display[0] = "   0   1   2   3   4   5   6   7   8   9          0   1   2   3   4   5   6   7   8   9   ";
        this.display[1] = " |---|---|---|---|---|---|---|---|---|---|      |---|---|---|---|---|---|---|---|---|---| ";
        this.display[2] = "0|   |   |   |   |   |   |   |   |   |   |     0|   |   |   |   |   |   |   |   |   |   | ";
        this.display[3] = " |---|---|---|---|---|---|---|---|---|---|      |---|---|---|---|---|---|---|---|---|---| ";
        this.display[4] = "1|   |   |   |   |   |   |   |   |   |   |     1|   |   |   |   |   |   |   |   |   |   | ";
        this.display[5] = " |---|---|---|---|---|---|---|---|---|---|      |---|---|---|---|---|---|---|---|---|---| ";
        this.display[6] = "2|   |   |   |   |   |   |   |   |   |   |     2|   |   |   |   |   |   |   |   |   |   | ";
        this.display[7] = " |---|---|---|---|---|---|---|---|---|---|      |---|---|---|---|---|---|---|---|---|---| ";
        this.display[8] = "3|   |   |   |   |   |   |   |   |   |   |     3|   |   |   |   |   |   |   |   |   |   | ";
        this.display[9] = " |---|---|---|---|---|---|---|---|---|---|      |---|---|---|---|---|---|---|---|---|---| ";
        this.display[10] = "4|   |   |   |   |   |   |   |   |   |   |     4|   |   |   |   |   |   |   |   |   |   | ";
        this.display[11] = " |---|---|---|---|---|---|---|---|---|---|      |---|---|---|---|---|---|---|---|---|---| ";
        this.display[12] = "5|   |   |   |   |   |   |   |   |   |   |     5|   |   |   |   |   |   |   |   |   |   | ";
        this.display[13] = " |---|---|---|---|---|---|---|---|---|---|      |---|---|---|---|---|---|---|---|---|---| ";
        this.display[14] = "6|   |   |   |   |   |   |   |   |   |   |     6|   |   |   |   |   |   |   |   |   |   | ";
        this.display[15] = " |---|---|---|---|---|---|---|---|---|---|      |---|---|---|---|---|---|---|---|---|---| ";
        this.display[16] = "7|   |   |   |   |   |   |   |   |   |   |     7|   |   |   |   |   |   |   |   |   |   | ";
        this.display[17] = " |---|---|---|---|---|---|---|---|---|---|      |---|---|---|---|---|---|---|---|---|---| ";
        this.display[18] = "8|   |   |   |   |   |   |   |   |   |   |     8|   |   |   |   |   |   |   |   |   |   | ";
        this.display[19] = " |---|---|---|---|---|---|---|---|---|---|      |---|---|---|---|---|---|---|---|---|---| ";
        this.display[20] = "9|   |   |   |   |   |   |   |   |   |   |     9|   |   |   |   |   |   |   |   |   |   | ";
        this.display[21] = " |---|---|---|---|---|---|---|---|---|---|      |---|---|---|---|---|---|---|---|---|---| ";

    }

    public void displayShip(int[][] myBoard) {
        for (int i = 0; i < myBoard.length; i++) {
            for (int j = 0; j < myBoard[0].length; j++) {
                if (myBoard[i][j] == 1) {
                    int index = 2 * (i + 1);
                    int symbolIndex = 46 + 4 * (j + 1);
                    char shipSquare = 'S';
                    char blankSquare = this.display[index].charAt(symbolIndex);
                    this.display[index] = this.replaceCharAt(this.display[index], symbolIndex, shipSquare);
                }
            }
        }
        this.print();
    }

    public void displayHit(int[][] myHits) {

        for (int i = 0; i < myHits.length; i++) {
            for (int j = 0; j < myHits[0].length; j++) {
                if (myHits[i][j] == 0) {
                    int index = 2 * (i + 1);
                    int symbolIndex = 4 * (j + 1) - 1;
                    char blankSquare = this.display[index].charAt(symbolIndex);
                    char missSquare = '*';
                    this.display[index] = this.replaceCharAt(this.display[index], symbolIndex, missSquare);
                }
                if (myHits[i][j] == 1) {
                    int index = 2 * (i + 1);
                    int symbolIndex = 4 * (j + 1) - 1;
                    char blankSquare = this.display[index].charAt(symbolIndex);
                    char touchSquare = 'T';
                    this.display[index] = this.replaceCharAt(this.display[index], symbolIndex, touchSquare);
                }
                if (myHits[i][j] == 2) {
                    int index = 2 * (i + 1);
                    int symbolIndex = 4 * (j + 1) - 1;
                    char blankSquare = this.display[index].charAt(symbolIndex);
                    char killSquare = 'K';
                    this.display[index] = this.replaceCharAt(this.display[index], symbolIndex, killSquare);
                }
            }
        }
        this.print();
    }

    public void displayOpponentHit(int[][] rivalHits) {
        for (int i = 0; i < rivalHits.length; i++) {
            for (int j = 0; j < rivalHits[0].length; j++) {
                if (rivalHits[i][j] == 0) {
                    int index = 2 * (i + 1);
                    int symbolIndex = 46 + 4 * (j + 1);
                    char blankSquare = this.display[index].charAt(symbolIndex);
                    char missSquare = '*';
                    this.display[index] = this.replaceCharAt(this.display[index], symbolIndex, missSquare);
                }
                if (rivalHits[i][j] == 1) {
                    int index = 2 * (i + 1);
                    int symbolIndex = 46 + 4 * (j + 1);
                    char blankSquare = this.display[index].charAt(symbolIndex);
                    char touchSquare = 'T';
                    this.display[index] = this.replaceCharAt(this.display[index], symbolIndex, touchSquare);
                }
                if (rivalHits[i][j] == 2) {
                    int index = 2 * (i + 1);
                    int symbolIndex = 46 + 4 * (j + 1);
                    char blankSquare = this.display[index].charAt(symbolIndex);
                    char killSquare = 'K';
                    this.display[index] = this.replaceCharAt(this.display[index], symbolIndex, killSquare);
                }
            }
        }
        this.print();
    }

    /**
     * Method intend to actualize display before using print() function.
     * 
     * @param myBoard
     * @param myHits
     * @param rivalBoard
     * @param rivalHits 
     */
    public void actualizeDisplay(int[][] myBoard, int[][] myHits, int[][] rivalBoard, int[][] rivalHits) {

        for (int i = 0; i < myBoard.length; i++) {
            for (int j = 0; j < myBoard[0].length; j++) {
                if (myBoard[i][j] == 1) {
                    int index = 2 * (i + 1);
                    int symbolIndex = 46 + 4 * (j + 1);
                    char shipSquare = 'S';
                    char blankSquare = this.display[index].charAt(symbolIndex);
                    this.display[index] = this.replaceCharAt(this.display[index], symbolIndex, shipSquare);
                }
            }
        }
        for (int i = 0; i < myHits.length; i++) {
            for (int j = 0; j < myHits[0].length; j++) {
                if (myHits[i][j] == 0) {
                    int index = 2 * (i + 1);
                    int symbolIndex = 4 * (j + 1) - 1;
                    char blankSquare = this.display[index].charAt(symbolIndex);
                    char missSquare = '*';
                    this.display[index] = this.replaceCharAt(this.display[index], symbolIndex, missSquare);
                }
                if (myHits[i][j] == 1) {
                    int index = 2 * (i + 1);
                    int symbolIndex = 4 * (j + 1) - 1;
                    char blankSquare = this.display[index].charAt(symbolIndex);
                    char touchSquare = 'T';
                    this.display[index] = this.replaceCharAt(this.display[index], symbolIndex, touchSquare);
                }
                if (myHits[i][j] == 2) {
                    int index = 2 * (i + 1);
                    int symbolIndex = 4 * (j + 1) - 1;
                    char blankSquare = this.display[index].charAt(symbolIndex);
                    char killSquare = 'K';
                    this.display[index] = this.replaceCharAt(this.display[index], symbolIndex, killSquare);
                }
            }
        }
        for (int i = 0; i < rivalHits.length; i++) {
            for (int j = 0; j < rivalHits[0].length; j++) {
                if (rivalHits[i][j] == 0) {
                    int index = 2 * (i + 1);
                    int symbolIndex = 46 + 4 * (j + 1);
                    char blankSquare = this.display[index].charAt(symbolIndex);
                    char missSquare = '*';
                    this.display[index] = this.replaceCharAt(this.display[index], symbolIndex, missSquare);
                }
                if (rivalHits[i][j] == 1) {
                    int index = 2 * (i + 1);
                    int symbolIndex = 46 + 4 * (j + 1);
                    char blankSquare = this.display[index].charAt(symbolIndex);
                    char touchSquare = 'T';
                    this.display[index] = this.replaceCharAt(this.display[index], symbolIndex, touchSquare);
                }
                if (rivalHits[i][j] == 2) {
                    int index = 2 * (i + 1);
                    int symbolIndex = 46 + 4 * (j + 1);
                    char blankSquare = this.display[index].charAt(symbolIndex);
                    char killSquare = 'K';
                    this.display[index] = this.replaceCharAt(this.display[index], symbolIndex, killSquare);
                }
            }
        }
    }

    public String replaceCharAt(String string, int targetPosition, char replacement) {
        return string.substring(0, targetPosition) + replacement + string.substring(targetPosition + 1);
    }

    public void print() {
        for (int i = 0; i < 22; i++) {
            System.out.println(this.display[i]);
        }
    }
}
