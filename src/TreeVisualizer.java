package de.unijena.bioinf.TreeVisualization;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.IntStream;

import static java.lang.foreign.ValueLayout.*;

public class TreeVisualizer {
    private static class libNames {
        public static final String odgfFileName = "libOGDF.so";
        public static final String tmapFileName = "libtmap.so";
        public static final String functionFileName = "libLayoutFromEdgeList_internal.so";
    }

    private static class FunctionNames {
        public static final String ogdfBinomial = "_ZN4ogdf4Math8binomialEii";
        public static final String tmapLayoutFromEdgeList = "_ZN4tmap18LayoutFromEdgeListEjRKSt6vectorISt5tupleIJjjfEESaIS2_EENS_19LayoutConfigurationEbb";
        public static final String LayoutFromEdgeList = "_Z28LayoutFromEdgeList_internalsiPiS_Pfi";
    }
    public static void main(String[] args) {
        // Run for testing
        TreeVisualizer testlayout = new TreeVisualizer();
        EdgeList testedges = new EdgeList(new int[]{0, 2, 3, 1, 0}, new int[]{2, 3, 1, 0, 3}, new float[]{0.5F, 1.0F, 0.5F, 1.0F, 1.2F});
        Layout result = testlayout.LayoutFromEdgeList(testedges);
        System.out.println(result);
    }

    /**
     *
     * @param edges List of all edges contained in this Graph
     * @return Layout of all Edges and Nodes with x and y coordinates
     */
    public Layout LayoutFromEdgeList(EdgeList edges) {
        int[] sources = edges.getSources();
        int[] destinations = edges.getDestinations();
        float[] weights = edges.getWeights();

        // Test Edge List validity
        assert sources.length == destinations.length && destinations.length == weights.length;

        HashSet<Integer> nodes = new HashSet<>();
        Arrays.stream(sources).forEach(node -> nodes.add(node));
        Arrays.stream(destinations).forEach(node -> nodes.add(node));
        int number_of_nodes = nodes.size();

        float[] result_array = layout(number_of_nodes, sources, destinations, weights);
        // Format of result_array [x1, x2, x3, ..., xn, y1, y2, ..., yn]

        float[] xcoordinates = new float[result_array.length/2];
        float[] ycoordinates = new float[result_array.length/2];

        // copy results to respective arrays
        IntStream.range(0, xcoordinates.length).forEach(i -> xcoordinates[i] = result_array[i]);
        IntStream.range(0, ycoordinates.length).forEach(i -> ycoordinates[i] = result_array[i + xcoordinates.length]);


        return new Layout(xcoordinates, ycoordinates, sources, destinations);

    }

    private void setIntMemoryFromArray(MemorySegment memory, int[] inputArray) {
        for(int i=0; i<inputArray.length; i++) {
            memory.setAtIndex(JAVA_INT, i, inputArray[i]);
        }
    }

    private void setFloatMemoryFromArray(MemorySegment memory, float[] inputArray) {
        for(int i=0; i<inputArray.length; i++) {
            memory.setAtIndex(JAVA_FLOAT, i, inputArray[i]);
        }
    }

    private float[] layout(int number_of_nodes, int[] sources, int[] destinations, float[] weights){
        int result_length = number_of_nodes*2; //#Nodes * 2 (x-coord, y-coord)
        float[] results = new float[result_length];


        try (Arena offHeap = Arena.ofConfined()) {


            // Allocate off-heap memory to store pointers
            MemorySegment input1mem = offHeap.allocateArray(JAVA_INT, sources.length);
            MemorySegment input2mem = offHeap.allocateArray(JAVA_INT, destinations.length);
            MemorySegment input3mem = offHeap.allocateArray(JAVA_FLOAT, weights.length);

            // Fill memory with values
            setIntMemoryFromArray(input1mem, sources);
            setIntMemoryFromArray(input2mem, destinations);
            setFloatMemoryFromArray(input3mem, weights);

            //Define foreign function
            SymbolLookup lib = SymbolLookup.libraryLookup(libNames.functionFileName, offHeap);
            MethodHandle libLayout = Linker.nativeLinker().downcallHandle(
                    lib.find(FunctionNames.LayoutFromEdgeList).orElseThrow(),
                    // returns tuple of vectors and GraphProperties, input: int, edges (address), config(adress), bool, bool
                    //FunctionDescriptor.of(ADDRESS, JAVA_INT, ADDRESS, JAVA_BOOLEAN, JAVA_BOOLEAN));
                    FunctionDescriptor.of(ADDRESS, JAVA_INT, ADDRESS, ADDRESS, ADDRESS, JAVA_INT));

            MemorySegment result = (MemorySegment) libLayout.invoke(5, input1mem, input2mem, input3mem, 6);
            MemorySegment realResult = result.reinterpret(JAVA_FLOAT.byteSize()* (result_length+1));

            //unpack Memory Segment
            for(int i=0; i<result_length; i++) {
                results[i] = realResult.getAtIndex(JAVA_FLOAT, i);
            }

        } catch (Throwable e) {throw new RuntimeException(e);}
    return results;
    }


