import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;
public class interpreter2 {

    int i; 

    private Vector<ArrayList<String>> CommandLine;
    public interpreter2(Vector<ArrayList<String>> CommandLine){
        this.CommandLine = CommandLine;
    }
    public interpreter2(){
        this.CommandLine = new Vector<ArrayList<String>>();
        this.i = 0;
    }

    public void run(String filename){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            ArrayList<String> x = new ArrayList<>();
            this.CommandLine.add(x);
            while ((line = reader.readLine()) != null) {
                this.CommandLine.get(i).add(line);
            }
            reader.close();
            i++;
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            System.exit(1);
        }
        i=0;
    }
    
    public  Vector<ArrayList<String>> getCommandLine() {
        return CommandLine;
    }

    public void setCommandLine(Vector<ArrayList<String>> commandLine) {
        CommandLine = commandLine;
    }
}

    

