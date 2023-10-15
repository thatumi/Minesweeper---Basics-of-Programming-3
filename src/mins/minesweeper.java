package mins;

import javax.swing.*;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Game logic, game state saving, and loading.
 */
public class minesweeper extends JPanel {
    /**
     * Stores whether the game is currently active.
     */
    private boolean inGame;
    /**
     * Stores whether the game has been won.
     */
    private boolean gameWon = false;
    /**
     * Stores the number of remaining bombs.
     */
    private int remainingBombs;
    /**
     * Stores images for graphical representation.
     */
    private Image[] images = new Image[14];
    /**
     * Number of rows and columns in the game field.
     */
    private int size = 10;
    /**
     * Size of the game window.
     */
    private final int fieldSize = size * 32 + 1;
    /**
     * Initial number of mines in the game.
     */
    private final int mines = 15;
    // private int[][] field = new int[size][size];
    /**
     * Collection(s) that store the game field.
     */
    ArrayList<ArrayList<Integer>> fieldCollection;
    /**
     * Size of tiles in pixels for drawing and mouse input.
     */
    private final int tileSize = 32;
    /**
     * Id for a plain cell.
     */
    private final int cellId = 10;
    /**
     * Id for an empty cell.
     */
    private final int emptyCellId = 0;
    /**
     * Id for a mine.
     */
    private final int mineId = 9;
    /**
     * Id for a flagged cell.
     */
    private final int flagId = 11;
    /**
     * Id for an 'X' flag (indicates wrongly placed flags when the game is lost).
     */
    private final int xFlagId = 12;
    /**
     * Id for a timed mine.
     */
    private final int timedMineId = 13;
    /**
     * Id for a covered timed mine that hasn't been flagged or uncovered yet. It's calculated by adding timedMineId and cellId.
     */
    private final int coveredTimedMineId = timedMineId + cellId;
    /**
     * Id for a flagged timed mine. It's calculated by adding coveredTimedMineId and cellId.
     */
    private final int flaggedTimedMineId = coveredTimedMineId + cellId;
    /**
     * Id for a covered mine that hasn't been flagged or uncovered yet. It's calculated by adding mineId and cellId.
     */
    private final int coveredMineId = mineId + cellId;
    /**
     * Id for a flagged mine. It's calculated by adding coveredMineId and cellId.
     */
    private final int flaggedMineId = coveredMineId + cellId;
    /**
     * JLabel for displaying the number of bombs and game status.
     */
    private final JLabel textField;
    /**
     * Timer for timed mines.
     */
    private Timer timer;

    /**
     * Stores the i position of the timed mine. (Ideally, this variable wouldn't be needed, but it was the simplest way to add a new mine type.)
     */
    int iTimedMine = 0;
    /**
     * Stores the j position of the timed mine. (Ideally, this variable wouldn't be needed, but it was the simplest way to add a new mine type.)
     */
    int jTimedMine = 0;

    /**
     * Scoreboard.
     */
    ScoreTable scoreboard = new ScoreTable();


    /**
     * Returns the value of the gameWon variable.
     *
     * @return {boolean} The game state.
     */
    public boolean isGameWon() {
        return gameWon;
    }

    /**
     * @param textField This is where the number of bombs and game status will be displayed.
     * @param timer     Countdown timer.
     */
    public minesweeper(JLabel textField, Timer timer) {
        this.textField = textField;
        this.timer = timer;
        initializeGame();
    }

    /**
     * Initializes the game field window, MouseListener, stores tiles for later rendering, and resets the timer.
     */
    private void initializeGame() {
        for (int i = 0; i < 14; i++) {
            var imageSource = "src/resources/tiles/" + i + ".png";
            images[i] = (new ImageIcon(imageSource)).getImage();
        }
        setPreferredSize(new Dimension(fieldSize, fieldSize));
        addMouseListener(new MineSweeperMouseAdapter());
        startNewGame();
        timer.reset();
        timer.start();
    }

