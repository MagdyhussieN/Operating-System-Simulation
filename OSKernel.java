import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.Vector;

public class OSKernel implements Serializable{
	
	public Object tempforAssign1 = "";
	public Object tempforAssign2 = "";
	public Object tempforAssign3 = "";

    private int processIDCounter = 1;
    
    public Queue<Integer> Ready;
    public Queue<Integer> Blocked;
    //public ArrayList<Process> ListOfProcess;
    
    public int[] MemoryProcess;

    private Object[] memory;
    interpreter2 InterpreterOS;
    public int PCBCount = 0;
    Boolean P1 = false;
    Boolean P2 = false;
    Boolean P3 = false;
    
    Mutex UserInput;
    Mutex FileAccess;
    Mutex OutputScreen;
    

    ArrayList<String> Process_Control_Path;
    Hashtable <Integer,String> ProcessArrival;
    public OSKernel() throws OSException, IOException{
    	this.UserInput = new Mutex("userInput");
    	this.FileAccess = new Mutex("file");
    	this.OutputScreen = new Mutex("userOutput");
    	//this.ListOfProcess = new ArrayList<>();
    	this.Blocked = new LinkedList<>();
    	this.MemoryProcess = new int[2];
    	this.Ready = new LinkedList<>();
        this.ProcessArrival = new Hashtable<>();
        this.Process_Control_Path = new ArrayList<>();
        this.memory = new Object[40];
        init();
    }


    

    public void init() throws OSException, IOException{



        // for start OS with send filename of programs to interpreter 
        InterpreterOS = new interpreter2();
       

        // create three processes 

        Scanner scanner = new Scanner(System.in);

        System.out.println("Please enter the arrival time for the first process:");
        int arrivalTime1 = scanner.nextInt();

        System.out.println("Please enter the arrival time for the second process:");
        int arrivalTime2 = scanner.nextInt();

        System.out.println("Please enter the arrival time for the third process:");
        int arrivalTime3 = scanner.nextInt();
        
        
        

        int cycle =0;
        
        
        int RR = 0;
        Boolean AssignFlag1 = false;
        Boolean AssignFlag2 = false;
        Boolean AssignFlag3 = false;
        
        int idprevremove =0;
        ArrayList<Process> ListOfProcess = new ArrayList<>();

        while(true){
        	
        	
        if(cycle <arrivalTime1 && cycle < arrivalTime2 && cycle< arrivalTime3) {
        	cycle++;
        }
        else {
            if(P1 == true && P2 == true && P3 == true){
                return;
            }
            
            if(cycle == arrivalTime1){
                InterpreterOS.run("/Users/magdyhussien/Desktop/project OS 2/OperatingSystem/src/Program_1.txt");
                Process process = AllocatePCB(arrivalTime1);
                this.Ready.add(process.getProcess_ID());
                ListOfProcess.add(process);
                

            }
            if(cycle == arrivalTime2 ){
                InterpreterOS.run("/Users/magdyhussien/Desktop/project OS 2/OperatingSystem/src/Program_2.txt"); 
                Process process = AllocatePCB(arrivalTime2);
                this.Ready.add(process.getProcess_ID());
                ListOfProcess.add(process);
                
                

            }
            if(cycle == arrivalTime3){
            
                InterpreterOS.run("/Users/magdyhussien/Desktop/project OS 2/OperatingSystem/src/Program_3.txt");
                Process process = AllocatePCB(arrivalTime3);
                this.Ready.add(process.getProcess_ID());
                ListOfProcess.add(process);
            }
            
            //ArrayList<Process> ListOfProcesses = null;
        	
        	
            int readytoexecute = 0;
            
            if(!this.Ready.isEmpty())
            	readytoexecute = this.Ready.peek();
            
            
            ArrayList<String> Instruction =ReadFromCsv("Process"+readytoexecute);
            int PositionOfPC = FindPCB(readytoexecute);
            int PC = (int)this.memory[PositionOfPC];
            Process ProcessReadyToExecute = null;
            for(int i=0;i<ListOfProcess.size();i++) {
//            	
//            	System.out.println(ListOfProcess.get(i).getProcess_ID());
//            	System.out.println(readytoexecute);
//            	System.out.println(Ready.peek());
            	if(ListOfProcess.get(i).getProcess_ID() == readytoexecute) {
            		
            		//ListOfProcesses = FillListOfProcesses(cycle,arrivalTime1,arrivalTime2,arrivalTime3);
            		ProcessReadyToExecute = ListOfProcess.get(i);
            		ProcessReadyToExecute.setInstructions(Instruction);
            	
            }
            }
            
        	
            

            
            String instruction = ProcessReadyToExecute.getInstructions().get(ProcessReadyToExecute.getProgram_Counter());
        	String[] splitArray = instruction.split(" ");
        	List<String> list = new ArrayList<>(Arrays.asList(splitArray));
            if(this.MemoryProcess[0] != readytoexecute && this.MemoryProcess[1] != readytoexecute) {
            	//System.out.println("length of ready queue :" +this.Ready.size());
            	if(this.Ready.size()!=1) {
            		int lastElement = -1;
            		for (int element : this.Ready) {
            		    lastElement = element;
            		}
            		
            		swapping(readytoexecute,lastElement,ListOfProcess);
            	}
            	else if(this.Blocked.size()!=0) {
            	
            		swapping(readytoexecute,this.Blocked.peek(),ListOfProcess);

            	}
            	else {
            	
            		swapping(readytoexecute,idprevremove,ListOfProcess);
            	}
            }
            
            ProcessReadyToExecute.setProcess_State(ProcessState.Running);
            HandleProcessesStates(ProcessReadyToExecute,this.Ready,this.Blocked,ListOfProcess);

            	if(RR == 2) {
            		int temp = this.Ready.remove();
            		this.Ready.add(temp);
            		RR=0;
            	}
            	else {
            		// execute system call 
            		if(ProcessReadyToExecute.getProcess_ID() == 1) {
            			Boolean flag2= ExecuteSystemCall(ProcessReadyToExecute,RR,AssignFlag1);
                		AssignFlag1 = flag2;
            		}
            		else if(ProcessReadyToExecute.getProcess_ID() == 2) {
            			Boolean flag2= ExecuteSystemCall(ProcessReadyToExecute,RR,AssignFlag2);
                		AssignFlag2 = flag2;
            		}
            		else {
            			Boolean flag2= ExecuteSystemCall(ProcessReadyToExecute,RR,AssignFlag3);
                		AssignFlag3 = flag2;
            		}
            		
            		
            			if(ProcessReadyToExecute.getProcess_ID() == 1 && AssignFlag1 == true)
            				PC--;	
            			else if(ProcessReadyToExecute.getProcess_ID() == 2 && AssignFlag2 == true)
            				PC--;
            			else if(ProcessReadyToExecute.getProcess_ID() == 3 && AssignFlag3 == true)
            				PC--;
            			else if(this.Blocked.contains(ProcessReadyToExecute.getProcess_ID()))
            				PC--;
            		
            		/*if(ProcessReadyToExecute.getProcess_ID() == 1) {
                		System.out.println(PC + "before");
                	}*/
            		RR++;
            		PC++;
            		
            	}	
            if(PC ==  ProcessReadyToExecute.getInstructions().size()) {
            	if(ProcessReadyToExecute.getProcess_ID()==1)
            		this.P1 = true;
            	else if(ProcessReadyToExecute.getProcess_ID()==2) {
            		this.P2 = true;
            	}
            	else {
            		this.P3 = true;
            	}
            }
            if(!list.get(0).equals("assign")) {
            	if(ProcessReadyToExecute.getProcess_ID()==1) {
            		AssignFlag1 = false;
            	}
            	else if(ProcessReadyToExecute.getProcess_ID()==2) {
            		AssignFlag2 = false;
            	}
            	else {
            		AssignFlag3 = false;
            	}
            	
            	//System.out.print(AssignFlag);
            	
            }
            	/*if(ProcessReadyToExecute.getProcess_ID() == 1) {
            		System.out.println(PC + "last ");
            	}*/
            	ProcessReadyToExecute.setProgram_Counter(PC);
            	this.memory[PositionOfPC]= PC;
            	cycle++;
            	//System.out.print(cycle);
            	
            	if(P1 == true) {
            		idprevremove=1;
                	RemoveFormReady(1);
                }
                if(P2 == true) {
                	idprevremove=2;
                	RemoveFormReady(2);
                }
                if(P3 == true) {
                	idprevremove=3;
                	RemoveFormReady(3);
                }
                for(int i=0 ; i< ListOfProcess.size();i++) {
            		if(ListOfProcess.get(i).getProcess_ID() == ProcessReadyToExecute.getProcess_ID() ) {
            			if(ListOfProcess.get(i).getProgram_Counter() == ListOfProcess.get(i).getInstructions().size()) {
            				ProcessReadyToExecute.setProcess_State(ProcessState.Finished);
            			}
            		}
            			
            	}
                updateTextFile(ProcessReadyToExecute);
            	
            
        }
        }
        
        
        
            // process want to execute and located in the disk 
            
            
            
            
     
        

        //AllocateInstruction(arrivalTime1,arrivalTime2,arrivalTime3);


        
        /*-------------------scheduling and start executing ---------------------------
        -------------------------------------------------------------------------------*/    
        
    }
    
