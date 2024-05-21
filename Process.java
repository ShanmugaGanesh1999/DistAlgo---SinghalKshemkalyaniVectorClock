import java.util.ArrayList;
import java.util.List;

/**
 * @author Shanmuga Ganesh
 *         class process is how a typical process looks like in the distributed
 *         environment
 */
public class Process {
    private int pid;
    private MatrixClock matrixClock;
    private List<List<Message>> channel; // current process's (local) channel

    /**
     * Constructor method
     * 
     * @param pid
     * @param n
     */
    public Process(int pid, int n) {
        setPid(pid);
        this.matrixClock = new MatrixClock(pid, n);
        this.channel = new ArrayList<>();
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public boolean isChannelEmpty() {
        return channel.isEmpty();
    }

    public void addToChannel(List<Message> channel) {
        this.channel.add(channel);
    }

    /**
     * this method is used to print the messages which is used for sending to other
     * process
     * 
     * @param messages
     * @param toPid
     */
    public void printMessages(List<Message> messages, int toPid) {
        String str = "messages of process " + this.pid + " -> " + toPid + " {";
        for (int i = 0; i < messages.size(); i++) {
            str += messages.get(i).toString();
            if (i != messages.size() - 1) {
                str += ",";
            }
        }
        System.out.println(str + "}\n");
    }

    /**
     * this is the main send method
     * 
     * @param toProcess
     */
    public void sendMessage(Process toProcess) {
        // Simulating internal event
        this.matrixClock.internalEventUpdate();

        List<Message> messageList = this.matrixClock.constructMessage(toProcess.getPid());

        System.out.println(
                "State " + this.matrixClock.getState() + " (before sending) for the p" + this.pid + "\n");
        this.matrixClock.printVC();
        printMessages(messageList, toProcess.getPid());

        // updating the last sent for the current process
        this.matrixClock.updateLastSent(toProcess.getPid());

        toProcess.addToChannel(messageList);

        toProcess.receiveMessage(toProcess);
    }

    /**
     * this is the main receive method
     * 
     * @param toProcess
     */
    public void receiveMessage(Process toProcess) {
        // Simulating internal event
        toProcess.matrixClock.internalEventUpdate();

        for (List<Message> messageList : toProcess.channel) {
            toProcess.matrixClock.updateMatrixClock(messageList);
        }

        System.out.println("State " + toProcess.matrixClock.getState() + " (after receiving) for the p"
                + toProcess.getPid() + "\n");
        toProcess.matrixClock.printVC();
        System.out.println("**********************************************************************\n");
    }
}