    /**
     * Starts a new game, populates the game field, and sets up the text field.
     */
    private void startNewGame() {
        /** Used for mine placement. */
        var random = new Random();
        gameWon = false;
        inGame = true;
        remainingBombs = mines;
        fieldCollection = new ArrayList<>();
        for (int i = 0; i < fieldSize; i++) {
            fieldCollection.add(new ArrayList<>());
            for (int j = 0; j < fieldSize; j++) {
                fieldCollection.get(i).add(cellId);
            }
        }

        textField.setText("Bombs:" + Integer.toString(remainingBombs));

        int placedMines = 0;

        while (placedMines < mines) {
            int iRandom = (int) (size * random.nextDouble());
            int jRandom = (int) (size * random.nextDouble());
            int mineType = coveredMineId;
            int addValue = 1;
            if (placedMines == 0) {
                mineType = coveredTimedMineId;
            }
            if ((iRandom < size) && (jRandom < size) && (fieldCollection.get(iRandom).get(jRandom) != coveredMineId)) {
                // Place the mine
                fieldCollection.get(iRandom).set(jRandom, mineType);
                if (placedMines == 0) {
                    iTimedMine = iRandom;
                    jTimedMine = jRandom;
                }

                System.out.println("Mine location:" + iRandom + " " + jRandom + " Type: " + mineType);
                placedMines++;
                if (jRandom > 0) {
                    // Upper left
                    if (iRandom - 1 >= 0 && jRandom - 1 >= 0) {
                        if (fieldCollection.get(iRandom - 1).get(jRandom - 1) != coveredMineId && fieldCollection.get(iRandom - 1).get(jRandom - 1) != coveredTimedMineId) {
                            fieldCollection.get(iRandom - 1).set(jRandom - 1, (fieldCollection.get(iRandom - 1).get(jRandom - 1) + 1));
                        }
                    }

                    // Left
                    if (jRandom - 1 >= 0) {
                        if (fieldCollection.get(iRandom).get(jRandom - 1) != coveredMineId && fieldCollection.get(iRandom).get(jRandom - 1) != coveredTimedMineId) {
                            fieldCollection.get(iRandom).set(jRandom - 1, (fieldCollection.get(iRandom).get(jRandom - 1) + 1));
                        }
                    }

                    // Lower left
                    if (iRandom + 1 < size && jRandom - 1 >= 0) {
                        if (fieldCollection.get(iRandom + 1).get(jRandom - 1) != coveredMineId && fieldCollection.get(iRandom + 1).get(jRandom - 1) != coveredTimedMineId) {
                            fieldCollection.get(iRandom + 1).set(jRandom - 1, (fieldCollection.get(iRandom + 1).get(jRandom - 1) + 1));
                        }
                    }
                }

                // Upper
                if (iRandom - 1 >= 0) {
                    if (fieldCollection.get(iRandom - 1).get(jRandom) != coveredMineId && fieldCollection.get(iRandom - 1).get(jRandom) != coveredTimedMineId) {
                        fieldCollection.get(iRandom - 1).set(jRandom, (fieldCollection.get(iRandom - 1).get(jRandom) + 1));
                    }
                }

                // Lower
                if (iRandom + 1 < size) {
                    if (fieldCollection.get(iRandom + 1).get(jRandom) != coveredMineId && fieldCollection.get(iRandom + 1).get(jRandom) != coveredTimedMineId) {
                        fieldCollection.get(iRandom + 1).set(jRandom, (fieldCollection.get(iRandom + 1).get(jRandom) + 1));
                    }
                }
                if (jRandom < size - 1) {
                    // Upper right
                    if (iRandom - 1 >= 0 && jRandom + 1 < size) {
                        if (fieldCollection.get(iRandom - 1).get(jRandom + 1) != coveredMineId && fieldCollection.get(iRandom - 1).get(jRandom + 1) != coveredTimedMineId) {
                            fieldCollection.get(iRandom - 1).set(jRandom + 1, (fieldCollection.get(iRandom - 1).get(jRandom + 1) + 1));
                        }
                    }

                    // Right
                    if (jRandom + 1 < size) {
                        if (fieldCollection.get(iRandom).get(jRandom + 1) != coveredMineId && fieldCollection.get(iRandom).get(jRandom + 1) != coveredTimedMineId) {
                            fieldCollection.get(iRandom).set(jRandom + 1, (fieldCollection.get(iRandom).get(jRandom + 1) + 1));
                        }
                    }

                    // Lower right
                    if (iRandom + 1 < size && jRandom + 1 < size) {
                        if (fieldCollection.get(iRandom + 1).get(jRandom + 1) != coveredMineId && fieldCollection.get(iRandom + 1).get(jRandom + 1) != coveredTimedMineId) {
                            fieldCollection.get(iRandom + 1).set(jRandom + 1, (fieldCollection.get(iRandom + 1).get(jRandom + 1) + 1));
                        }
                    }
                }
            }
        }
    }

