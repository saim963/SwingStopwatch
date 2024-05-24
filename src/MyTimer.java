import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MyTimer implements ActionListener {
    JFrame frame = new JFrame("Stopwatch");
    JLabel timeLabel = new JLabel("00:00:00");
    JButton startBtn = new JButton("Start");
    JButton resetBtn = new JButton("Reset");
    private boolean isRunning = false;
    private TimerThread timerThread;
    public MyTimer(){
        timeLabel.setFont(new Font("Arial",Font.PLAIN,30));

        timeLabel.setBounds(40,30,150,30);
        startBtn.setBounds(15,80,100,25);
        resetBtn.setBounds(115,80,100,25);

        startBtn.setFocusable(false);
        startBtn.addActionListener(this);
        resetBtn.setFocusable(false);
        resetBtn.addActionListener(this);

        frame.add(timeLabel);
        frame.add(startBtn);
        frame.add(resetBtn);
        frame.setLayout(null);
        frame.setSize(240,168);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startBtn) {
            if (!isRunning) {
                if (timerThread == null || !timerThread.isAlive()) {
                    timerThread = new TimerThread(this);
                    timerThread.start();
                } else {
                    timerThread.resumeTimer();
                }
                startBtn.setText("Pause");
            } else {
                timerThread.pauseTimer();
                startBtn.setText("Resume");
            }
            isRunning = !isRunning;
        } else if (e.getSource() == resetBtn) {
            if (timerThread != null) {
                timerThread.stopTimer();
                timerThread = null;
            }
            isRunning = false;
            startBtn.setText("Start");
            updateTimeLabel(0, 0, 0);
        }
    }
    void updateTimeLabel(int hr, int min, int sec){
        String hrStr = String.format("%02d",hr);
        String minStr = String.format("%02d",min);
        String secStr = String.format("%02d",sec);
        timeLabel.setText(hrStr+":"+minStr+":"+secStr);
    }

    public static void main(String[] args) {
        new MyTimer();
    }
}

class TimerThread extends Thread{
    private MyTimer myTimer;
    private boolean running =true;
    private boolean paused = false;
    private int hr = 0;
    private int min = 0;
    private int sec = 0;

    public TimerThread(MyTimer mytimer){this.myTimer = mytimer;}

    @Override
    public void run(){
        while(running){
            synchronized (this){
                while(paused){
                    try{
                        wait();
                    }catch(InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
            try{
                Thread.sleep(1000); // Update every second
                sec++;
                if (sec == 60) {
                    sec = 0;
                    min++;
                }
                if (min == 60) {
                    min = 0;
                    hr++;
                }
                myTimer.updateTimeLabel(hr, min, sec);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public synchronized void pauseTimer() {
        paused = true;
    }

    public synchronized void resumeTimer() {
        paused = false;
        notify();
    }

    public void stopTimer() {
        running = false;
        resumeTimer(); // Ensure the thread exits if it was paused
    }
}