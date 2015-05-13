import java.util.ArrayList;

/**
 * Created by Ekaterina.Alekseeva on 06-Apr-15.
 */
public class SpecialConditionsElement {
    public String selector;
    public String action; // click or write
    public String type; // special write ("write") || after click search only for specified elements ("search elements") || search in specified area ("search area") || "alert accept" || "alert decline"
    public String area;
    public ArrayList<AllowedSelector> allowedSelectors = new ArrayList<AllowedSelector>();
    public ArrayList<String> allowedWrite = new ArrayList<String>();

    public class AllowedSelector {
        String selector;
        String action;
    }
}