    /**
     * Recursively checks adjacent fields. If empty, it uncovers them.
     *
     * @param i Row to check.
     * @param j Column to check.
     */
    private void checkEmptyNeighborCells(int i, int j) {

        if (j > 0) {
            // Upper Left
            if (i - 1 >= 0 && j - 1 >= 0) {
                if (fieldCollection.get(i - 1).get(j - 1) > mineId) {
                    fieldCollection.get(i - 1).set(j - 1, (fieldCollection.get(i - 1).get(j - 1) - cellId));
                    if (fieldCollection.get(i - 1).get(j - 1) == emptyCellId) {
                        checkEmptyNeighborCells(i - 1, j - 1);
                    }
                }
            }

            // Left
            if (j - 1 >= 0) {
                if (fieldCollection.get(i).get(j - 1) > mineId) {
                    fieldCollection.get(i).set(j - 1, (fieldCollection.get(i).get(j - 1) - cellId));
                    if (fieldCollection.get(i).get(j - 1) == emptyCellId) {
                        checkEmptyNeighborCells(i, j - 1);
                    }
                }
            }

            // Lower Left
            if (i + 1 < size && j - 1 >= 0) {
                if (fieldCollection.get(i + 1).get(j - 1) > mineId) {
                    fieldCollection.get(i + 1).set(j - 1, (fieldCollection.get(i + 1).get(j - 1) - cellId));
                    if (fieldCollection.get(i + 1).get(j - 1) == emptyCellId) {
                        checkEmptyNeighborCells(i + 1, j - 1);
                    }
                }
            }
        }

        // Upper
        if (i - 1 >= 0) {
            if (fieldCollection.get(i - 1).get(j) > mineId) {
                fieldCollection.get(i - 1).set(j, (fieldCollection.get(i - 1).get(j) - cellId));
                if (fieldCollection.get(i - 1).get(j) == emptyCellId) {
                    checkEmptyNeighborCells(i - 1, j);
                }
            }
        }

        // Lower
        if (i + 1 < size) {
            if (fieldCollection.get(i + 1).get(j) > mineId) {
                fieldCollection.get(i + 1).set(j, (fieldCollection.get(i + 1).get(j) - cellId));
                if (fieldCollection.get(i + 1).get(j) == emptyCellId) {
                    checkEmptyNeighborCells(i + 1, j);
                }
            }
        }

        if (j < size - 1) {
            // Upper Right
            if (i - 1 >= 0 && j + 1 < size) {
                if (fieldCollection.get(i - 1).get(j + 1) > mineId) {
                    fieldCollection.get(i - 1).set(j + 1, (fieldCollection.get(i - 1).get(j + 1) - cellId));
                    if (fieldCollection.get(i - 1).get(j + 1) == emptyCellId) {
                        checkEmptyNeighborCells(i - 1, j + 1);
                    }
                }
            }

            // Right
            if (j + 1 < size) {
                if (fieldCollection.get(i).get(j + 1) > mineId) {
                    fieldCollection.get(i).set(j + 1, (fieldCollection.get(i).get(j + 1) - cellId));
                    if (fieldCollection.get(i).get(j + 1) == emptyCellId) {
                        checkEmptyNeighborCells(i, j + 1);
                    }
                }
            }

            // Lower Right
            if (i + 1 < size && j + 1 < size) {
                if (fieldCollection.get(i + 1).get(j + 1) > mineId) {
                    fieldCollection.get(i + 1).set(j + 1, (fieldCollection.get(i + 1).get(j + 1) - cellId));
                    if (fieldCollection.get(i + 1).get(j + 1) == emptyCellId) {
                        checkEmptyNeighborCells(i + 1, j + 1);
                    }
                }
            }
        }
    }

