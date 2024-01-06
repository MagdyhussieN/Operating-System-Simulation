import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;

public class Process implements Serializable{

    private int Process_ID;
    private  ProcessState Process_State;
    private int Program_Counter;
    private int[] Memory_Boundaries;
    private ArrayList<String> instructions;
    private ArrayList<Object> PCB;
    private Object[] tempvars ;
    private Hashtable<String,String> VariablesKeys; // <1(first variable) , B > 
  

    private String fileName; // file name for the CSV file


    
    
    // public Process(int process_ID, int[] memory_Boundaries) {
    //     this.PCB = new ArrayList<>();
    //     Process_ID = process_ID;
    //     Process_State = ProcessState.New;
    //     Program_Counter = 0;
    //     Memory_Boundaries = memory_Boundaries;
    //     this.instructions = new ArrayList<>();
        

    

	


	// }
    public Process(int id,ProcessState state,int pc,int[] arr,Hashtable<String,String> var,String[] arr2) {
    	this.Process_ID = id;
    	this.Process_State = state;
    	this.Program_Counter = pc;
    	this.Memory_Boundaries = arr;
    	this.VariablesKeys = var;
    	this.tempvars = arr2;
    }
    public Process() throws OSException{
    	this.VariablesKeys = new Hashtable<>();
    			
    	this.tempvars = new Object[3];
    	/*this.tempvars[0] = null;
    	this.tempvars[1] = null;
    	this.tempvars[2] = null;*/
        this.PCB = new ArrayList<>();
        Process_State = ProcessState.New;
        Program_Counter = 0;
        this.instructions = new ArrayList<>();
        this.fileName = "src/resources/Process/Process"+Process_ID + ".class"; // set the file name
        //this.SeralizeProcess(fileName);
        PCB.add(this.Process_ID);
        PCB.add(this.Program_Counter);
        PCB.add(this.Memory_Boundaries);
        PCB.add(this.Process_State);
    }

   
    public void HandleVariables() {
    	Boolean flag1 = false;
    	Boolean flag2 = false;
    	Boolean flag3 = false;
    	for(int i=0;i<this.instructions.size();i++) {
    		String instruction = this.getInstructions().get(i);
        	String[] splitArray = instruction.split(" ");
        	List<String> list = new ArrayList<>(Arrays.asList(splitArray));
        	switch (list.get(0)) {
        	case "assign" :  if(flag1 == false && !this.VariablesKeys.containsValue(list.get(1))) {
        		this.VariablesKeys.put("1", list.get(1));
        		flag1 = true;
        	}
        	else if(flag2 == false  && !this.VariablesKeys.containsValue(list.get(1))) {
        		this.VariablesKeys.put("2", list.get(1));
        		flag2 = true;
        	}
        	else if(flag3 == false && !this.VariablesKeys.containsValue(list.get(1))) {
        		this.VariablesKeys.put("3", list.get(1));
        		flag3 = true;
        	}
        	}
    	}
    }
    
    public Hashtable<String, String> getVariablesKeys() {
		return VariablesKeys;
	}


	public void setVariablesKeys(Hashtable<String, String> variablesKeys) {
		VariablesKeys = variablesKeys;
	}
    

    public int getProcess_ID() {
        return Process_ID;
    }
    public void setProcess_ID(int process_ID) {
        Process_ID = process_ID;
    }
    public ProcessState getProcess_State() {
        return Process_State;
    }
    public void setProcess_State(ProcessState state) {
        Process_State = state;
    }
    public int getProgram_Counter() {
        return Program_Counter;
    }
    public void setProgram_Counter(int program_Counter) {
        Program_Counter = program_Counter;
    }
    public int[] getMemory_Boundaries() {
        return Memory_Boundaries;
    }
    public void setMemory_Boundaries(int[] memory_Boundaries) {
        Memory_Boundaries = memory_Boundaries;
    }
    public ArrayList<String> getInstructions() {
        return instructions;
    }
    public void setInstructions(ArrayList<String> instruction) {
        this.instructions =  instruction;
    }

    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public ArrayList<Object> getPCB() {
        return PCB;
    }
    public void setPCB(ArrayList<Object> pCB) {
        PCB = pCB;
    }
    public Object[] getTempvars() {
		return tempvars;
	}


	public void setTempvars(Object[] tempvars) {
		this.tempvars = tempvars;
	}


    public void print(Object x){
        System.out.println(x.toString());
    }

    public static ArrayList<String> readFile(String filePath) {
    	ArrayList<String> temp = new ArrayList<>();
        try {
            File file = new File(filePath);
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                //System.out.println(line);
            	if(line.startsWith("PCB"))
            		break;
            	temp.add(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return temp;
    }

    public static void writeFile(String filePath, String data) {
        try {
            File file = new File(filePath);
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(data);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void assignVariable(Object variable, String value) throws OSException{
        // Perform variable assignment logic here
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter your input");
        String assignmentStatement = scanner.nextLine();

        try {
            variable =  Integer.parseInt(assignmentStatement); 
        } catch (NumberFormatException e) {
        	try {
                variable = (String) assignmentStatement;
            } catch (NumberFormatException e2) {
                throw new OSException("this is invalid data type");
            }

        }
    }

    public ArrayList<Integer>  printFromTo(String xs, String ys )throws OSException{
    	// we need to throw exception here
    	ArrayList<Integer> result = new ArrayList<>();
        int x = 0;
        int y = 0;
        
        try {
            x = Integer.parseInt(xs);
            y = Integer.parseInt(ys);
        } catch (Exception e) {
            throw new OSException("invalid data type");
        }
        
        //System.out.println("x: " + x + " y: " + y);

        if (x <= y) {
            for (int i = x; i <= y; i++) {
                result.add(i);
                System.out.print(i + " ");
            }
        } else {
            for (int i = x; i >= y; i--) {
                result.add(i);
                System.out.print(i + " ");
            }
        }

        return result;

    }
    public void semWait(Mutex Resource){

        if(Resource.IsLocked == true){
        	Resource.BlockedQueue.add(this.Process_ID);      
        }
        else{
        	if(Resource.BlockedQueue.contains(this.Process_ID)) {
        		Resource.BlockedQueue.remove(this.Process_ID);
        	}
        	Resource.IsLocked = true;
        }

       
    }

    public void semSignal(Mutex Resource) {
        // TODO Auto-generated method stub
            Resource.IsLocked = false;
            //notify();
            return;      

    }


}
