import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class AIProjectCSP {

    public static void main(String[] args) {
        // Input file check
        if (args.length != 1) {
            System.out.println("Problem with input file!");
            return;
        }
        // read input
        GraphInput graphInput = ReadFile.read(args[0]);
        // start search
        BTSearch BTSearch = new BTSearch(graphInput);
        HashMap<Integer, Integer> result = BTSearch.executeSearch();
        // results
        if (result.isEmpty()) {
            System.out.println("No solution found!\n");
        } else {
            System.out.println("Vertex,Color");
            for (Integer node : result.keySet()) {
                System.out.println((node) + " -> " + result.get(node));
            }
        }
    }

    public static class ConsistencyCheck {
        private final HashMap<Integer, ArrayList<Integer>> removedValues;
        private final boolean isConsistent;

        public ConsistencyCheck(HashMap<Integer, ArrayList<Integer>> removedValues, boolean isConsistent) {
            this.removedValues = removedValues;
            this.isConsistent = isConsistent;
        }

        public HashMap<Integer, ArrayList<Integer>> getRemovedValues() {
            return removedValues;
        }

        public boolean checkConsistency() {
            return isConsistent;
        }
    }

    public static class BTSearch {
        private final HashMap<Integer, ArrayList<Integer>> adjacencyList;
        private final HashMap<Integer, HashSet<Integer>> domainList;
        private final int colorCount;

        public BTSearch(GraphInput graphInput) {
            adjacencyList = graphInput.getAdjacencyList();
            domainList = new HashMap<>();
            colorCount = graphInput.getColorCount();
            initializeDomain();
        }

        private void initializeDomain() {
            for (Integer node : adjacencyList.keySet()) {
                HashSet<Integer> tempSet = new HashSet<>();
                for (int i = 1; i <= colorCount; i++) {
                    tempSet.add(i);
                }
                domainList.put(node, tempSet);
            }
        }

        public HashMap<Integer, Integer> executeSearch() {
            return search(new HashMap<>());
        }

        private HashMap<Integer, Integer> search(HashMap<Integer, Integer> assignment) {
            if (assignment.keySet().size() == adjacencyList.keySet().size()) {
                return assignment;
            }

            int variable = selectVariable(assignment);

            ArrayList<Integer> orderedDomain = orderDomain(variable);

            for (int color : orderedDomain) {
                HashMap<Integer, Integer> copiedAssignment = new HashMap<>(assignment);
                copiedAssignment.put(variable, color);

                ConsistencyCheck consistencyCheck = checkConsistency(variable, color);

                if (consistencyCheck.checkConsistency()) {
                    for (Integer node : consistencyCheck.getRemovedValues().keySet()) {
                        for (Integer value : consistencyCheck.getRemovedValues().get(node)) {
                            domainList.get(node).remove(value);
                        }
                    }
                } else continue;

                HashMap<Integer, Integer> result = search(copiedAssignment);
                if (!result.isEmpty()) {
                    return result;
                }
                for (Integer node : consistencyCheck.getRemovedValues().keySet()) {
                    for (Integer value : consistencyCheck.getRemovedValues().get(node)) {
                        domainList.get(node).add(value);
                    }
                }
            }
            return new HashMap<>();
        }

        private int selectVariable(HashMap<Integer, Integer> assignment) {
            ArrayList<Integer> minimums = new ArrayList<>();
            int minSize = Integer.MAX_VALUE;
            for (Integer node : domainList.keySet()) {
                if (!assignment.containsKey(node)) {
                    int domainSize = domainList.get(node).size();
                    if (domainSize < minSize) {
                        minSize = domainSize;
                        minimums.clear();
                        minimums.add(node);
                    } else if (domainSize == minSize) {
                        minimums.add(node);
                    }
                }
            }
            int maxDegreeNode = minimums.get(0);
            if (minimums.size() > 1) {
                int maxDegree = Integer.MIN_VALUE;
                for (Integer node : minimums) {
                    int degree = adjacencyList.get(node).size();
                    if (degree > maxDegree) {
                        maxDegree = degree;
                        maxDegreeNode = node;
                    }
                }
                return maxDegreeNode;
            }
            return minimums.get(0);
        }
    
        private ArrayList<Integer> orderDomain(int node) {
            ArrayList<Integer> orderedDomain = new ArrayList<>();
            ArrayList<StoreSet> orderedStoreSets = new ArrayList<>();
            for (Integer val : domainList.get(node)) {
                int count = 0;
                for (Integer neighbour : adjacencyList.get(node)) {
                    if (domainList.get(neighbour).contains(val)) count++;
                }
                orderedStoreSets.add(new StoreSet(val, count));
            }
            orderedStoreSets.sort(Comparator.comparing(StoreSet::getSecond));
            for (StoreSet StoreSet : orderedStoreSets) {
                orderedDomain.add(StoreSet.getFirst());
            }
            return orderedDomain;
        }
    
        private ConsistencyCheck checkConsistency(int node, int color) {
            Queue<StoreSet> queue = new LinkedList<>();
            HashMap<Integer, ArrayList<Integer>> removedValues = new HashMap<>();
    
            ArrayList<Integer> removedFromDomain = new ArrayList<>();
            for (Integer val : domainList.get(node)) {
                if (color != val) {
                    removedValues.computeIfAbsent(node, ArrayList::new);
                    removedValues.get(node).add(val);
                    removedFromDomain.add(val);
                }
            }
            for (Integer val : removedFromDomain) {
                domainList.get(node).remove(val);
            }
    
            for (Integer neighbour : adjacencyList.get(node)) {
                queue.add(new StoreSet(neighbour, node));
            }
            while (!queue.isEmpty()) {
                StoreSet arc = queue.remove();
                if (update(arc.getFirst(), arc.getSecond(), removedValues)) {
                    if (domainList.get(arc.getFirst()).isEmpty()) {
                        return new ConsistencyCheck(removedValues, false);
                    }
                    for (Integer neighbour : adjacencyList.get(arc.getFirst())) {
                        queue.add(new StoreSet(neighbour, arc.getFirst()));
                    }
                }
            }
            return new ConsistencyCheck(removedValues, true);
        }
    
        private boolean update(int from, int to, HashMap<Integer, ArrayList<Integer>> removedValues) {
            boolean updated = false;
            boolean consistent = false;
            for (Iterator<Integer> i = domainList.get(from).iterator(); i.hasNext(); ) {
                int xValue = i.next();
                for (int yValue : domainList.get(to)) {
                    if (xValue != yValue) {
                        consistent = true;
                        break;
                    }
                }
                if (!consistent) {
                    i.remove();
                    removedValues.computeIfAbsent(from, ArrayList::new);
                    removedValues.get(from).add(xValue);
                    updated = true;
                }
                consistent = false;
            }
            return updated;
        }
    }
    
    public static class GraphInput {
        private final HashMap<Integer, ArrayList<Integer>> adjacencyList;
        private final int colorCount;
    
        public GraphInput(HashMap<Integer, ArrayList<Integer>> adjacencyList, int colorCount) {
            this.adjacencyList = adjacencyList;
            this.colorCount = colorCount;
        }
    
        public HashMap<Integer, ArrayList<Integer>> getAdjacencyList() {
            return adjacencyList;
        }
    
        public int getColorCount() {
            return colorCount;
        }
    }
    
    public static class ReadFile {
        public static GraphInput read(String filePath) {
            System.out.print("file_Path-==->" + filePath);
            System.out.print('\n');
            HashMap<Integer, ArrayList<Integer>> adjacencyList = new HashMap<>();
            int colorCount = 0;
    
            try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = br.readLine()) != null) {
    
                    if (line.isEmpty() || line.charAt(0) == '#') continue;
    
                    if (line.charAt(0) == 'c' || line.charAt(0) == 'C') {
                        String[] tokens = line.split(" ");
                        colorCount = Integer.parseInt(tokens[2]);
                    }
                    
                     else {
    
                        String[] args = line.split(",");
                        int from = Integer.parseInt(args[0]);
                        int to = Integer.parseInt(args[1]);                        
    
                        // initialize lists if absent
                        adjacencyList.computeIfAbsent(from, ArrayList::new);
                        adjacencyList.computeIfAbsent(to, ArrayList::new);
    
                        // It is an undirected graph so add it to both of them
                        adjacencyList.get(from).add(to);
                        adjacencyList.get(to).add(from);
                    }
                }
            } catch (Exception e) {
                System.out.println("Your input file format is not correct!");
                e.printStackTrace();
                System.exit(1);
            }
    
            return new GraphInput(adjacencyList, colorCount);
        }
    }
    
    public static class StoreSet {
        private final int first;
        private final int second;
    
        public StoreSet(int first, int second) {
            this.first = first;
            this.second = second;
        }
    
        public int getFirst() {
            return first;
        }
    
        public int getSecond() {
            return second;
        }
    }
}    
    
