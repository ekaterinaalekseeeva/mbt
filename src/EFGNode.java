/**
 * Created by Ekaterina.Alekseeva on 18-May-15.
 */
public class EFGNode {
    public String name;
    public String selector;
    public EFGNode parent;
    public boolean simple;
    public boolean root;

    public EFGNode(){
        name = "";
        selector = "";
        parent = null;
    }
}
