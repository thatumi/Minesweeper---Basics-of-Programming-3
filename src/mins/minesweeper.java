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

/** Játéklogika, illetve játékállás mentés és betöltés */
public class minesweeper extends JPanel {
    /** Eltárolja, hogy aktív játékban vagyunk-e. */
    private boolean jatekban;
    /** Eltárolja, hogy megnyertük-e a játékot */
    private boolean gameWon = false;
    /** A hátralévő bombák számát tárolja */
    private int hatralevoBombak;
    /** A csempe képeke tárolja el a grafikus megjelenítéshez */
    private Image[] kepek = new Image[14];
    /** Játékmező sorainak és oszlopainak száma */
    private int meret = 10;
    /** Az ablak méretesézéhez kell */
    private final int jatekMezoMeret = meret * 32 + 1;
    /** Kezdő aknák száma a játékban */
    private final int aknak = 15;
    //private int[][] jatekMezo = new int[meret][meret];
    /** Ebben a kollekció(k)ban van eltárolva a játékmező*/
    ArrayList<ArrayList<Integer>> list;
    /** A csempék mérete pixelben. Kirajzoláshoz és egérinputhoz szükséges adat. */
    private final int kepMeret = 32;
    /** Sima cella id-je */
    private final int idCella = 10;
    /** Üres mező id-je */
    private final int idSemmi = 0;
    /** Akna id-je */
    private final int idAkna = 9;
    /** Zászlóval jelölt id-je */
    private final int idZaszlo = 11;
    /** X-es zászló id-je (ha elvesztettük a játékot, ez mutatja, ha rossz helyre tettünk zászlót) */
    private final int idXZaszlo = 12;
    /** Időakna mező id-je */
    private final int idIdoAkna = 13;
    /** Időknás mező id-je, ami nem lett megjelölve és feltárva sem. idIdoAkna és idCella-t adja össze, később az idCella kivonásával lesz belőle akna */
    private final int idBetakartIdoAkna = idIdoAkna + idCella; //23
    /** Időknás mező id-je, ami meg lett jelölve zászlóval. idBetakartIdoAkna és idCella-t adja össze, később az idCella kivonásával lesz belőle idBetakartAkna */
    private final int idZaszloIdoAkna = idBetakartIdoAkna + idCella; //33

    /** Aknás mező id-je, ami nem lett megjelölve és feltárva sem. idAkna és idCella-t adja össze, később az idCella kivonásával lesz belőle akna */
    private final int idBetakartAkna = idAkna + idCella;//19
    /** Aknás mező id-je, ami meg lett jelölve zászlóval. idBetakartAkna és idCella-t adja össze, később az idCella kivonásával lesz belőle idBetakartAkna */
    private final int idZaszloAkna = idBetakartAkna + idCella;//29
    /** JLabelt hoz létre, később ide lesz kiírva a bombák száma és a játék állapota. */
    private final JLabel szovegmezo;
    /** Időzítő létrehozása */
    private idozito idozito;

    /** Eltárolja az időakna i pozicióját. (ideálisan nem lenne rá szükség, de úgy kezdtem el írni a programot,
     * hogy csak 1 fajta akna lesz és ez volt a legegyszerűbb módszer újat behozni) */
    int iIdoAkna = 0;
    /** Eltárolja az időakna j pozicióját. (ideálisan nem lenne rá szükség, de úgy kezdtem el írni a programot,
     * hogy csak 1 fajta akna lesz és ez volt a legegyszerűbb módszer újat behozni) */
    int jIdoAkna = 0;

    /** Eredménytábla */
    eredmenytabla eredmenytabla = new eredmenytabla();

    /** A gameWon változót returnolja
     * @return {number} A játék állapota
     */
    public boolean getGameWon() {
        return gameWon;
    }

    /**
     * @param szovegmezo ide lesz kiírva a bombák száma és a játék állapota.
     * @param idozito Visszaszámláló időzítő
     */
    public minesweeper(JLabel szovegmezo, idozito idozito) {
        this.szovegmezo = szovegmezo;
        this.idozito = idozito;
        jatekMezoInic();
    }

