
package stack;

/**
 *
 * @author Daniel Nissenbaum
 */
public class StackQueue {

    private void testStack(){
        System.out.println("Testing Stack...");
        Stack s = new Stack(4);
        s.push(1);
        s.push(2);
        s.push(3);
        s.push(4);
        s.push(5);
        System.out.println(s.pop() + "\n" + s.pop() + "\n" + s.pop() + "\n" + s.pop());
        System.out.println(s.pop() + "\n" );
        
    }
    public static void main(String[] args) {
        StackQueue sq = new StackQueue();
        sq.testStack();
    }
}
