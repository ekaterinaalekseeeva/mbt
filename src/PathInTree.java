import java.util.ArrayList;

/**
 * Created by Ekaterina.Alekseeva on 30-May-15.
 */
public class PathInTree {
    public EFGNode root;
    public ArrayList<ArrayList<EFGNode>> paths;

        public PathInTree(){
            root = null;
            paths = new ArrayList<ArrayList<EFGNode>>();
        }
}
