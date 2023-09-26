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

/** JFrame-k beállítása történik itt főként */
public class Main extends JFrame {
    /** JLabelt hoz létre, később ide lesz kiírva a bombák száma és a játék állapota. */
    private JLabel szovegmezo = new JLabel("");
    /** Időzítő létrehozása */
    private idozito idozito = new idozito();
    /** eredmenytabla példányosítás */
    private eredmenytabla eredmenytabla = new eredmenytabla();
    /** Ide lesz eltárolva a felhasználónév a nyertes játék után, az eredménytáblához */
    private JTextField felhasznalonev;
    /** Eredménytábla framejét hozza létre */
    private JFrame eredmenyFrame;
    /** Nyertes játék esetén itt lehet bevinni a felhasználónevet */
    private JFrame ujEredmenyFrame;
    /** Főmenü framejét hozza létre */
    private JFrame menuFrame;
    /** Ide lesz kiírva a top10 eredménytábla */
    private JLabel eredmenyek;
    private minesweeper minesweeper = new minesweeper(szovegmezo, idozito);



    public static void main(String[] args) {
        var ex = new Main();
        ex.setVisible(true);
    }

    public Main() {
        menuInic();

    }
    /** Menu frame beállítása */
    private void menuInic() {
        menuFrame = new JFrame("aknakereso");
        JButton bJatek = new JButton("Új játék");
        bJatek.setBounds(10, 50, 150, 20);
        menuFrame.add(bJatek);
        bJatek.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jatekInic();
                menuFrame.setVisible(false);
            }
        });
        JButton bEredmenyek = new JButton("Eredmenyek");
        bEredmenyek.setBounds(10, 100, 150, 20);
        menuFrame.add(bEredmenyek);
        bEredmenyek.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                eredmenyTablaInic();
                // menuFrame.setVisible(false);
            }
        });
        JButton bBetolt = new JButton("Betöltés");
        bBetolt.setBounds(10, 150, 150, 20);
        menuFrame.add(bBetolt);
        bBetolt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jatekInic();
                minesweeper.betoltes();
                menuFrame.setVisible(false);
            }
        });
        menuFrame.setResizable(false);
        menuFrame.setTitle("Aknakereső");
        menuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        menuFrame.setSize(300, 300);
        menuFrame.setLayout(null);
        menuFrame.setVisible(true);
    }

    /** Játék frame beállítása */
    private void jatekInic() {
        //jatekFrame = new JFrame("aknakereso");
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(final KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_F5) {
                    minesweeper.mentes();
                }
            }
        });
        if(minesweeper.getGameWon() == true){
            //ujEredmenyInic();
        }


        add(idozito, BorderLayout.SOUTH);
        add(szovegmezo, BorderLayout.NORTH);
        add(minesweeper);
        setResizable(false);
        setTitle("Aknakereső");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
    }

    /** Eredménytábla frame beállítása */
    private void eredmenyTablaInic() {
        eredmenyFrame = new JFrame("eredmenytabla");
        felhasznalonev = new JTextField("felhasznaloneved");
        eredmenyek = new JLabel(eredmenytabla.eredmenyBe());
        eredmenyek.setBounds(0, 0, 200, 300);
        eredmenyFrame.add(eredmenyek);
        eredmenyFrame.setSize(200, 400);
        eredmenyFrame.setLayout(null);
        eredmenyFrame.setVisible(true);
    }
}