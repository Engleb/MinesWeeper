import com.cs.engine.cell.Color;
import com.cs.engine.cell.Game;

import java.util.ArrayList;
import java.util.List;

public class MinesweeperGame extends Game {
   private static final int SIDE = 10;
   private int countClosedTiles = SIDE*SIDE;
   private static final String MINE = "\uD83D\uDCA3";
   private static final String FLAG = "\uD83D\uDEA9";
   private GameObject[][] gameField = new GameObject[SIDE][SIDE];
   private int countMinesOnField = 0;
   private int countFlag = 0;
   private boolean isGameStopped;
   List<GameObject> list = new ArrayList<>();

    public void initialize() {
        setScreenSize(SIDE,SIDE);
        createGame();
    }

    private void createGame() {
        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE; j++) {
                setCellValue(j,i,"");
                setCellColor(j,i, Color.LIGHTGRAY);
                gameField[i][j] = new GameObject(j,i,getRandomNumber(10)==0);
                if (gameField[i][j].isMine){
                    countMinesOnField++;
                    // list = getNeighbours(gameField[i][j]);
                }
            }
        }
        countMineNeighbours();
        countFlag = countMinesOnField;
        setScore(countFlag);
    }

    private void countMineNeighbours() {
        List<GameObject> list = new ArrayList();
        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE; j++) {
                if (!gameField[j][i].isMine){
                    list = getNeighbours(gameField[j][i]);
                    for (GameObject o : list) {
                        if (o.isMine){
                            gameField[j][i].countMineNeighbours++;
                        }
                    }
                }
            }
        }
    }

    private List<GameObject> getNeighbours(GameObject gameObject) {
        int x = gameObject.x;
        int y = gameObject.y;
        List<GameObject> listIn = new ArrayList<>();
        for (int i = x-1; i < x+2; i++) {
            for (int j = y-1; j < y+2; j++) {
                if (!((i == x && j == y) || i < 0 || j < 0 || j > SIDE-1 || i > SIDE-1)){
                    listIn.add(gameField[j][i]);
                }
            }

        }

        return listIn;
    }

    @Override
    public void onMouseLeftClick(int x, int y) {
        if (!isGameStopped)
            openTile(x,y);
        else
            restart();
        super.onMouseLeftClick(x, y);
    }

    @Override
    public void onMouseRightClick(int x, int y) {
        markTile(x,y);
        super.onMouseRightClick(x, y);
    }

    private void markTile(int x, int y) {
        if(!gameField[y][x].isOpen && !isGameStopped){
            if (gameField[y][x].isFlag) {
                gameField[y][x].isFlag = false;
                countFlag++;
                setCellValue(x, y, "");
            } else if (countFlag != 0) {
                gameField[y][x].isFlag = true;
                countFlag--;
                setCellValue(x, y, FLAG);
            }
        }
        setScore(countFlag);
    }

    private void openTile(int x, int y) {
        if (!gameField[y][x].isOpen && !isGameStopped && !gameField[y][x].isFlag) {
            gameField[y][x].isOpen = true;
            countClosedTiles--;
            if (gameField[y][x].isMine) {
                setCellValueEx(x, y, Color.RED, MINE);
                gameOver();
            } else {
                if (gameField[y][x].countMineNeighbours != 0) {
                    setCellNumber(x, y, gameField[y][x].countMineNeighbours);
                    setCellColor(x, y, Color.GREENYELLOW);
                } else {
                    setCellColor(x, y, Color.TEAL);
                    List<GameObject> list = getNeighbours(gameField[y][x]);
                    for (GameObject each : list) {
                        if (!each.isOpen)
                            openTile(each.x, each.y);
                    }
                }
                if (countMinesOnField == countClosedTiles)
                    win();

            }
        }
    }

    private void win() {
        isGameStopped = true;
        showMessageDialog(Color.BLACK,
                "YOU WIN!!!",
                Color.BLUEVIOLET,40);
    }

    private void gameOver() {
        isGameStopped = true;
        showMessageDialog(Color.BLACK,
                "Game OVER!!!",
                Color.BLUEVIOLET,40);
    }

    private void restart() {
        isGameStopped = false;

        countClosedTiles = SIDE * SIDE;
        countMinesOnField = 0;
        createGame();
    }
}