import java.util.AbstractCollection;
import java.util.ArrayList;

/**
 * Created by Ekaterina.Alekseeva on 19-Mar-15.
 */
public class ConditionallyTerminalElement {
    public String selector;
    public String action; // click or write
    public ArrayList<Condition> conditions = new ArrayList<Condition>();

    public class Condition{
        String neededElementSelector;
        String neededAction;
        boolean specialProcessing; //todo: if true, search for this element in specialCondElements list
    }
}
