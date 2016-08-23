
package stack;

/**
 * Stack is class that represents a stack
 * @author Daniel Nissenbaum
 */
public class Stack {
    int stack[];
    int pointer = 0;
    
    /**
     * creates a stack of fixed length
     * @param size maximum stack size
     */
    public Stack(int size){
        stack = new int[size];
    }
    
    /**
     * checks if stack is full
     * @return true if stack is full
     */
    public boolean isFull(){
        return pointer == stack.length;
    }
    
    /**
     * checks if stack is empty
     * @return true if stack is empty
     */
    public boolean isEmpty(){
        return pointer == 0;
    }
    
    /**
     * Pushes an item onto the stack. Prints error if stack is full
     * @param item integer to put on stack
     */
    
    public void push(int item){
        if(pointer < stack.length) {
            stack[pointer++] = item;
        } else{
            System.out.println("Attempted Push() when Stack was is full");
        }
    }
    
    /**
     * Returns top item from the stack. Prints error if stack is empty
     * @return top item from stack
     */
    
    public int pop(){
        if(pointer > 0) {
            return stack[--pointer];
        }
        System.out.println("Attempted Pop() when Stack is empty");
        return 0;
    }
}
