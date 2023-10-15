package mins;

import javax.swing.*;

/** Countdown Timer */
public class Timer extends JLabel {
    /** Determines whether the timer should run or be stopped */
    private boolean canRun;
    /** Create a new thread */
    private Runnable countdown;
    /** Countdown starts from this value */
    private int seconds = 100;

    /** Returns the current value of seconds
     * @return The remaining seconds on the timer.
     */
    public int getSeconds() {
        return seconds;
    }

    /** Returns the value of canRun
     * @return Whether the timer is running or not
     */
    public boolean isRunning() {
        return canRun;
    }

    /** Sets the countdown timer. If a negative number is provided, it's set to 0
     * @param seconds The number of seconds to set the timer to
     */
    public void setSeconds(int seconds) {
        if(seconds > 0){
            this.seconds = seconds;
        } else {
            this.seconds = 0;
        }

        setText("" + seconds);
    }

    /** Countdown Timer Constructor */
    public Timer() {
        super("", SwingConstants.CENTER);
        countdown = new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
                while (canRun) {
                    setText(String.valueOf(seconds));
                    try {
                        Thread.sleep(1000);
                        seconds -= 1;
                    } catch (InterruptedException e) {
                    }
                    if (Integer.valueOf(getText()) == 0) {
                        canRun = false;
                        setText("Time's up");
                    }
                }
            }
        };
        canRun = false;
    }

    /** Starts (or resumes) the countdown timer */
    public void start() {
        canRun = true;
        new Thread(countdown).start();
    }

    /** Stops the countdown timer by setting canRun to false */
    public void stop() {
        canRun = false;
    }

    /** Resets the timer to its default value, and stops it by setting canRun to false */
    public void reset() {
        canRun = false;
        setText("" + seconds);
        seconds = 100;
    }
}
