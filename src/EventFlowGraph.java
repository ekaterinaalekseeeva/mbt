import java.io.*;
import java.util.ArrayList;

/**
 * Created by Ekaterina.Alekseeva on 18-May-15.
 */
public class EventFlowGraph {
    public static int pathLength;
    public static ArrayList<EFGNode> nodes = new ArrayList<EFGNode>();
    public static ArrayList<EFGNode> roots = new ArrayList<EFGNode>();
    public static ArrayList<PathInTree> pathsInTrees = new ArrayList<PathInTree>();
    public static BufferedWriter out;

    public static EFGNode findNode(String name){
        for (EFGNode i : nodes){
            if (i.name.equals(name)){
                return i;
            }
        }
        return null;
    }

    public static PathInTree findPathInTreeByRoot(EFGNode root){
        for (PathInTree i : pathsInTrees){
            if (i.root == root){
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

    public static void printPathsInTrees(){
        for (PathInTree i : pathsInTrees){
            System.out.println(i.root.name);
            for (ArrayList<EFGNode> j : i.paths){
                for (EFGNode k : j){
                    System.out.print(k.name + " ");
                }
                System.out.println();
            }
            System.out.println();
        }
    }

    public static void printPath(ArrayList<EFGNode> path) {
        try {
            for (EFGNode j : path) {
                out.write(j.name + " ");
            }
            out.write("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<EFGNode> insertFragmentInPath(ArrayList<EFGNode> pathBeforeInsert, ArrayList<EFGNode> fragment, ArrayList<EFGNode> pathAfterInsert){
        ArrayList<EFGNode> curPath = new ArrayList<EFGNode>();
        for (EFGNode i : pathBeforeInsert){
            curPath.add(i);
        }

        for (EFGNode i : fragment){
            curPath.add(i);
        }

        for (EFGNode i : pathAfterInsert){
            curPath.add(i);
        }

        return curPath;
    }

    public static void addPathsFromTree(ArrayList<EFGNode> curPath){
        for (int i = 0; i < curPath.size(); i++){
            if (!curPath.get(i).simple){
                ArrayList<ArrayList<EFGNode>> paths = findPathInTreeByRoot(curPath.get(i)).paths;
                for (ArrayList<EFGNode> j : paths){
                    if (pathLength >=2 && (curPath.size() + j.size()) == pathLength) {
                        //                                ArrayList<EFGNode> before = (ArrayList<EFGNode>)curPath.subList(0, i+1);
                        if (i < curPath.size() - 1) {
                            printPath(insertFragmentInPath(new ArrayList<EFGNode>(curPath.subList(0, i + 1)), j, new ArrayList<EFGNode>(curPath.subList(i + 1, curPath.size()))));
                        } else {
                            printPath(insertFragmentInPath(new ArrayList<EFGNode>(curPath.subList(0, i + 1)), j, new ArrayList<EFGNode>()));
                        }
                    }
                }
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
            if (!currentLine.contains("URL") && !currentLine.contains("terminal")) {
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
//            System.out.println();
        }
    }

    public static void markSimpleNodes(){
        for (EFGNode i : nodes){
            if (i.parent == null){
                i.simple = true;
                i.root = true;
                roots.add(i);
            } else {
                i.simple = false;
                findNode(i.parent.name).simple = false;
            }
        }
        System.out.println("Number of roots " + roots.size());
    }

    public static int swap(int a, int b) {  // usage: y = swap(x, x=y);
        return a;
    }

    public static int[] reverse (int[] array, int start, int end){
        int i;
        int m = end - start;
        for (i = 0; i < m/2; i++){
            array[end-i-1] = swap (array[i+start], array[i+start]=array[end-i-1]);
        }
        return array;
    }

    public static void generatePermutations(int length, int firstExcluded, int secondExcluded){
        int n = roots.size()-1;
        int[] combinations = new int [length];
        for (int i = 0; i < length; i++){
                combinations[i] = i;
        }

        System.out.println("length " + length + " path length " + pathLength);

        while(true){
//            for(int i=0;i<length;i++) //Печатаем очередную последовательность
//                System.out.print(combinations[i] + " ");
//            System.out.println();

            boolean hasExcluded = false;
            for(int i = 0; i < length; i++){
                if (combinations[i] == firstExcluded || combinations[i] == secondExcluded){
                    hasExcluded = true;
                    break;
                }
            }

            if(!hasExcluded){
//                System.out.println("not has excluded");
                int min;
                boolean ifCheck = false;
                int [] permutations = combinations.clone();
                while (true){
                    for (int i = length-2; i >= 0; i--){ //просматриваем массив с конца
                        ifCheck = false;
                        if (permutations[i] < permutations[i+1]) { // если возникает "беспорядок" (эл-ты расположены не по возрастанию начиная с какого-то эл-та X)
                            min = i+1;
                            for (int j = i+1; j < length; j++){
                                if (permutations[j] <= permutations[min] && permutations[j] > permutations[i]) {//ищем среди предыдущих эл-тов наименьший, больший X
                                    min = j;
                                }
                            }
                            permutations[min] = swap (permutations[i], permutations[i]=permutations[min]); //меняем их местами
                            if ((i+1)!=(length-1)) {
                                permutations = reverse (permutations, i+1, length);//разворачиваем все предыдущие эл-ты
                            }
                            ifCheck = true;
                            break;
                        }
                    }
                    if (!ifCheck) {
                        break;
                    }
                    ArrayList<EFGNode> curPath = new ArrayList<EFGNode>();
                    curPath.add(roots.get(firstExcluded));
                    for(int i = 0; i < length; i++) {
                        curPath.add(roots.get(permutations[i]));
//                        System.out.print(permutations[i] + " ");
                    }
                    curPath.add(roots.get(secondExcluded));

//                    paths.add(curPath);
                    printPath(curPath);
//                    System.out.println();

                    addPathsFromTree(curPath);
                }

                ArrayList<EFGNode> curPath = new ArrayList<EFGNode>();
                curPath.add(roots.get(firstExcluded));
                for(int i = 0; i < length; i++) {
                    curPath.add(roots.get(combinations[i]));
//                    System.out.print(combinations[i] + " ");
                }
                curPath.add(roots.get(secondExcluded));
//                paths.add(curPath);
                printPath(curPath);
//                System.out.println();
                addPathsFromTree(curPath);
            }

            int i;
            for(i=length-1;i>=0 && combinations[i]==n+i-length+1;i--); //Ищем первый справа элемент, не достигший максимального значения

            if(i==-1) break; //Если не нашли, то заканчиваем работу.

            combinations[i]++; //Если нашли, то увеличиваем его на 1

            for(int j=i+1;j<length;j++) //и заполняем правую часть

                combinations[j]=combinations[j-1]+1; //минимально возможными значениями.

        }
    }

    public static void findPathBetweenTwoNodes(int a, int b){
        int r = 0;
        int n = roots.size();
        ArrayList<EFGNode> curPath = new ArrayList<EFGNode>();
        curPath.add(roots.get(a));
        curPath.add(roots.get(b));
//        paths.add(curPath);
        if (pathLength <= 2) {
            printPath(curPath);
        }
        addPathsFromTree(curPath);

//        r++;
        if (pathLength < 2) {
            while (r + 2 < n) {
                r++;
                generatePermutations(r, a, b);
            }
        } else{
            generatePermutations(pathLength-2, a, b);
        }
    }

    public static void findPathsBetweenRoots(){
        for (int i = 0; i < roots.size(); i++){
            for (int j = 0; j < roots.size(); j++){
                if (i != j){
                    findPathBetweenTwoNodes(i, j);
                }
            }
        }
//        findPathBetweenTwoNodes(0, 4);
    }

    public static void findPathsInTree(EFGNode root, ArrayList<EFGNode> path, PathInTree pathInTree){
        for (EFGNode i: nodes){
            if (!i.root && i.parent == root){
                path.add(i);
                ArrayList<EFGNode> newpath = new ArrayList<EFGNode>();
                for (EFGNode j : path){
                    newpath.add(j);
                }
                pathInTree.paths.add(newpath);
                findPathsInTree(i, path, pathInTree);
            }
//
        }
        if (path.size() > 0) {
            path.remove(path.size() - 1);
        }
    }

    public static void main(String[] args) throws IOException {
        pathLength = 2;
        out = new BufferedWriter(new FileWriter("Paths.txt"));
        int graphCounter = 87;
//        String filename = "GUIgraph"+graphCounter;
        String filename = "GUIgraph";

        parseGUIGraph(filename);
        System.out.println("Number of nodes " + nodes.size());

        markSimpleNodes();

        for (EFGNode i : nodes){
            if (i.root && !i.simple){
                PathInTree newPath = new PathInTree();
                newPath.root = i;
                ArrayList<EFGNode> path = new ArrayList<EFGNode>();
                findPathsInTree(i, path, newPath);
                pathsInTrees.add(newPath);
            }
        }

        findPathsBetweenRoots();

//        printNodes();

//        printPathsInTrees();

        out.close();
    }
}
