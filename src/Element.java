import org.openqa.selenium.WebElement;

/**
 * Created by Ekaterina.Alekseeva on 11-Mar-15.
 */
public class Element {
    public String name;
    private WebElement element;
    private boolean terminal;
    private boolean cond_terminal;
    private boolean special_cond;
    private String action;
    private Element parent;
    private boolean marked;

//    public Element(WebElement el, boolean term, boolean cond_term, boolean spec_cond, String act){
//        element = el;
//        terminal = term;
//        cond_terminal = cond_term;
//        special_cond = spec_cond;
//        action = act;
//    marked = false;
//    }

    public Element(WebElement el){
        element = el;
        terminal = false;
        cond_terminal = false;
        special_cond = false;
        action = "";
        marked = false;
    }

    public Element(){
        element = null;
        terminal = false;
        cond_terminal = false;
        special_cond = false;
        action = "";
        marked = false;
    }

    public WebElement getElement (){
        return element;
    }

    public void setElement (WebElement value){
        element = value;
    }

    public boolean getTerminal(){
        return terminal;
    }

    public void setTerminal(boolean value){
        if (value != cond_terminal){
            terminal = value;
        }
    }

    public boolean getCondTerminal(){
        return cond_terminal;
    }

    public void setCondTerminal(boolean value){
        if (value != terminal){
            cond_terminal = value;
        }
    }

    public boolean getSpecialCond(){
        return special_cond;
    }

    public void setSpecialCond(boolean value){
        if (!terminal){
            special_cond = value;
        }
    }

    public String getAction(){
        return action;
    }

    public void setAction (String value) {
        if (value.equals("click") || value.equals("write")){
            action = value;
        } //else {
//            throw new Exception("Unknown action");
//        }
    }

    public void setParent (Element value){
        parent = value;
    }

    public Element getParent (){
        return parent;
    }

    public void makeMarked(){
        marked = true;
    }

    public boolean isMarked(){
        return marked;
    }

}
