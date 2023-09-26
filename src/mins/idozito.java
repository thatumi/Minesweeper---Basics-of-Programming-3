package mins;

import javax.swing.*;

/** Visszaszámláló időzítő */
public class idozito extends JLabel {
    /** Itt van eltárolva, hogy mehet-e az időzítő, vagy meg kell állítani */
    private boolean mehet;
    /** Thread létrehozása */
    private Runnable fd;
    /** Innentől számol vissza */
    private int masodperc = 100;

    /** A masodperc változót returnolja
     * @return Hány másodperc maradt az időzitőből.
     */
    public int getMasodperc() {
        return masodperc;
    }

    /** A mehet változót returnolja
     * @return Az időzítő meg van állítva vagy sem
     */
    public boolean getMehet() {
        return mehet;
    }

    /** A visszaszámlálót állítja. Ha negatív számot kap, 0 lesz
     * @return mp A másodperc amire állítani szeretnénk a visszaszámlálót
     */
    public void setMasodperc(int mp) {
        if(mp > 0){
            this.masodperc = mp;
        }else{
            this.masodperc = 0;
        }

        setText(""+masodperc);
    }

    /** Visszaszámláló időzítő */
    public idozito() {
        super("", SwingConstants.CENTER);
        fd = new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
                while (mehet) {
                    setText(String.valueOf(masodperc));
                    try {
                        Thread.sleep(1000);
                        masodperc -= 1;
                    } catch (InterruptedException e) {
                    }
                    if(Integer.valueOf(getText()) == 0) {
                        mehet = false;
                        setText("Az ido lejart");
                    }

                }
            }
        };
        mehet = false;
    }

    /** (Újra)indítja a visszaszámlálót */
    public void start() {
        mehet = true;
        new Thread(fd).start();
    }

    /** A mehet változót false-ra állítja, így megáll a visszaszámláló */
    public void stop() {
        mehet = false;
    }

    /** Alap értékre állítja a másodperc változót, a mehet változót false-ra, így meg is állítja a visszaszámlálót */
    public void reset() {
        mehet = false;
        setText(""+masodperc);
        masodperc = 100;
    }
}