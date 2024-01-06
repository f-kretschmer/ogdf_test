package de.unijena.bioinf.TreeVisualization;

import java.util.Arrays;
import java.util.HashSet;

public class TreeVisualizer {
    static {
        System.loadLibrary("src/LayoutFromEdgeList_internal.cpp"); // TODO: unable to find the File?
        /*
        String libName = "layout.cc"; // The name of the file in resources/ dir
        URL url = TreeVisualizer.class.getResource("/" + libName);
        File tmpDir;
        try {
            tmpDir = Files.createTempDirectory("tempDir").toFile();
            tmpDir.deleteOnExit();
            File nativeLibTmpFile = new File(tmpDir, libName);
            nativeLibTmpFile.deleteOnExit();
            assert url != null;
            try (InputStream in = url.openStream()) {
                Files.copy(in, nativeLibTmpFile.toPath());
        }
        System.load(nativeLibTmpFile.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

         */
    }

    public static void main(String[] args) {
        // Run for testing
        TreeVisualizer testlayout = new TreeVisualizer();
        EdgeList testedges = new EdgeList(new int[]{1, 2, 3}, new int[]{2, 3, 1}, new float[]{0.5F, 0.2F, 0.3F});
        System.out.println(testlayout.LayoutFromEdgeList(testedges));
    }

    // Implementation at
    // https://github.com/reymond-group/tmap/blob/32d2d91de3c9a15efde3d6169172d31f0d45642b/src/_tmap/layout.cc#L472
    private native float[][] LayoutFromEdgeList_internal(int number_of_nodes, int[] sources, int[] destinations,
            float[] weights, int number_of_edges);
    // (https://github.com/reymond-group/tmap/blob/32d2d91de3c9a15efde3d6169172d31f0d45642b/src/_tmap/layout.hh#L294)
    // compile with : javac -h . TreeVisualizer.java
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

        float[][] mixedReturntype = LayoutFromEdgeList_internal(number_of_nodes, sources, destinations, weights,
                weights.length);

        float[] xcoordinates = mixedReturntype[0];
        float[] ycoordinates = mixedReturntype[1];
        int[] returnedSources = new int[mixedReturntype[2].length];
        int[] returnedDestinations = new int[mixedReturntype[3].length];

        for (int i = 0; i < mixedReturntype[2].length; i++) {
            returnedSources[i] = (int) mixedReturntype[2][i];
        }
        for (int i = 0; i < mixedReturntype[3].length; i++) {
            returnedDestinations[i] = (int) mixedReturntype[3][i];
        }

        // If this fails, something went horribly wrong...
        assert xcoordinates.length == ycoordinates.length;
        assert returnedSources.length == returnedDestinations.length;

        return new Layout(xcoordinates, ycoordinates, returnedSources, returnedDestinations);

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
            if (!(o instanceof EdgeList))
                return false;
            EdgeList edgeList = (EdgeList) o;
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
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (!(o instanceof Layout))
                return false;
            Layout layout = (Layout) o;
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