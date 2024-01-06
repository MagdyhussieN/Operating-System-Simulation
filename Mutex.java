import java.util.LinkedList;
import java.util.Queue;

public class Mutex{

    Boolean IsLocked;
    String ResourceName;
    Queue<Integer> BlockedQueue;

    public Mutex(String x){
    	this.BlockedQueue = new LinkedList<>();
        this.ResourceName = x ;
        this.IsLocked = false;
    }

    /*public void semWait(String ResName) throws OSException{

        if(this.ResourceName.equals(ResName)){
            while (IsLocked) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    // Handle exception
                }
            }
            IsLocked = true;
        }
        else{
            throw new OSException("this resource name is not exist");
        }

       
    }

    public void semSignal(String x) {
        // TODO Auto-generated method stub
        if(this.ResourceName.equals(x)){
            IsLocked = false;
            notify();
            return;
        }

    }*/

    
    
}
