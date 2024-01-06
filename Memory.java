public class Memory {

    private Object[] Memory_Container; // outer container 
    private Object[] Memory_Container_PCB; // for PCB inner
    private Object[] Memory_Container_Instruction; // for instruction inner

    

    public Memory(){
        this.Memory_Container = new Object[2]; // two arrays one for PCB and Other for instructions 
        this.Memory_Container_PCB = new Object[2];
        this.Memory_Container_Instruction = new Object[2];
        this.Memory_Container[0] = this.Memory_Container_PCB;
        this.Memory_Container[1] = this.Memory_Container_Instruction;
    }

    public Object[] getMemory_Container() {
        return Memory_Container;
    }

    public void setMemory_Container(Object[] memory_Container) {
        Memory_Container = memory_Container;
    }

    public Object[] getMemory_Container_PCB() {
        return Memory_Container_PCB;
    }

    public void setMemory_Container_PCB(Object[] memory_Container_PCB) {
        Memory_Container_PCB = memory_Container_PCB;
    }

    public Object[] getMemory_Container_Instruction() {
        return Memory_Container_Instruction;
    }

    public void setMemory_Container_Instruction(Object[] memory_Container_Instruction) {
        Memory_Container_Instruction = memory_Container_Instruction;
    }

    
}
