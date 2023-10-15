package mins;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/** Main class for setting up JFrame windows. */
public class Main extends JFrame {
    /** JLabel for displaying the number of bombs and game status. */
    private JLabel textField = new JLabel("");
    /** Timer initialization. */
    private Timer timer = new Timer();
    /** HighScores instance. */
    private ScoreTable highScores = new ScoreTable();
    /** Stores the username for the high scores table after a winning game. */
    private JTextField username;
    /** JFrame for the high scores table. */
    private JFrame highScoresFrame;
    /** JFrame for entering a new high score. */
    private JFrame newHighScoreFrame;
    /** Main menu JFrame. */
    private JFrame menuFrame;
    /** Displays the top 10 high scores. */
    private JLabel highScoreLabel;
    private minesweeper minesweeper = new minesweeper(textField, timer);

    public static void main(String[] args) {
        var ex = new Main();
        ex.setVisible(true);
    }

    public Main() {
        menuInit();
    }

    /** Initialize the menu frame. */
    private void menuInit() {
        menuFrame = new JFrame("Minesweeper");
        JButton newGameButton = new JButton("New Game");
        newGameButton.setBounds(10, 50, 150, 20);
        menuFrame.add(newGameButton);
        newGameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                gameInit();
                menuFrame.setVisible(false);
            }
        });
        JButton highScoresButton = new JButton("High Scores");
        highScoresButton.setBounds(10, 100, 150, 20);
        menuFrame.add(highScoresButton);
        highScoresButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                highScoresTableInit();
            }
        });
        JButton loadGameButton = new JButton("Load Game");
        loadGameButton.setBounds(10, 150, 150, 20);
        menuFrame.add(loadGameButton);
        loadGameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                gameInit();
                minesweeper.loadGame();
                menuFrame.setVisible(false);
            }
        });
        menuFrame.setResizable(false);
        menuFrame.setTitle("Minesweeper");
        menuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        menuFrame.setSize(300, 300);
        menuFrame.setLayout(null);
        menuFrame.setVisible(true);
    }

    /** Initialize the game frame. */
    private void gameInit() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(final KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_F5) {
                    minesweeper.saveGame();
                }
            }
        });
        if (minesweeper.isGameWon()) {
            newHighScoreInit();
        }
        add(timer, BorderLayout.SOUTH);
        add(textField, BorderLayout.NORTH);
        add(minesweeper);
        setResizable(false);
        setTitle("Minesweeper");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
    }

    /** Initialize the high scores table frame. */
    private void highScoresTableInit() {
        highScoresFrame = new JFrame("High Scores");
        username = new JTextField("Enter your username");
        highScoreLabel = new JLabel(highScores.readScoreTable());
        highScoreLabel.setBounds(0, 0, 200, 300);
        highScoresFrame.add(highScoreLabel);
        highScoresFrame.setSize(200, 400);
        highScoresFrame.setLayout(null);
        highScoresFrame.setVisible(true);
    }

    /** Initialize the frame for entering a new high score. */
    private void newHighScoreInit() {
        newHighScoreFrame = new JFrame("New High Score");
        username = new JTextField("Your username");
        newHighScoreFrame.add(username);
        newHighScoreFrame.setSize(300, 150);
        username.setBounds(20, 20, 200, 30);
        newHighScoreFrame.setVisible(true);
    }
}