    /** Inicializálja a játékmező ablakot és MouseListenert, eltárolja a csempéket későbbi megjelenéshez, reseteli az időt */
    private void jatekMezoInic() {
        for (int i = 0; i < 14; i++) {
            var kepForras = "src/csempek/" + i + ".png";
            kepek[i] = (new ImageIcon(kepForras)).getImage();
        }
        setPreferredSize(new Dimension(jatekMezoMeret, jatekMezoMeret));
        addMouseListener(new aknaEger());
        ujJatek();
        idozito.reset();
        idozito.start();
    }
    /** Új játékot hoz létre, itt van feltöltve a játékmező, itt lesz beállítva a szövegmező */
    private void ujJatek() {
        /** Aknák elhelyezéséhez kell */
        var random = new Random();
        gameWon = false;
        jatekban = true;
        hatralevoBombak = aknak;
        list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(new ArrayList<>());
            for (int j = 0; j < 10; j++) {
                list.get(i).add(idCella);
            }
        }

        szovegmezo.setText("Bombak:" + Integer.toString(hatralevoBombak));

        int iAknak = 0;

        while (iAknak < aknak) {
            int iRandom = (int) (meret * random.nextDouble());
            int jRandom = (int) (meret * random.nextDouble());
            int aknaTipus = idBetakartAkna;
            int hozzaAd = 1;
            if (iAknak == 0){
                aknaTipus = idBetakartIdoAkna;
            }
            if ((iRandom < meret) && (jRandom < meret) && (list.get(iRandom).get(jRandom) != idBetakartAkna)) {
                //Bomba lehelyezés
                list.get(iRandom).set(jRandom, aknaTipus);
                if (iAknak == 0){
                    iIdoAkna = iRandom;
                    jIdoAkna = jRandom;
                }

                System.out.println("Bomba helye:" + iRandom + " " + jRandom + " Típusa: " + aknaTipus);
                iAknak++;
                if (jRandom > 0) {
                    // Balra fel
                    if (iRandom - 1 >= 0 && jRandom - 1 >= 0) {
                        if (list.get(iRandom - 1).get(jRandom - 1) != idBetakartAkna && list.get(iRandom - 1).get(jRandom - 1) != idBetakartIdoAkna) {
                            list.get(iRandom - 1).set(jRandom - 1, (list.get(iRandom - 1).get(jRandom - 1) + 1));
                            //jatekMezo[iRandom - 1][jRandom - 1] += 1;
                        }
                    }

                    // Balra
                    if (jRandom - 1 >= 0) {
                        if (list.get(iRandom).get(jRandom - 1) != idBetakartAkna && list.get(iRandom).get(jRandom - 1) != idBetakartIdoAkna) {
                            list.get(iRandom).set(jRandom - 1, (list.get(iRandom).get(jRandom - 1) + 1));
                            //jatekMezo[iRandom][jRandom - 1] += 1;
                        }
                    }

                    // Balra le
                    if (iRandom + 1 < meret && jRandom - 1 >= 0) {
                        if (list.get(iRandom + 1).get(jRandom - 1) != idBetakartAkna && list.get(iRandom + 1).get(jRandom - 1) != idBetakartIdoAkna) {
                            //jatekMezo[iRandom + 1][jRandom - 1] += 1;
                            list.get(iRandom + 1).set(jRandom - 1, (list.get(iRandom + 1).get(jRandom - 1) + 1));
                        }
                    }
                }

                // Fel
                if (iRandom - 1 >= 0) {
                    if (list.get(iRandom - 1).get(jRandom) != idBetakartAkna && list.get(iRandom - 1).get(jRandom) != idBetakartIdoAkna) {
                        //jatekMezo[iRandom - 1][jRandom] += 1;
                        list.get(iRandom - 1).set(jRandom, (list.get(iRandom - 1).get(jRandom) + 1));
                    }
                }

                // Le
                if (iRandom + 1 < meret) {
                    if (list.get(iRandom + 1).get(jRandom) != idBetakartAkna && list.get(iRandom + 1).get(jRandom) != idBetakartIdoAkna) {
                        //jatekMezo[iRandom + 1][jRandom] += 1;
                        list.get(iRandom + 1).set(jRandom, (list.get(iRandom + 1).get(jRandom) + 1));
                    }
                }
                if (jRandom < meret - 1) {

                    // Jobbra fel
                    if (iRandom - 1 >= 0 && jRandom + 1 < meret) {
                        if (list.get(iRandom - 1).get(jRandom + 1) != idBetakartAkna && list.get(iRandom - 1).get(jRandom + 1) != idBetakartIdoAkna) {
                            //jatekMezo[iRandom - 1][jRandom + 1] += 1;
                            list.get(iRandom - 1).set(jRandom + 1, (list.get(iRandom - 1).get(jRandom + 1) + 1));
                        }
                    }

                    // Jobbra
                    if (jRandom + 1 < meret) {
                        if (list.get(iRandom).get(jRandom + 1) != idBetakartAkna && list.get(iRandom).get(jRandom + 1) != idBetakartIdoAkna) {
                            //jatekMezo[iRandom][jRandom + 1] += 1;
                            list.get(iRandom ).set(jRandom + 1, (list.get(iRandom).get(jRandom + 1) + 1));
                        }
                    }

                    // Jobbra le
                    if (iRandom + 1 < meret && jRandom + 1 < meret) {
                        if (list.get(iRandom + 1).get(jRandom + 1) != idBetakartAkna && list.get(iRandom + 1).get(jRandom + 1) != idBetakartIdoAkna) {
                            //jatekMezo[iRandom + 1][jRandom + 1] += 1;
                            list.get(iRandom + 1).set(jRandom + 1, (list.get(iRandom + 1).get(jRandom + 1) + 1));
                        }
                    }
                }

            }
        }
        //list.get(0).set(0, idBetakartIdoAkna);
    }

    /** Ellenőrzi a szomszédos mezőket rekurzivan, ha üres, felfedi
     * @param i ellenőrizni kivánt sor
     * @param j ellenőrizni kivánt oszlop
     */
    private void szomszedUresMezo(int i, int j) {

        if (j > 0) {
            // BalraFel
            if (i - 1 >= 0 && j - 1 >= 0) {
                if (list.get(i - 1).get(j - 1) > idAkna) {
                    //jatekMezo[i - 1][j - 1] -= idCella;
                    list.get(i - 1).set(j - 1, (list.get(i - 1).get(j - 1) - idCella));
                    if (list.get(i - 1).get(j - 1) == idSemmi) {
                        szomszedUresMezo(i - 1, j - 1);
                    }
                }
            }

            // Balra
            if (j - 1 >= 0) {
                if (list.get(i ).get(j - 1) > idAkna) {
                    list.get(i).set(j - 1, (list.get(i).get(j - 1) - idCella));
                    //jatekMezo[i][j - 1] -= idCella;
                    if (list.get(i ).get(j - 1) == idSemmi) {
                        szomszedUresMezo(i, j - 1);
                    }
                }
            }

            // Balra le
            if (i + 1 < meret && j - 1 >= 0) {
                if (list.get(i + 1).get(j - 1) > idAkna) {
                    //jatekMezo[i + 1][j - 1] -= idCella;
                    list.get(i + 1).set(j - 1, (list.get(i + 1).get(j - 1) - idCella));
                    if (list.get(i + 1).get(j - 1) == idSemmi) {
                        szomszedUresMezo(i + 1, j - 1);
                    }
                }
            }
        }

        // Fel
        if (i - 1 >= 0) {
            if (list.get(i - 1).get(j) > idAkna) {
                //jatekMezo[i - 1][j] -= idCella;
                list.get(i - 1).set(j, (list.get(i - 1).get(j) - idCella));
                if (list.get(i - 1).get(j ) == idSemmi) {
                    szomszedUresMezo(i - 1, j);
                }
            }
        }

        // Le
        if (i + 1 < meret) {
            if (list.get(i + 1).get(j) > idAkna) {
                //jatekMezo[i + 1][j] -= idCella;
                list.get(i + 1).set(j , (list.get(i + 1).get(j ) - idCella));
                if (list.get(i + 1).get(j) == idSemmi) {
                    szomszedUresMezo(i + 1, j);
                }
            }
        }

        if (j < meret - 1) {
            // Jobbra fel
            if (i - 1 >= 0 && j + 1 < meret) {
                if (list.get(i - 1).get(j + 1) > idAkna) {
                    list.get(i - 1).set(j + 1, (list.get(i - 1).get(j + 1) - idCella));
                    //jatekMezo[i - 1][j + 1] -= idCella;
                    if (list.get(i - 1).get(j + 1) == idSemmi) {
                        szomszedUresMezo(i - 1, j + 1);
                    }
                }
            }

            // Jobbra
            if (j + 1 < meret) {
                if (list.get(i).get(j + 1) > idAkna) {
                    //jatekMezo[i][j + 1] -= idCella;
                    list.get(i).set(j + 1, (list.get(i).get(j + 1) - idCella));
                    if (list.get(i).get(j + 1) == idSemmi) {
                        szomszedUresMezo(i, j + 1);
                    }
                }
            }

            // Jobbra le
            if (i + 1 < meret && j + 1 < meret) {
                if (list.get(i+ 1).get(j + 1) > idAkna) {
                    //jatekMezo[i + 1][j + 1] -= idCella;
                    list.get(i + 1).set(j + 1, (list.get(i + 1).get(j + 1) - idCella));
                    if (list.get(i+ 1).get(j + 1) == idSemmi) {
                        szomszedUresMezo(i + 1, j + 1);
                    }
                }
            }
        }
    }

    /** Itt történik a grafikus kirajzolás és a játékállapot eldöntése */
    @Override
    public void paintComponent(Graphics g) {
        //
        int felfed = 0;

        for (int i = 0; i < meret; i++) {
            for (int j = 0; j < meret; j++) {
                int mezo = list.get(i).get(j);

                if (!jatekban) {
                    if (mezo == idBetakartAkna) {
                        mezo = idAkna;
                    }else if (mezo == idBetakartIdoAkna) {
                        mezo = idIdoAkna;
                    } else if (mezo == idZaszloAkna || mezo == idZaszloIdoAkna) {
                        mezo = idZaszlo;
                    }else if(i == iIdoAkna && j == jIdoAkna){
                        mezo = idIdoAkna;
                    }else if (mezo > idBetakartAkna) {
                        mezo = idXZaszlo;
                    } else if (mezo > idAkna) {
                        mezo = idCella;
                    }
                } else {
                    if(mezo == idBetakartIdoAkna){
                        mezo = idCella;
                    }else if(i == iIdoAkna && j == jIdoAkna){
                        mezo = idIdoAkna;
                    }else if (mezo > idBetakartAkna ) {
                        mezo = idZaszlo;
                    } else if (mezo > idAkna ) {
                        mezo = idCella;
                        felfed++;
                    }
                }

                g.drawImage(kepek[mezo], (j * kepMeret), (i * kepMeret), this);
            }
        }

        // Játékállapot eldöntése
        if (!jatekban) {
            szovegmezo.setText("Vesztettel");
            idozito.stop();
        } else if (jatekban && felfed == 0) {
            gameWon = true;
            ujEredmenyInic();
            idozito.stop();
            szovegmezo.setText("Victory Royale");
            jatekban = false;
        }

    }

    /** Mouse inputot kezel, bal klikkel mezőket fed fel, jobb klikkel megjelöl mezőt. */
    private class aknaEger extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            /** Eltárolja, hogy az egér melyik játékmező oszlopban van */
            int egerOszlop = e.getX() / kepMeret;
            /** Eltárolja, hogy az egér melyik játékmező sorban van */
            int egerSor = e.getY() / kepMeret;
            System.out.println("Erre a mezore kattintottal: " + egerOszlop + " " + egerSor);
            boolean ujrarajzKell = false;

            // (bárhova) kattintásra új játék kezdése
            if (!jatekban) {
                ujJatek();
                idozito.reset();
                idozito.start();
                repaint();
            }

            //jobb klikk (zászló jelölés)
            if (e.getButton() == MouseEvent.BUTTON3) {
                if (list.get(egerSor).get(egerOszlop) > idAkna) {
                    ujrarajzKell = true;
                    System.out.println("ujrarajz kell true 1");
                    if (list.get(egerSor).get(egerOszlop) <= idBetakartAkna) {
                        if (hatralevoBombak > 0) {
                            //jatekMezo[egerSor][egerOszlop] += idCella;
                            list.get(egerSor).set(egerOszlop, (list.get(egerSor).get(egerOszlop) + idCella));
                            hatralevoBombak--;
                            szovegmezo.setText(Integer.toString(hatralevoBombak));
                        } else {
                            szovegmezo.setText("Nincs tobb zaszlod");
                        }
                    } else {
                        //jatekMezo[egerSor][egerOszlop] -= idCella;
                        list.get(egerSor).set(egerOszlop, (list.get(egerSor).get(egerOszlop) - idCella));
                        hatralevoBombak++;
                        szovegmezo.setText(Integer.toString(hatralevoBombak));
                    }
                }
            }

            //bal klikkk
            if (e.getButton() == MouseEvent.BUTTON1 && (list.get(egerSor).get(egerOszlop) > idAkna)
                    && (list.get(egerSor).get(egerOszlop) < idZaszloAkna) ) {

                //jatekMezo[egerSor][egerOszlop] -= idCella;
                list.get(egerSor).set(egerOszlop, (list.get(egerSor).get(egerOszlop) - idCella));
                ujrarajzKell = true;
                if (list.get(egerSor).get(egerOszlop) == idAkna) {
                    jatekban = false;
                }

                if (list.get(egerSor).get(egerOszlop) == idIdoAkna) {
                    idozito.setMasodperc(idozito.getMasodperc() - 10);
                    System.out.print(list.get(egerSor).get(egerOszlop));
                }

                if (list.get(egerSor).get(egerOszlop) == idSemmi) {
                    szomszedUresMezo(egerSor, egerOszlop);
                }
            }

            if (ujrarajzKell) {
                repaint();
            }
        }

    }

    /** Egy txt fájlból tölt be pályát és időt. */
    public void betoltes() {
        ujJatek();

        repaint();

        try {
            Scanner sc = new Scanner(new BufferedReader(new FileReader("src/palya.txt")));
            int[][] ujtomb = new int[10][10];
            while (sc.hasNextLine()) {
                for (int i = 0; i < 10; i++) {
                    String[] line = sc.nextLine().trim().split(" ");
                    for (int j = 0; j < 10; j++) {
                        ujtomb[i][j] = Integer.parseInt(line[j]);
                    }
                }
                String lineTime = sc.nextLine();
                idozito.setMasodperc(Integer.parseInt(lineTime));
                String iHely = sc.nextLine();
                iIdoAkna = Integer.parseInt(iHely);
                String jHely = sc.nextLine();
                jIdoAkna = Integer.parseInt(jHely);
            }
            jatekban = true;
            gameWon = false;
            //jatekMezo = ujtomb;
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    list.get(i).set(j, (ujtomb[i][j]));
                }
            }
            repaint();
            System.out.println("Betöltve!");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /** Egy txt fájlba ment el pályát és időt.
     */
    public void mentes() {
        try {
            File file = new File("src/palya.txt");

            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    bw.write(String.valueOf(list.get(i).get(j)));
                    bw.write(" ");
                }
                bw.newLine();
            }
            bw.write(String.valueOf(idozito.getMasodperc()));
            bw.newLine();
            bw.write(String.valueOf(iIdoAkna));
            bw.newLine();
            bw.write(String.valueOf(jIdoAkna));
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /** Nyertes játék esetén itt lehet bevinni a felhasználónevet */
    private void ujEredmenyInic(){
        JFrame ujEredmenyFrame = new JFrame("Uj Eredmeny");
        ujEredmenyFrame.setSize(300, 300);
        JButton b = new JButton("Bevitel");
        b.setBounds(20,70,95,30);
        ujEredmenyFrame.add(b);
        ujEredmenyFrame.setLayout(null);
        ujEredmenyFrame.setVisible(true);
        JTextField felhasznalonev = new JTextField("fnev");
        felhasznalonev.setBounds(20, 20, 100, 20);
        ujEredmenyFrame.add(felhasznalonev);
        b.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                eredmenytabla.eredmenyKi(felhasznalonev.getText(), idozito.getMasodperc());
                ujEredmenyFrame.setVisible(false);
            }
        });
    }

}