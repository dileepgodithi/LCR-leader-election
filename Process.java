public class Process extends Thread{
    private int potentialLeader;
    private int ringSequence;
    private int myId;
    private RingCoordinator ringCoordinator;

    public Process(int ringSequence, int myId, RingCoordinator ringCoordinator) {
        this.ringSequence = ringSequence;
        this.myId = myId;
        this.ringCoordinator = ringCoordinator;
        this.potentialLeader = myId;
    }

    public void run() {
        //read message
        //synchronized (ringCoordinator.acquireLock()){
        if(ringCoordinator.leader){
            if(myId == potentialLeader){
                System.out.println("I am the leader with id: "
                        + potentialLeader + ". My sequence in ring: " + ringSequence);
                return;
            }
            System.out.println("Leader found with id: " + potentialLeader
                    + ". My id: "+ myId + ". My sequence in ring: " + ringSequence);
            return;
        }
        //}

        //System.out.println("Running " + ringSequence);
        if(ringCoordinator.messageBox[ringSequence] != -1){
            int message = ringCoordinator.messageBox[ringSequence];
            if(message < potentialLeader)
                potentialLeader = message;
            else if(message == myId){
                synchronized (ringCoordinator.acquireLock()){
                    ringCoordinator.leader = true;
                    //return;
                }
            }
        }

        ringCoordinator.sendMessage(ringSequence, potentialLeader);
        //System.out.println("Send message");
        synchronized (ringCoordinator.acquireLock()){
//            int neighbor = (ringSequence + 1) %
//            ringCoordinator.messageBox[] = potentialLeader;
            try {
                ringCoordinator.acquireLock().wait();
                run();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
