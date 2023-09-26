

import mins.eredmenytabla;
import mins.idozito;
import mins.minesweeper;
import org.junit.Assert;
import org.junit.Test;


import javax.swing.*;

import static java.lang.Thread.sleep;

public class teszt {

    @Test
    public void testIdozitoInic() {
        idozito idozito = new idozito();
        idozito.reset();
        int result = idozito.getMasodperc();
        Assert.assertEquals(100, result, 0);
    }
    @Test
    public void testIdozitoStart() throws InterruptedException {
        idozito idozito = new idozito();
        idozito.reset();
        idozito.start();
        sleep(2100);
        int result = idozito.getMasodperc();
        Assert.assertEquals(99, result, 0);
    }

    @Test
    public void testIdozitoSetMasodperc()  {
        idozito idozito = new idozito();
        idozito.reset();
        idozito.setMasodperc(5);
        int result = idozito.getMasodperc();
        Assert.assertEquals(5, result, 0);
    }

    @Test
    public void testIdozitoStop() throws InterruptedException {
        idozito idozito = new idozito();
        idozito.reset();
        idozito.start();
        sleep(2000);
        idozito.stop();
        sleep(2000);
        int result = idozito.getMasodperc();
        Assert.assertEquals(99, result, 0);
    }

    @Test
    public void testIdozitoReset() throws InterruptedException {
        idozito idozito = new idozito();
        idozito.reset();
        idozito.start();
        sleep(2000);
        idozito.reset();
        int result = idozito.getMasodperc();
        Assert.assertEquals(100, result, 0);
    }

    @Test
    public void testGetGameWon() {
        JLabel szovegmezo = new JLabel("");
        idozito idozito = new idozito();
        minesweeper minesweeper = new minesweeper(szovegmezo, idozito);
        boolean result = minesweeper.getGameWon();
        Assert.assertFalse(result);
    }

    @Test
    public void testEredmenyKi()  {
        eredmenytabla eredmenytabla = new eredmenytabla();
        String ercsi[] = eredmenytabla.eredmenyKi("maxiTeszt", 999);
        Assert.assertEquals("maxiTeszt", ercsi[0]);
    }
}
