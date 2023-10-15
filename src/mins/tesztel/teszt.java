
import mins.minesweeper;
import mins.Timer;
import mins.ScoreTable;
import org.junit.Assert;
import org.junit.Test;


import javax.swing.*;

import static java.lang.Thread.sleep;

public class teszt {

    @Test
    public void testTimerInic() {
        Timer timer = new Timer();
        timer.reset();
        int result = timer.getSeconds();
        Assert.assertEquals(100, result, 0);
    }
    @Test
    public void testTimerStart() throws InterruptedException {
        Timer timer = new Timer();
        timer.reset();
        timer.start();
        sleep(2100);
        int result = timer.getSeconds();
        Assert.assertEquals(99, result, 0);
    }

    @Test
    public void testTimerSetSeconds()  {
        Timer timer = new Timer();
        timer.reset();
        timer.setSeconds(5);
        int result = timer.getSeconds();
        Assert.assertEquals(5, result, 0);
    }

    @Test
    public void testTimerStop() throws InterruptedException {
        Timer timer = new Timer();
        timer.reset();
        timer.start();
        sleep(2000);
        timer.stop();
        sleep(2000);
        int result = timer.getSeconds();
        Assert.assertEquals(99, result, 0);
    }

    @Test
    public void testTimerReset() throws InterruptedException {
        Timer timer = new Timer();
        timer.reset();
        timer.start();
        sleep(2000);
        timer.reset();
        int result = timer.getSeconds();
        Assert.assertEquals(100, result, 0);
    }

    @Test
    public void testGetGameWon() {
        JLabel txtfield = new JLabel("");
        Timer timer = new Timer();
        minesweeper minesweeper = new minesweeper(txtfield, timer);
        boolean result = minesweeper.isGameWon();
        Assert.assertFalse(result);
    }

    @Test
    public void testScoreBoard()  {
        ScoreTable scoreboard = new ScoreTable();
        String ercsi[] = scoreboard.updateScoreTable("maxiTeszt", 999);
        Assert.assertEquals("maxiTeszt", ercsi[0]);
    }
}
