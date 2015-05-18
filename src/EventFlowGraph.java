import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Ekaterina.Alekseeva on 18-May-15.
 */
public class EventFlowGraph {
    public static ArrayList<EFGNode> nodes = new ArrayList<EFGNode>();

    public static EFGNode findNode(String name){
        for (EFGNode i : nodes){
            if (i.name.equals(name)){
                return i;
            }
        }
        return null;
    }

    public static void printNodes(){
        for (EFGNode i : nodes){
            if (i.parent == null) {
                System.out.println(i.name + " " + i.selector + " " + null + " " + i.simple);
            } else {
                System.out.println(i.name + " " + i.selector + " " + i.parent.name + " " + i.simple);
            }
        }
    }

    public static void parseGUIGraph (String filename) throws IOException {
        BufferedReader input = new BufferedReader(new FileReader(filename));
        String currentLine;

        while((currentLine = input.readLine()) != null){
//            System.out.println(currentLine);
            EFGNode tmpNode = new EFGNode();
            int len = currentLine.length();
            if (!currentLine.contains("URL")) {
                if (currentLine.contains("label")) {
                    tmpNode.name = currentLine.substring(0, currentLine.indexOf(' '));
                    tmpNode.selector = currentLine.substring(currentLine.indexOf('"')+1, len - 3);
                    nodes.add(tmpNode);
//                    System.out.println(tmpNode.name + " " + tmpNode.selector);
                } else if (currentLine.contains(" -- ")){
                    String[] substrings = currentLine.split(" -- ");
                    substrings[substrings.length-1] = substrings[substrings.length-1].replaceAll(";", "");
//                    for (String i : substrings){
//                        System.out.println(i);
//                    }
                    for (int i = 0; i < substrings.length-1; i++){
                        findNode(substrings[i]).parent = findNode(substrings[i+1]);
                    }
                }
            }
            System.out.println();
        }
    }

    public static void markSimpleNodes(){
        for (EFGNode i : nodes){
            if (i.parent == null){
                i.simple = true;
                i.root = true;
            } else {
                i.simple = false;
                findNode(i.parent.name).simple = false;
            }
        }
    }

    public static void findPathsBetweenRoots(){

    }

    public static void findPathsInTree(EFGNode root){

    }

    public static void main(String[] args) throws IOException {
        int graphCounter = 87;
        String filename = "GUIgraph"+graphCounter;

        parseGUIGraph(filename);

        markSimpleNodes();

        findPathsBetweenRoots();

        printNodes();
    }
}