    private ArrayList<Process> FillListOfProcesses(int cycle, int arrivalTime1 , int arrivalTime2 ,int arrivalTime3) {
    	//System.out.println("cycle :- " + cycle + " arrival :- " + arrivalTime1);
    	ArrayList<Process> result = new ArrayList<>();
    	if(cycle >= arrivalTime1) {
    		List<String> PCB = this.ReadPCBFromCsv("Process1");
    		int id =0 ;
    		int pc=0;
    		//ProcessState state = ProcessState.New;
    		int[] arr = new int[2];
    		Hashtable<String,String> var = new Hashtable<>();
    		String[] arr2 = new String[3];
    		int j=0;
    		
    		for(int i=0;i<PCB.size();i++) {
    			String info = PCB.get(i);
    	    	String[] splitArray = info.split(" ");
    	    	List<String> list = new ArrayList<>(Arrays.asList(splitArray));
    	    	switch (list.get(0)) {
    	    	case "ID" :
    	    		id = Integer.parseInt(list.get(1));
    	    		//process.setProcess_ID(Integer.parseInt(list.get(1)));
    	    		break;
    	    	case "State" : 
    	    			j=i;
    	    			
    	    		//process.setProcess_State(x);
    	    		break;
    	    	case "ProgramCounter" : pc = Integer.parseInt(list.get(1));
    	    	
    	    		//process.setProgram_Counter(Integer.parseInt(list.get(1)));
    	    	break;
    	    	case "MemoryBoundries" :
    	    		arr[0] = Integer.parseInt(list.get(1));
    	    		arr[1] = Integer.parseInt(list.get(2));
    	    		
    	    		//arr = {Integer.parseInt(list.get(1)) , Integer.parseInt(list.get(2))};
    	    		//process.setMemory_Boundaries(arr);
    	    		break;
    	    	case "VariablesKey" :
    	    		
    	    		var.put("1", list.get(1));
    	    		var.put("2", list.get(2));
    	    		var.put("3", list.get(3));
    	    		break;
    	    	case "VariablesValue" : 
    	    			arr2[0] = list.get(1);
    	    			arr2[1] = list.get(2);
    	    			arr2[2] = list.get(3);

    	    		//process.setTempvars(arr2);
    	    	break;
    	    	//default: continue;
    	    	}
    	    	
    			
    		}
    		String info = PCB.get(j);
	    	String[] splitArray = info.split(" ");
	    	List<String> list = new ArrayList<>(Arrays.asList(splitArray));
	    	ProcessState state = getStateFromString(list.get(1));
    		Process process = new Process(id,state,pc,arr,var,arr2);
    		result.add(process);
    	
    	}
    	if(cycle >= arrivalTime2) {
    		List<String> PCB = this.ReadPCBFromCsv("Process2");
    		int id =0 ;
    		int pc=0;
    		//ProcessState state = ProcessState.New;
    		int[] arr = new int[2];
    		Hashtable<String,String> var = new Hashtable<>();
    		String[] arr2 = new String[3];
    		int j=0;
    		
    		for(int i=0;i<PCB.size();i++) {
    			String info = PCB.get(i);
    	    	String[] splitArray = info.split(" ");
    	    	List<String> list = new ArrayList<>(Arrays.asList(splitArray));
    	    	switch (list.get(0)) {
    	    	case "ID" : 
    	    		id = Integer.parseInt(list.get(1));
    	    		//process.setProcess_ID(Integer.parseInt(list.get(1)));
    	    		break;
    	    	case "State" : 
    	    			j=i;
    	    			
    	    		//process.setProcess_State(x);
    	    		break;
    	    	case "ProgramCounter" : pc = Integer.parseInt(list.get(1));
    	    		//process.setProgram_Counter(Integer.parseInt(list.get(1)));
    	    	break;
    	    	case "MemoryBoundries" :
    	    		arr[0] = Integer.parseInt(list.get(1));
    	    		arr[1] = Integer.parseInt(list.get(2));
    	    		//arr = {Integer.parseInt(list.get(1)) , Integer.parseInt(list.get(2))};
    	    		//process.setMemory_Boundaries(arr);
    	    		break;
    	    	case "VariablesKey" :
    	    		var.put("1", list.get(1));
    	    		var.put("2", list.get(2));
    	    		var.put("3", list.get(3));
    	    		break;
    	    	case "VariablesValue" : 
    	    			arr2[0] = list.get(1);
    	    			arr2[1] = list.get(2);
    	    			arr2[2] = list.get(3);
    	    		//process.setTempvars(arr2);
    	    			break;
    	    	}
    	    	
    			
    		}
    		String info = PCB.get(j);
	    	String[] splitArray = info.split(" ");
	    	List<String> list = new ArrayList<>(Arrays.asList(splitArray));
	    	ProcessState state = getStateFromString(list.get(1));
    		Process process = new Process(id,state,pc,arr,var,arr2);
    		result.add(process);
    	}
    	if(cycle >= arrivalTime3) {
    		List<String> PCB = this.ReadPCBFromCsv("Process3");
    		int id =0 ;
    		int pc=0;
    		//ProcessState state = ProcessState.New;
    		int[] arr = new int[2];
    		Hashtable<String,String> var = new Hashtable<>();
    		String[] arr2 = new String[3];
    		int j=0;
    		
    		for(int i=0;i<PCB.size();i++) {
    			String info = PCB.get(i);
    	    	String[] splitArray = info.split(" ");
    	    	List<String> list = new ArrayList<>(Arrays.asList(splitArray));
    	    	switch (list.get(0)) {
    	    	case "ID" : //System.out.println(list.get(0) + "         " + list.get(1));
    	    		id = Integer.parseInt(list.get(1));
    	    		//process.setProcess_ID(Integer.parseInt(list.get(1)));
    	    		break;
    	    	case "State" : 
    	    			j=i;
    	    			
    	    		//process.setProcess_State(x);
    	    		break;
    	    	case "ProgramCounter" : pc = Integer.parseInt(list.get(1));
    	    		//process.setProgram_Counter(Integer.parseInt(list.get(1)));
    	    	break;
    	    	case "MemoryBoundries" :
    	    		arr[0] = Integer.parseInt(list.get(1));
    	    		arr[1] = Integer.parseInt(list.get(2));
    	    		//arr = {Integer.parseInt(list.get(1)) , Integer.parseInt(list.get(2))};
    	    		//process.setMemory_Boundaries(arr);
    	    		break;
    	    	case "VariablesKey" :
    	    		var.put("1", list.get(1));
    	    		var.put("2", list.get(2));
    	    		var.put("3", list.get(3));
    	    		break;
    	    	case "VariablesValue" : arr2[0] = list.get(1);
    	    			arr2[1] = list.get(2);
    	    			arr2[2] = list.get(3);
    	    		//process.setTempvars(arr2);break;
    	    	}
    	    	
    			
    		}
    		String info = PCB.get(j);
	    	String[] splitArray = info.split(" ");
	    	List<String> list = new ArrayList<>(Arrays.asList(splitArray));
	    	ProcessState state = getStateFromString(list.get(1));
    		Process process = new Process(id,state,pc,arr,var,arr2);
    		result.add(process);
    	}
    	return result;
    	
    	
    }
    