    /**
     * Handles graphic rendering and game state determination.
     */
    @Override
    public void paintComponent(Graphics g) {
        int uncovered = 0;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int cell = fieldCollection.get(i).get(j);

                if (!inGame) {
                    if (cell == coveredMineId) {
                        cell = mineId;
                    } else if (cell == coveredTimedMineId) {
                        cell = timedMineId;
                    } else if (cell == flaggedMineId || cell == flaggedTimedMineId) {
                        cell = flagId;
                    } else if (i == iTimedMine && j == jTimedMine) {
                        cell = timedMineId;
                    } else if (cell > coveredMineId) {
                        cell = xFlagId;
                    } else if (cell > mineId) {
                        cell = cellId;
                    }
                } else {
                    if (cell == coveredTimedMineId) {
                        cell = cellId;
                    } else if (i == iTimedMine && j == jTimedMine) {
                        cell = timedMineId;
                    } else if (cell > coveredMineId) {
                        cell = flagId;
                    } else if (cell > mineId) {
                        cell = cellId;
                        uncovered++;
                    }
                }

                g.drawImage(images[cell], (j * tileSize), (i * tileSize), this);
            }
        }

        // Determine the game state
        if (!inGame) {
            textField.setText("You Lost");
            timer.stop();
        } else if (inGame && uncovered == 0) {
            gameWon = true;
            initializeNewScore();
            timer.stop();
            textField.setText("Victory Royale");
            inGame = false;
        }
    }

    /**
     * Handles mouse input: left-click uncovers cells, right-click flags cells.
     */
    private class MineSweeperMouseAdapter extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            /** Stores the column of the game field where the mouse is located. */
            int column = e.getX() / tileSize;
            /** Stores the row of the game field where the mouse is located. */
            int row = e.getY() / tileSize;
            System.out.println("Clicked on this cell: " + column + " " + row);
            boolean redrawRequired = false;

            // Start a new game on any click
            if (!inGame) {
                startNewGame();
                timer.reset();
                timer.start();
                repaint();
            }

            // Right-click (flagging)
            if (e.getButton() == MouseEvent.BUTTON3) {
                if (fieldCollection.get(row).get(column) > mineId) {
                    redrawRequired = true;
                    System.out.println("redrawRequired true 1");
                    if (fieldCollection.get(row).get(column) <= coveredMineId) {
                        if (remainingBombs > 0) {
                            // fieldCollection[row][column] += cellId;
                            fieldCollection.get(row).set(column, (fieldCollection.get(row).get(column) + cellId));
                            remainingBombs--;
                            textField.setText(Integer.toString(remainingBombs));
                        } else {
                            textField.setText("No more flags available");
                        }
                    } else {
                        // fieldCollection[row][column] -= cellId;
                        fieldCollection.get(row).set(column, (fieldCollection.get(row).get(column) - cellId));
                        remainingBombs++;
                        textField.setText(Integer.toString(remainingBombs));
                    }
                }
            }

            // Left-click
            if (e.getButton() == MouseEvent.BUTTON1 && (fieldCollection.get(row).get(column) > mineId)
                    && (fieldCollection.get(row).get(column) < flaggedMineId)) {

                // fieldCollection[row][column] -= cellId;
                fieldCollection.get(row).set(column, (fieldCollection.get(row).get(column) - cellId));
                redrawRequired = true;
                if (fieldCollection.get(row).get(column) == mineId) {
                    inGame = false;
                }

                if (fieldCollection.get(row).get(column) == timedMineId) {
                    timer.setSeconds(timer.getSeconds() - 10);
                    System.out.print(fieldCollection.get(row).get(column));
                }

                if (fieldCollection.get(row).get(column) == emptyCellId) {
                    checkEmptyNeighborCells(row, column);
                }
            }

            if (redrawRequired) {
                repaint();
            }
        }
    }

    /**
     * Loads a game board and time from a text file.
     */
    public void loadGame() {
        startNewGame();

        repaint();

        try {
            Scanner sc = new Scanner(new BufferedReader(new FileReader("src/resources/board.txt")));
            int[][] newBoard = new int[10][10];
            while (sc.hasNextLine()) {
                for (int i = 0; i < 10; i++) {
                    String[] line = sc.nextLine().trim().split(" ");
                    for (int j = 0; j < 10; j++) {
                        newBoard[i][j] = Integer.parseInt(line[j]);
                    }
                }
                String lineTime = sc.nextLine();
                timer.setSeconds(Integer.parseInt(lineTime));
                String iLocation = sc.nextLine();
                iTimedMine = Integer.parseInt(iLocation);
                String jLocation = sc.nextLine();
                jTimedMine = Integer.parseInt(jLocation);
            }
            inGame = true;
            gameWon = false;
            // fieldCollection = newBoard;
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    fieldCollection.get(i).set(j, (newBoard[i][j]));
                }
            }
            repaint();
            System.out.println("Loaded!");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves the game board and time to a text file.
     */
    public void saveGame() {
        try {
            File file = new File("src/resources/board.txt");

            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    bw.write(String.valueOf(fieldCollection.get(i).get(j)));
                    bw.write(" ");
                }
                bw.newLine();
            }
            bw.write(String.valueOf(timer.getSeconds()));
            bw.newLine();
            bw.write(String.valueOf(iTimedMine));
            bw.newLine();
            bw.write(String.valueOf(jTimedMine));
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Allows entering a username when winning the game.
     */
    private void initializeNewScore() {
        JFrame newScoreFrame = new JFrame("New Score");
        newScoreFrame.setSize(300, 300);
        JButton submitButton = new JButton("Submit");
        submitButton.setBounds(20, 70, 95, 30);
        newScoreFrame.add(submitButton);
        newScoreFrame.setLayout(null);
        newScoreFrame.setVisible(true);
        JTextField usernameField = new JTextField("Username");
        usernameField.setBounds(20, 20, 100, 20);
        newScoreFrame.add(usernameField);
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scoreboard.updateScoreTable(usernameField.getText(), timer.getSeconds());
                newScoreFrame.setVisible(false);
            }
        });
    }
}
