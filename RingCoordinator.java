import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;

public class RingCoordinator {
    public int[] messageBox;
    private int[] pIDmap;
    private Process[] processMap;
    private final int ringSize;
    private final ReentrantLock lock;
    private int messageCount = 0;
    //private int round = 0;
    public boolean leader = false;

    public static void main(String[] args) {
        try {
            File file = new File("input.dat");
            Scanner scanner = new Scanner(file);
            int ringSize = scanner.nextInt();
            RingCoordinator ringCoordinator = new RingCoordinator(ringSize);
            //ringCoordinator.readPIDs();
            for(int i = 0; i < ringSize; i++){
                ringCoordinator.pIDmap[i] = scanner.nextInt();
            }

            ringCoordinator.createProcesses();
            ringCoordinator.startProcesses();
            ringCoordinator.monitorProcesses();

            System.out.println("leader elected successfully");
        } catch (FileNotFoundException e) {
            System.out.println("file not found");
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public RingCoordinator(int ringSize){
        this.ringSize = ringSize;
        this.messageBox = new int[ringSize];
        Arrays.fill(messageBox, -1);
        this.pIDmap = new int[ringSize];
        lock = new ReentrantLock();
        processMap = new Process[ringSize];
    }

    private void createProcesses(){
        for(int i = 0; i < this.ringSize; i++){
            processMap[i] = new Process(i, pIDmap[i], this);
        }
    }

    private void startProcesses() throws InterruptedException {
        for(Process process : processMap){
            process.start();
        }
        Thread.sleep(1000);
    }

    private void monitorProcesses(){

        while(true){
            if(leader){
                System.out.println("Leader found -- from ring coordinator");
//                for(int i = 0; i < ringSize; i++){
//                    System.out.println(messageBox[i]);
//                }
                //System.exit(0);
                //leader = true;
                synchronized (lock){
                    lock.notifyAll();
                }
                break;
            }

            if(messageCount == ringSize){
                messageCount = 0;
                //round++;
                synchronized (lock){
                    lock.notifyAll();
                    Arrays.fill(messageBox, -1);
                }
            }
        }
    }

    public void sendMessage(int ringSequence, int message){
        int neighbor = (ringSequence + 1) % ringSize;
        messageBox[neighbor] = message;
        synchronized (lock){
            messageCount++;
        }
    }

    public ReentrantLock acquireLock(){
        return lock;
    }
}
