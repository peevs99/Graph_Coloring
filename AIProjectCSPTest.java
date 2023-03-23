import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

import AIProject2.AIProjectCSP;

class AIProjectCSPTest {

    @Test
    void testGraphInput() {
        HashMap<Integer, ArrayList<Integer>> adjacencyList = new HashMap<>();
        ArrayList<Integer> neighbors1 = new ArrayList<>(Arrays.asList(2));
        ArrayList<Integer> neighbors2 = new ArrayList<>(Arrays.asList(1, 3));
        ArrayList<Integer> neighbors3 = new ArrayList<>(Arrays.asList(2, 4));
        ArrayList<Integer> neighbors4 = new ArrayList<>(Arrays.asList(3));
        adjacencyList.put(1, neighbors1);
        adjacencyList.put(2, neighbors2);
        adjacencyList.put(3, neighbors3);
        adjacencyList.put(4, neighbors4);

        AIProjectCSP.GraphInput graphInput = new AIProjectCSP.GraphInput(adjacencyList, 3);

        assertEquals(3, graphInput.getColorCount());
        assertEquals(adjacencyList, graphInput.getAdjacencyList());
    }

    @Test
    void testReadFile() {
        String testGraph = "c 3\n1,2\n2,3\n3,4\n";
        AIProjectCSP.GraphInput graphInput = AIProjectCSP.ReadFile.read(testGraph);

        HashMap<Integer, ArrayList<Integer>> adjacencyList = new HashMap<>();
        ArrayList<Integer> neighbors1 = new ArrayList<>(Arrays.asList(2));
        ArrayList<Integer> neighbors2 = new ArrayList<>(Arrays.asList(1, 3));
        ArrayList<Integer> neighbors3 = new ArrayList<>(Arrays.asList(2, 4));
        ArrayList<Integer> neighbors4 = new ArrayList<>(Arrays.asList(3));
        adjacencyList.put(1, neighbors1);
        adjacencyList.put(2, neighbors2);
        adjacencyList.put(3, neighbors3);
        adjacencyList.put(4, neighbors4);

        assertEquals(3, graphInput.getColorCount());
        assertEquals(adjacencyList, graphInput.getAdjacencyList());
    }

    @Test
    void testStoreSet() {
        AIProjectCSP.StoreSet storeSet = new AIProjectCSP.StoreSet(1, 2);

        assertEquals(1, storeSet.getFirst());
        assertEquals(2, storeSet.getSecond());
    }

    @Test
    void testBTSearch() {
        HashMap<Integer, ArrayList<Integer>> adjacencyList = new HashMap<>();
        ArrayList<Integer> neighbors1 = new ArrayList<>(Arrays.asList(2));
        ArrayList<Integer> neighbors2 = new ArrayList<>(Arrays.asList(1, 3));
        ArrayList<Integer> neighbors3 = new ArrayList<>(Arrays.asList(2, 4));
        ArrayList<Integer> neighbors4 = new ArrayList<>(Arrays.asList(3));
        adjacencyList.put(1, neighbors1);
        adjacencyList.put(2, neighbors2);
        adjacencyList.put(3, neighbors3);
        adjacencyList.put(4, neighbors4);

        AIProjectCSP.GraphInput graphInput = new AIProjectCSP.GraphInput(adjacencyList, 3);
        AIProjectCSP.BTSearch btSearch = new AIProjectCSP.BTSearch(graphInput);

        HashMap<Integer, Integer> result = btSearch.executeSearch();

        // Check if the output is correct
        assertTrue(result.get(1) != result.get(2));
        assertTrue(result.get(2) != result.get(3));
        assertTrue(result.get(3) != result.get(4));
    }
    @Test
    void testConsistencyCheck() {
        HashMap<Integer, ArrayList<Integer>> removedValues = new HashMap<>();
        ArrayList<Integer> removedValues1 = new ArrayList<>(Arrays.asList(2, 3));
        removedValues.put(1, removedValues1);

        AIProjectCSP.ConsistencyCheck consistencyCheck = new AIProjectCSP.ConsistencyCheck(removedValues, true);

        assertEquals(removedValues, consistencyCheck.getRemovedValues());
        assertTrue(consistencyCheck.checkConsistency());
    }

    @Test
    void testExecuteSearchWithNoSolution() {
        HashMap<Integer, ArrayList<Integer>> adjacencyList = new HashMap<>();
        ArrayList<Integer> neighbors1 = new ArrayList<>(Arrays.asList(2));
        ArrayList<Integer> neighbors2 = new ArrayList<>(Arrays.asList(1, 3));
        ArrayList<Integer> neighbors3 = new ArrayList<>(Arrays.asList(2));
        adjacencyList.put(1, neighbors1);
        adjacencyList.put(2, neighbors2);
        adjacencyList.put(3, neighbors3);

        AIProjectCSP.GraphInput graphInput = new AIProjectCSP.GraphInput(adjacencyList, 1);
        AIProjectCSP.BTSearch btSearch = new AIProjectCSP.BTSearch(graphInput);

        HashMap<Integer, Integer> result = btSearch.executeSearch();

        // Check if no solution is found
        assertTrue(result.isEmpty());
    }

}