    private void HandleProcessesStates(Process processReadyToExecute, Queue<Integer> ready2, Queue<Integer> blocked2,ArrayList<Process> ListOfProcess) {
		// TODO Auto-generated method stub
    	for(int i=0;i< ListOfProcess.size();i++) {
    		if(ready2.contains(ListOfProcess.get(i)) && processReadyToExecute.getProcess_ID() != ListOfProcess.get(i).getProcess_ID())
    			ListOfProcess.get(i).setProcess_State(ProcessState.Ready);
    		if(blocked2.contains(ListOfProcess.get(i)) && processReadyToExecute.getProcess_ID() != ListOfProcess.get(i).getProcess_ID())
    			ListOfProcess.get(i).setProcess_State(ProcessState.Blocked);
    		
    	}
    	
		
		
	}




	public void RemoveFormReady(int x) {
    	
		for (int i =0 ; i<this.Ready.size() ; i++) {
		    if(this.Ready.peek() == x) {
		    	this.Ready.remove();
		    	i++;
		    }
		    else
		    	this.Ready.add(this.Ready.remove());
		}
		for (int i =0 ; i<this.Blocked.size() ; i++) {
		    if(this.Blocked.peek() == x) {
		    	this.Blocked.remove();
		    	i++;
		    }
		    else
		    	this.Blocked.add(this.Blocked.remove());
		}
    }
    
    public void PrintQueue(Queue Ready) {
    	Queue<Integer> tempQueue = new LinkedList<>(Ready);
        
        // Print elements from the tempQueue
        while (!tempQueue.isEmpty()) {
            int element = tempQueue.poll();
            System.out.println(element);
        }
        
     
    }
    
