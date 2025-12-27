package org.example.ThreadTutorial.BasicThread;

/**
 * Ini adalah Extend Thread Dasar di gunakan ketika
 * */
public class MyThread extends Thread{
    @Override
    public void run() {
        int i = 0;
        while (true){
            if (i > 10){
                break;
            }
            try {
                Thread.sleep(500);
                System.out.println("Running in with name : " + getName() +" kali-" +i);
                i++;
            } catch (InterruptedException e) {
               interrupted();
               break;
            }

        }
        System.out.println(getName() + " stopped Gracefully");

    }
}