    static class EdgeList {
        private final int[] sources;
        private final int[] destinations;
        private final float[] weights;

        public EdgeList(int[] sources, int[] destinations, float[] weights) {
            this.sources = sources;
            this.destinations = destinations;
            this.weights = weights;
        }

        public int[] getSources() {
            return sources;
        }

        public int[] getDestinations() {
            return destinations;
        }

        public float[] getWeights() {
            return weights;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (!(o instanceof EdgeList edgeList))
                return false;
            return Arrays.equals(getSources(), edgeList.getSources())
                    && Arrays.equals(getDestinations(), edgeList.getDestinations())
                    && Arrays.equals(getWeights(), edgeList.getWeights());
        }

        @Override
        public int hashCode() {
            int result = Arrays.hashCode(getSources());
            result = 31 * result + Arrays.hashCode(getDestinations());
            result = 31 * result + Arrays.hashCode(getWeights());
            return result;
        }
    }

    static class Layout {
        private final float[] xCoordinates;
        private final float[] yCoordinates;
        private final int[] edgeSources;
        private final int[] edgeDestinations;

        @Override
        public String toString() {
            StringBuilder result = new StringBuilder();
            // (x, y) Coordinates
            result.append("Coordinates: ");
            IntStream.range(0, xCoordinates.length).forEach(i -> result.append("(").append(xCoordinates[i]).append(", ").append(yCoordinates[i]).append("), "));
            result.deleteCharAt(result.length()-1);
            result.deleteCharAt(result.length()-1);
            // (u, v) Edges
            result.append("\nEdges: ");
            IntStream.range(0, edgeSources.length).forEach(i -> result.append("(").append(edgeSources[i]).append(", ").append(edgeDestinations[i]).append("), "));
            result.deleteCharAt(result.length()-1);
            result.deleteCharAt(result.length()-1);
            return result.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (!(o instanceof Layout layout))
                return false;
            return Arrays.equals(getxCoordinates(), layout.getxCoordinates())
                    && Arrays.equals(getyCoordinates(), layout.getyCoordinates())
                    && Arrays.equals(getEdgeSources(), layout.getEdgeSources())
                    && Arrays.equals(getEdgeDestinations(), layout.getEdgeDestinations());
        }

        @Override
        public int hashCode() {
            int result = Arrays.hashCode(getxCoordinates());
            result = 31 * result + Arrays.hashCode(getyCoordinates());
            result = 31 * result + Arrays.hashCode(getEdgeSources());
            result = 31 * result + Arrays.hashCode(getEdgeDestinations());
            return result;
        }

        public Layout(float[] xCoordinates, float[] yCoordinates, int[] edgeSources, int[] edgeDestinations) {
            this.xCoordinates = xCoordinates;
            this.yCoordinates = yCoordinates;
            this.edgeSources = edgeSources;
            this.edgeDestinations = edgeDestinations;
        }

        public float[] getxCoordinates() {
            return xCoordinates;
        }

        public float[] getyCoordinates() {
            return yCoordinates;
        }

        public int[] getEdgeSources() {
            return edgeSources;
        }

        public int[] getEdgeDestinations() {
            return edgeDestinations;
        }
    }
}