    public Boolean ExecuteSystemCall(Process processReadyToExecute , int RR , Boolean flag) throws IOException, OSException {
		// TODO Auto-generated method stub
    	String instruction = processReadyToExecute.getInstructions().get(processReadyToExecute.getProgram_Counter());
    	String[] splitArray = instruction.split(" ");
    	List<String> list = new ArrayList<>(Arrays.asList(splitArray));
    	String operation = list.get(0);
    	
    	switch (operation) {
    	case "print": 
    		
    		
    		Object variable =null;
    		if(list.get(1).equals(processReadyToExecute.getVariablesKeys().get("1"))) {
    			int pos = getindexofmemory(processReadyToExecute.getVariablesKeys().get("1"),(processReadyToExecute.getMemory_Boundaries()[1]),processReadyToExecute);
    			variable = this.memory[pos];
    	}else if(list.get(1).equals(processReadyToExecute.getVariablesKeys().get("2"))) {
    			int pos = getindexofmemory(processReadyToExecute.getVariablesKeys().get("2"),(processReadyToExecute.getMemory_Boundaries()[1]),processReadyToExecute);
    			
    			variable = this.memory[pos];
    	}
    	else {
    			int pos = getindexofmemory(processReadyToExecute.getVariablesKeys().get("3"),(processReadyToExecute.getMemory_Boundaries()[1]),processReadyToExecute);
    			variable = this.memory[pos];
    	}
    		processReadyToExecute.print(variable);
    		//System.out.println(variable);
    		break;
    		
    	case "printFromTo": 
    		String variable1 ="";
    	    String variable2 ="";
    		if(list.get(1).equals(processReadyToExecute.getVariablesKeys().get("1"))) {
    			int pos = getindexofmemory(processReadyToExecute.getVariablesKeys().get("1"),(processReadyToExecute.getMemory_Boundaries()[1]),processReadyToExecute);
    			variable1 = (String)this.memory[pos];
    			
    			
    		}else if(list.get(1).equals(processReadyToExecute.getVariablesKeys().get("2"))) {
    			int pos = getindexofmemory(processReadyToExecute.getVariablesKeys().get("2"),(processReadyToExecute.getMemory_Boundaries()[1]),processReadyToExecute);
    			variable1 = (String)this.memory[pos];
    			//System.out.print(pos + "   " + this.memory[pos]);
    		}
    		else {
    			int pos = getindexofmemory(processReadyToExecute.getVariablesKeys().get("3"),(processReadyToExecute.getMemory_Boundaries()[1]),processReadyToExecute);
    			variable1 = (String)this.memory[pos];
    			//System.out.print(pos + "   " + this.memory[pos]);
    		}
    		if(list.get(2).equals(processReadyToExecute.getVariablesKeys().get("1"))) {
    			int pos = getindexofmemory(processReadyToExecute.getVariablesKeys().get("1"),(processReadyToExecute.getMemory_Boundaries()[1]),processReadyToExecute);
    			variable2 = (String)this.memory[pos];
    		}else if(list.get(2).equals(processReadyToExecute.getVariablesKeys().get("2"))) {
    			int pos = getindexofmemory(processReadyToExecute.getVariablesKeys().get("2"),(processReadyToExecute.getMemory_Boundaries()[1]),processReadyToExecute);
    			variable2 = (String)this.memory[pos];
    		}
    		else {
    			int pos = getindexofmemory(processReadyToExecute.getVariablesKeys().get("3"),(processReadyToExecute.getMemory_Boundaries()[1]),processReadyToExecute);
    			variable2 = (String)this.memory[pos];
    		}
    		
    		
    		
    		//System.out.println("print moshklaaa :-" + "Variable1 :- "+variable1 + "variable2:- " + variable2);
    		
    		ArrayList<Integer> temp = processReadyToExecute.printFromTo(variable1, variable2);
    		//System.out.println("temp is : -   " +temp.size());
    		//for(int i=0; i<temp.size();i++) {
    		//	System.out.println(i + ":- " + temp.get(i));
    		//}
    		
    		break;
    	case "assign":
    		if(flag ==false) {
    			String secndoperand = list.get(2);
    			String value ="";
    			if(secndoperand.equals("input")) {
    				Scanner scanner = new Scanner(System.in);
    		        System.out.println("Please enter the value:");
    		        value = scanner.nextLine(); 
    		       
    		       
    		        
    		        if(processReadyToExecute.getProcess_ID()==1) {
    		        	this.tempforAssign1 = value;
    		        }
    		        else if(processReadyToExecute.getProcess_ID()==2) {
    		        	this.tempforAssign2 = value;
    		        }
    		        else {
    		        	this.tempforAssign3 = value;
    		        }
    			}
    			
    			else {
    				//System.out.println(this.memory[]);
    				String var ="";
    				//System.out.println("moshklaaa" + list.get(3));
    				//System.out.println(processReadyToExecute.getMemory_Boundaries().toString());
    	    	if(list.get(3).equals(processReadyToExecute.getVariablesKeys().get("1"))) {
    	    			//System.out.println("moshklaaa" + list.get(3));
    	    			int pos = getindexofmemory(processReadyToExecute.getVariablesKeys().get("1"),(processReadyToExecute.getMemory_Boundaries()[1]),processReadyToExecute);
    	    			//System.out.println(pos+"a7aaaaaa");
    	    			//System.out.println(this.memory[pos].toString()+"a7aaaaaa");
    	    			var = (String)this.memory[pos];
    	    			
    	    			
    	    			
    	    			
    	    			
    	    	}else if(list.get(3).equals(processReadyToExecute.getVariablesKeys().get("2"))) {
    	    		int pos = getindexofmemory(processReadyToExecute.getVariablesKeys().get("2"),(processReadyToExecute.getMemory_Boundaries()[1]),processReadyToExecute);
    	    		var = (String)this.memory[pos];
    	    	}
    	    	else {
    	    		int pos = getindexofmemory(processReadyToExecute.getVariablesKeys().get("3"),(processReadyToExecute.getMemory_Boundaries()[1]),processReadyToExecute);
    	    		var = (String)this.memory[pos];
    	    	}
    	    		//System.out.println(var +"meegomeggo");
    				ArrayList<String> ValueFromReadFile =processReadyToExecute.readFile(var);
    		        flag = true;
    		        if(processReadyToExecute.getProcess_ID()==1) {
    		        	this.tempforAssign1 = ValueFromReadFile;
    		        }
    		        else if(processReadyToExecute.getProcess_ID()==2) {
    		        	this.tempforAssign2 = ValueFromReadFile;
    		        }
    		        else {
    		        	this.tempforAssign3 = ValueFromReadFile;
    		        }
    			}
    			flag = true;
    		}
    				
    			
    		
    		else {
    			
    			if(list.get(1).equals(processReadyToExecute.getVariablesKeys().get("1"))) {
    				//System.out.println("in : "+ "a");
    				int pos = getindexofmemory(processReadyToExecute.getVariablesKeys().get("1"),(processReadyToExecute.getMemory_Boundaries()[1]),processReadyToExecute);
    				
    	
    				if(processReadyToExecute.getProcess_ID()==1) {
    
    					this.memory[pos] = this.tempforAssign1;
    					
    					
    		        }
    		        else if(processReadyToExecute.getProcess_ID()==2) {
    		        	this.memory[pos] = this.tempforAssign2;
    		        	
    		        }
    		        else {
    		        	this.memory[pos] = this.tempforAssign3;
    		        }
    				
    				Object[] arr = {this.memory[pos],processReadyToExecute.getTempvars()[1],processReadyToExecute.getTempvars()[2]};
    				processReadyToExecute.setTempvars(arr);
    				//processReadyToExecute.getTempvars()[0] = this.memory[pos];
    		  
    				
    			}
    			else if(list.get(1).equals(processReadyToExecute.getVariablesKeys().get("2"))) {
    			int pos = getindexofmemory(processReadyToExecute.getVariablesKeys().get("2"),(processReadyToExecute.getMemory_Boundaries()[1]),processReadyToExecute);
    		
    			if(processReadyToExecute.getProcess_ID()==1) {
					this.memory[pos] = this.tempforAssign1;
		        }
		        else if(processReadyToExecute.getProcess_ID()==2) {
		        	this.memory[pos] = this.tempforAssign2;
		        }
		        else {
		        	this.memory[pos] = this.tempforAssign3;
		        }
    			
    		

    			//processReadyToExecute.getTempvars()[1] =  (Object) this.memory[pos];
    			Object[] arr = {processReadyToExecute.getTempvars()[0],this.memory[pos],processReadyToExecute.getTempvars()[2]};
				processReadyToExecute.setTempvars(arr);
    			

    		}
    		else {
    			int pos = getindexofmemory(processReadyToExecute.getVariablesKeys().get("3"),(processReadyToExecute.getMemory_Boundaries()[1]),processReadyToExecute);
    			if(processReadyToExecute.getProcess_ID()==1) {
					this.memory[pos] = this.tempforAssign1;
		        }
		        else if(processReadyToExecute.getProcess_ID()==2) {
		        	this.memory[pos] = this.tempforAssign2;
		        }
		        else {
		        	this.memory[pos] = this.tempforAssign3;
		        }
    			processReadyToExecute.getTempvars()[2] = this.memory[pos];

    		}
    			flag =false;
    			//if(processReadyToExecute.getProcess_ID()==3 && processReadyToExecute.getProgram_Counter()==1)
    				//System.out.print(this.memory[23].toString()+"abdoadham");
    		
    		}break;
    	case "semSignal" :
    		
    		String name = list.get(1);
    		int temp2=0;
    		if(name.equals("userInput")) {
    			processReadyToExecute.semSignal(this.UserInput);
    			if(this.UserInput.BlockedQueue.size()!=0) {
    				temp2 = UserInput.BlockedQueue.remove();
    			this.Ready.add(temp2);
    			}
    			
    			
    		}
    		
    		else if(name.equals("file")) {
    			processReadyToExecute.semSignal(this.FileAccess);
    			if(this.FileAccess.BlockedQueue.size()!=0) {
    				temp2 = FileAccess.BlockedQueue.remove();
    			this.Ready.add(temp2);
    			}
    			
    		}
    		else if(name.equals("userOutput")){
    			
    			processReadyToExecute.semSignal(this.OutputScreen);
    			if(this.OutputScreen.BlockedQueue.size()!=0) {
    				temp2 = OutputScreen.BlockedQueue.remove();
    			this.Ready.add(temp2);
    			}
    		} 
    		//this.Ready.add(temp2);
    		for (int i =0 ; i<this.Blocked.size() ; i++) {
    		    if(this.Blocked.peek() == temp2) {
    		    	this.Blocked.remove();
    		    	i++;
    		    }
    		    else
    		    	this.Blocked.add(this.Blocked.remove());
    		}
    		break;
    		
    		
       	case "semWait":
       		
    		String name2 = list.get(1);
    		Boolean flagg = false;
		if(name2.equals("userInput")) {
			processReadyToExecute.semWait(this.UserInput);
			if(this.UserInput.BlockedQueue.contains(processReadyToExecute.getProcess_ID())) {
				this.Blocked.add(processReadyToExecute.getProcess_ID());
				this.Ready.remove();
				flagg = true;
				
				
			}
			
		}
		
		else if(name2.equals("file")) {
			processReadyToExecute.semWait(this.FileAccess);
			if(this.FileAccess.BlockedQueue.contains(processReadyToExecute.getProcess_ID())) {
				this.Blocked.add(processReadyToExecute.getProcess_ID());
				this.Ready.remove();
				flagg = true;
			}
			
		}
		else if(name2.equals("userOutput")){
		
			processReadyToExecute.semWait(this.OutputScreen);
			if(this.OutputScreen.BlockedQueue.contains(processReadyToExecute.getProcess_ID())) {
				this.Blocked.add(processReadyToExecute.getProcess_ID());
				this.Ready.remove();
				flagg = true;
				
			}
			
		} 
		/*if(flagg = true) {
			int temp3 = processReadyToExecute.getProcess_ID();
    		for (int i =0 ; i<this.Ready.size() ; i++) {
    		    if(this.Ready.peek() == temp3) {
    		    	this.Ready.remove();
    		    	i++;
    		    }
    		    else
    		    	this.Ready.add(this.Ready.remove());
    		}
		}*/
		
		break;
		
    	case "readFile":
    		String filename = list.get(1);
    	if(list.get(1).equals(processReadyToExecute.getVariablesKeys().get("1"))) {
    		int pos = getindexofmemory(processReadyToExecute.getVariablesKeys().get("1"),(processReadyToExecute.getMemory_Boundaries()[1]),processReadyToExecute);
			filename = (String)this.memory[pos];
		}else if(list.get(1).equals(processReadyToExecute.getVariablesKeys().get("2"))) {
			int pos = getindexofmemory(processReadyToExecute.getVariablesKeys().get("2"),(processReadyToExecute.getMemory_Boundaries()[1]),processReadyToExecute);
			filename = (String)this.memory[pos];
		}
		else {
			int pos = getindexofmemory(processReadyToExecute.getVariablesKeys().get("3"),(processReadyToExecute.getMemory_Boundaries()[1]),processReadyToExecute);
			filename = (String)this.memory[pos];
		}
    	ArrayList<String> value =processReadyToExecute.readFile(filename);
        if(processReadyToExecute.getProcess_ID()==1) {
        	this.tempforAssign1 = value;
        }
        else if(processReadyToExecute.getProcess_ID()==2) {
        	this.tempforAssign2 = value;
        }
        else {
        	this.tempforAssign3 = value;
        }
    	break;
    	
    	case "writeFile": String filename2="";
    				String variable3 = "";
    		
    		if(list.get(1).equals(processReadyToExecute.getVariablesKeys().get("1"))) {
    			int pos = getindexofmemory(processReadyToExecute.getVariablesKeys().get("1"),(processReadyToExecute.getMemory_Boundaries()[1]),processReadyToExecute);
			filename2 = (String)this.memory[pos];
		}else if(list.get(1).equals(processReadyToExecute.getVariablesKeys().get("2"))) {
			int pos = getindexofmemory(processReadyToExecute.getVariablesKeys().get("2"),(processReadyToExecute.getMemory_Boundaries()[1]),processReadyToExecute);
			filename2 = (String)this.memory[pos];
		}
		else {
			int pos = getindexofmemory(processReadyToExecute.getVariablesKeys().get("3"),(processReadyToExecute.getMemory_Boundaries()[1]),processReadyToExecute);
			filename2 = (String)this.memory[pos];
		}
		if(list.get(2).equals(processReadyToExecute.getVariablesKeys().get("1"))) {
			int pos = getindexofmemory(processReadyToExecute.getVariablesKeys().get("1"),(processReadyToExecute.getMemory_Boundaries()[1]),processReadyToExecute);
			variable3 = (String)this.memory[pos];
		}else if(list.get(2).equals(processReadyToExecute.getVariablesKeys().get("2"))) {
			int pos = getindexofmemory(processReadyToExecute.getVariablesKeys().get("2"),(processReadyToExecute.getMemory_Boundaries()[1]),processReadyToExecute);
			variable3 = (String)this.memory[pos];
		}
		else {
			int pos = getindexofmemory(processReadyToExecute.getVariablesKeys().get("3"),(processReadyToExecute.getMemory_Boundaries()[1]),processReadyToExecute);
			variable3 = (String)this.memory[pos];
		}
		FileWriter fileWriter = new FileWriter(filename2, true);
    	BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
    	bufferedWriter.newLine();
    	bufferedWriter.write(variable3);
    	bufferedWriter.newLine();
    	// Close the resources
    	bufferedWriter.close();
    	fileWriter.close();
    	System.out.println("Data written to the CSV file successfully.");break;
    	
    	default:System.out.print("no system calls");return flag; 
    	}
    	PrintPCB(processReadyToExecute);
    	System.out.println("------------------Start Execution---------------------");
    	System.out.println("process in memory now :  " +this.MemoryProcess[0] + " and " + this.MemoryProcess[1]);
    	System.out.println("variables in memory : " + " 23:- " + this.memory[23] + " 24:- " + this.memory[24] +  " 25:- " +this.memory[25] + " 37:- "+this.memory[37] + " 38:- "+this.memory[38] + " 39:- " + this.memory[39]);
    	System.out.println(list.get(0) + "  " + list.get(1));
    	System.out.println("RR: " + RR);
    	System.out.println("--------------------Ready Queue-------------------");
    	PrintQueue(this.Ready);
    	System.out.println("--------------------Blocked Queue-------------------");
    	PrintQueue(this.Blocked);
    	System.out.println("---------------------------------------");
    	return flag;
    	
    	
    	

	}
    public void PrintPCB(Process x) {
    	System.out.println("--------------------Process information-------------------");
    	System.out.println("process ID :- " + x.getProcess_ID());
    	System.out.println("Program counter :- " + x.getProgram_Counter());
    	System.out.println("process state :- " + x.getProcess_State().toString());
    	System.out.println("process memory boundries  :- " + " lower end :- " + x.getMemory_Boundaries()[0] + " higher end:- " + x.getMemory_Boundaries()[1]);
    	System.out.println("process Variables :- " + x.getTempvars()[0] + " / " + x.getTempvars()[1] + "  / " + x.getTempvars()[2]);
    	System.out.println("Instruction Ready to Execute  :- " + x.getInstructions().get(x.getProgram_Counter()));
    	System.out.println("--------------------------END------------------------------");
    }
    
    public int getindexofmemory(String x , int highbound,Process processReadyToExecute) {
    	if(highbound < 25) {
    		if( x.equals(processReadyToExecute.getVariablesKeys().get("1"))) {
    			return 23;
    		}
    		else if(x.equals(processReadyToExecute.getVariablesKeys().get("2"))) {
    			return 24;
    		}
    		else {
    			return 25;
    		}
    	}
    	else {
    		if( x.equals(processReadyToExecute.getVariablesKeys().get("1"))) {
    			return 37;
    		}
    		else if(x.equals(processReadyToExecute.getVariablesKeys().get("2"))) {
    			return 38;
    		}
    		else {
    			return 39;
    		}
    	}
    }




	public int FindPCB(int id) {
    	int x = 0;
    	if((int)this.memory[0] == id ) {
    		return 2;
    	}
    	else if((int) this.memory[4]==id) {
    		return 6;
    	}
    	else {
    		return 10;
    	}
    }
	public File WriteIntoCsv(Process x,String filename, boolean flag) {
		
		
		
		File file = new File("Process" + x.getProcess_ID());
		
    
		try (PrintWriter writer = new PrintWriter(filename)) {
        // Write data to the CSV file
			for(int i=0;i<x.getInstructions().size();i++) {
				writer.println(x.getInstructions().get(i));
			}
			
			writer.println("PCB");
			writer.println("ID " + x.getProcess_ID());
			writer.println("State " + x.getProcess_State());
			writer.println("ProgramCounter " + x.getProgram_Counter());
			if(flag == true)
				writer.println("MemoryBoundries " + x.getMemory_Boundaries()[0] + " " + x.getMemory_Boundaries()[1]);
			else {
				writer.println("MemoryBoundries " + "0" + " " + "0");
			}
			writer.println("VariablesKey " + x.getVariablesKeys().get("1") + " " + x.getVariablesKeys().get("2") + " " + x.getVariablesKeys().get("3"));
			writer.println("VariablesValue " + x.getTempvars()[0] + " " + x.getTempvars()[1] + " " + x.getTempvars()[2]);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
		
		
    
		System.out.println("instruction has been written to the CSV file.");
		return file;
	}
	
	public void updateTextFile(Process x) {

		    File file = new File("Process" + x.getProcess_ID());
		    File tempFile = new File("temp");
		    boolean lineWritten = false;
		    boolean lineWritten2 = false;
		    boolean lineWritten3 = false;
		    boolean lineWritten4 = false;

		    try (BufferedReader reader = new BufferedReader(new FileReader(file));
		         BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
		        
		        String line;
		        while ((line = reader.readLine()) != null) {
		            if (line.startsWith("ProgramCounter") && !lineWritten) {		            	
		            
		            	line = "ProgramCounter" +" "+  x.getProgram_Counter();
		                writer.write(line);
		                writer.newLine();
		                lineWritten = true;
		            } 
		            else if(line.startsWith("State") && !lineWritten2) {
		            	 line = "State" +" "+  x.getProcess_State();
		            	 writer.write(line);
			             writer.newLine();
			             lineWritten2 = true;
		            }
		            else if(line.startsWith("VariablesValue") && !lineWritten3) {
		            	line = "VariablesValue" + " " + x.getTempvars()[0] + " " + x.getTempvars()[1] + " " + x.getTempvars()[2];
		            	writer.write(line);
			            writer.newLine();
			            lineWritten3 = true;
		            }
		            else if(line.startsWith("MemoryBoundries") && !lineWritten4) {
		            	line = "MemoryBoundries" + " " + x.getMemory_Boundaries()[0] + " " + x.getMemory_Boundaries()[1];
		            	writer.write(line);
			            writer.newLine();
			            lineWritten4 = true;
		            }
		            
		            
		            
		            
		            else {
		            	
		                writer.write(line);
		                writer.newLine();
		            }
		        }
		    } catch (IOException e) {
		        e.printStackTrace();
		    }

		    if (file.delete()) {
		        if (!tempFile.renameTo(file)) {
		            System.out.println("Failed to rename temp file to original file.");
		        }
		    } else {
		        System.out.println("Failed to delete original file.");
		    }
	}

	
	public ArrayList<String> ReadFromCsv(String filename ) {
	        
	        ArrayList<String> lines = new ArrayList<>();
	        boolean flag = false;
	        
	        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
	            String line;
	            while ((line = br.readLine()) != null) {
	            	if(line.equals("PCB"))
	            		flag = true;
	            	if(flag == false)
	            		lines.add(line);
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        
	        return lines;
	 }
	
	public List<String> ReadPCBFromCsv(String filename){
		List<String> lines = new ArrayList<>();
        Boolean flag =false; 
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
            	if(line.equals("PCB"))
            		flag = true;
            	else if(flag == true) {
            		lines.add(line);
            		}
            	
           
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return lines;
	}
	

    public Process AllocatePCB(int arrivalTime1) throws OSException{
        Vector<ArrayList<String>> commandLines = InterpreterOS.getCommandLine();
        Process process = null;
        for (ArrayList<String> commandLine : commandLines) {
            process = new Process();
            
            process.setProcess_ID(generateUniqueProcessID()); // Assign a unique process ID
            process.setInstructions(commandLine);
            
            this.ProcessArrival.put(arrivalTime1, process.getFileName());
            memory[PCBCount] = process.getProcess_ID();
            memory[PCBCount+1] = process.getProcess_State();
            memory[PCBCount+2] = process.getProgram_Counter();
            File file = new File("Process"+process.getProcess_ID());
            
            this.Process_Control_Path.add(file.getAbsolutePath());
            process.HandleVariables();
            AllocateInstruction(this.Process_Control_Path.get(this.Process_Control_Path.size()-1),process);
            
            memory[PCBCount+3] = process.getMemory_Boundaries();
            
            PCBCount= PCBCount+4;
                        
                    }
        InterpreterOS.getCommandLine().remove(0);
        return process;
    }

    private void AllocateInstruction(String filename,Process process) throws OSException{
    				boolean flagformemoryboundries = false;
    				List<String> instructionList = process.getInstructions();
                    if(memory[12] == null){
                    	int j=0;
                        for(j =0 ; j< instructionList.size() ;j++){
                        memory[j+12] = instructionList.get(j);
                        
                        }
                        flagformemoryboundries = true;
                        int [] tt = {12,j+12};
                        process.setMemory_Boundaries(tt);
                        memory[PCBCount+3] = process.getMemory_Boundaries();
                        memory[23] = ""; // for variable A
                        memory[24] = ""; // for variable B
                        memory[25] = ""; // for variable c
                    }
                    else if(memory[26] == null){
                    	int j=0;
                        for(j =0 ; j< instructionList.size();j++){
                            memory[j+26] = instructionList.get(j);
                        }
                        flagformemoryboundries = true;
                        int [] tt = {26,j+26};
                        process.setMemory_Boundaries(tt);
                        memory[PCBCount+3] = process.getMemory_Boundaries();
                        memory[37] = ""; // for variable A
                        memory[38] = ""; // for variable B
                        memory[39] = ""; // for variable c
                    }
                    if(this.MemoryProcess[0]==0) {
                    	this.MemoryProcess[0] = process.getProcess_ID();
                    }
                    else if(this.MemoryProcess[1]==0) {
                    	this.MemoryProcess[1] = process.getProcess_ID();
                    }
                    WriteIntoCsv(process , this.Process_Control_Path.get(this.Process_Control_Path.size()-1),flagformemoryboundries);
                   
                   
                    
                    
    }
        
    


    public void swapping(int id1 , int id2,ArrayList<Process> ListOfProcess){
    	
    	System.out.println("------------------ Before Swapping ---------------------");
    	System.out.println("variables in memory : " + " 23:- " + this.memory[23] + " 24:- " + this.memory[24] +  " 25:- " +this.memory[25] + " 37:- "+this.memory[37] + " 38:- "+this.memory[38] + " 39:- " + this.memory[39]);
    	System.out.println("------------------ After Swapping -----------------------");
    	if(this.MemoryProcess[0] == id2 ) {
    		this.MemoryProcess[0] = id1;
    	}
    	else {
    		this.MemoryProcess[1] = id1;
    	}
    	int[] array = null;
    	
    	List<String> instruction_process1 = ReadFromCsv("Process"+id1);
    	int PositionMemBoundries = getBoundriesMem(id2);
    	array = (int[]) this.memory[PositionMemBoundries];
    	
    	int j=0;
    	int i=0;
    	for(i=array[0];j< instruction_process1.size();i++) {
    		this.memory[i] = instruction_process1.get(j);
    		j++;
    	}
    	for(int z=0;z<ListOfProcess.size();z++) {
    		if(ListOfProcess.get(z).getProcess_ID() == id1) {
    			int [] arr = {array[0],i};
    			ListOfProcess.get(z).setMemory_Boundaries(arr);
    			if(this.memory[0].equals(id1)) {
    				this.memory[3] = arr;
    			}
    			else if(this.memory[4].equals(id1)) {
    				this.memory[7] = arr;
    			}
    			else {
    				this.memory[11] = arr;
    			}
    		}
    		
    	}
    	Object temp23 = this.memory[23];
		Object temp24 = this.memory[24];
		Object temp25 = this.memory[25];
		Object temp37 = this.memory[37];
		Object temp38 = this.memory[38];
		Object temp39 = this.memory[39];
    	for(int m =0 ; m<ListOfProcess.size();m++) {
    		
    		
    		if(id2 == ListOfProcess.get(m).getProcess_ID() ) {
    			int [] azz = (int[])this.memory[PositionMemBoundries];
    			if(azz[1]< 25) {  // 3
    				//this.ListOfProcess.get(m).getTempvars().add(this.memory[23]);
    				ListOfProcess.get(m).getTempvars()[0]=temp23;
    				ListOfProcess.get(m).getTempvars()[1]=temp24;
    				ListOfProcess.get(m).getTempvars()[2]=temp25;
    				
    			}
    			else {
    				ListOfProcess.get(m).getTempvars()[0]=temp37;
    				ListOfProcess.get(m).getTempvars()[1]=temp38;
    				ListOfProcess.get(m).getTempvars()[2]=temp39;
    			}
    			
    		}
    		if(id1 == ListOfProcess.get(m).getProcess_ID()) { // 1
    			if(i< 25) {
    				this.memory[23] = ListOfProcess.get(m).getTempvars()[0];
    				System.out.print("in swapping: " + "23  " + this.memory[23] + "   " + ListOfProcess.get(m).getTempvars()[0]);
    				this.memory[24] = ListOfProcess.get(m).getTempvars()[1];
    				System.out.print("in swapping: " + this.memory[24] + "   " + ListOfProcess.get(m).getTempvars()[1]);
    				this.memory[25] = ListOfProcess.get(m).getTempvars()[2];
    				
    			}
    			else {
    				this.memory[37]= ListOfProcess.get(m).getTempvars()[0];
    				System.out.print("in swapping: " + this.memory[37] + "   " + ListOfProcess.get(m).getTempvars()[0]);
    				this.memory[38] =ListOfProcess.get(m).getTempvars()[1];
    				System.out.print("in swapping: " + this.memory[38] + "   " + ListOfProcess.get(m).getTempvars()[1]);
    				this.memory[39] = ListOfProcess.get(m).getTempvars()[2];
    			}
    		}
    		
    	}
    	System.out.println("variables in memory : " + " 23:- " + this.memory[23] + " 24:- " + this.memory[24] +  " 25:- " +this.memory[25] + " 37:- "+this.memory[37] + " 38:- "+this.memory[38] + " 39:- " + this.memory[39]);
    	
    }
    public int getBoundriesMem(int id2) {
    	if((int)this.memory[0] == id2 ) {
    		return 3;
    	}
    	else if((int) this.memory[4]==id2) {
    		return 7;
    	}
    	else {
    		return 11;
    	}
    }
    private int generateUniqueProcessID() {
        int processID = processIDCounter;
        processIDCounter++;
        return processID;
    }
    public ProcessState getStateFromString(String input) throws IllegalArgumentException {
	    for (ProcessState state : ProcessState.values()) {
	        if (state.name().equalsIgnoreCase(input)) {
	            return state;
	        }
	    }
	    throw new IllegalArgumentException("Invalid process state: " + input);
	}

    public static void main(String[] args) throws OSException, IOException {
        OSKernel x = new OSKernel();
        for(int i =0 ; i<x.memory.length ; i++){
            System.out.print(x.memory[i]);
        }
    	
    }




	
	

}
