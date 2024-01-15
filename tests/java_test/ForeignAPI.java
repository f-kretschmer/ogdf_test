import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

import static java.lang.foreign.ValueLayout.*;

public class ForeignAPI {

    private static class libNames {
        public static final String odgfFileName = "libOGDF.so";
        public static final String tmapFileName = "libtmap.so";
    }

    private static class FunctionNames {
        public static final String ogdfBinomial = "_ZN4ogdf4Math8binomialEii";
        public static final String tmapLayoutFromEdgeList = "_ZN4tmap18LayoutFromEdgeListEjRKSt6vectorISt5tupleIJjjfEESaIS2_EENS_19LayoutConfigurationEbb";
        public static final String tmapLayoutFromLSHForest = "_ZN4tmap19LayoutFromLSHForestERNS_9LSHForestENS_19LayoutConfigurationEbbb";
        public static final String tmapLayoutConfiguration = "_ZN4tmap14LayoutInternalERN4ogdf17EdgeWeightedGraphIfEEjNS_19LayoutConfigurationERNS_15GraphPropertiesE"; // ist das einzige mit "LayoutConfiguration" das noch übrig ist
    }





    // some general tests on how this interface works
    void testForeignGeneral() {
        // check if paths are correct

        // This line creates a SymbolLookup object, which can be used to find native symbols on the C library path.
        SymbolLookup stdlib = Linker.nativeLinker().defaultLookup();
        // This line creates a MethodHandle object for the strlen() function. The MethodHandle object contains all
        // the information needed to call the native function, such as the function pointer and the function signature.
        MethodHandle strlen = Linker.nativeLinker().downcallHandle(
                stdlib.find("strlen").orElseThrow(),
                FunctionDescriptor.of(JAVA_LONG, ADDRESS));

        // This line creates an Arena object. An Arena is a region of memory that can be used to allocate off-heap
        // memory. The try-with-resources statement ensures that the Arena object is automatically closed when the code
        // block exits.
        try (Arena offHeap = Arena.ofConfined()) {

            // This line allocates a region of off-heap memory to store the string "Java 21 rocks! Yay!!!". The
            // MemorySegment object represents the allocated off-heap memory.
            MemorySegment str = offHeap.allocateUtf8String("Java 21 is the issh");

            // This line calls the strlen() function to calculate the length of the string "Java 21 works". The strlen()
            // function takes a pointer to a string as input and returns the length of the string as output.
            long len = (long) strlen.invoke(str);
            System.out.println("len = " + len);

            // System.load("/home/user/IdeaProjects/ogdf_test/binaries/libOGDF.so");


            // load our native lib
            SymbolLookup ogdf = SymbolLookup.libraryLookup(libNames.odgfFileName, offHeap);
            SymbolLookup tmap = SymbolLookup.libraryLookup(libNames.tmapFileName, offHeap);
            // check whether our function is present
            // NOTE: have to use the mangled C++ symbols, which are shit for namespaces... (-> objdump -T x.so | grep y)
            // NOTE: could be alleviated by using "extern"; this requires mod. the source code which we don't want
            System.out.println(ogdf.find(FunctionNames.ogdfBinomial).isPresent());
            System.out.println(tmap.find(FunctionNames.tmapLayoutFromEdgeList).isPresent());
            // get handle for symbol
            MethodHandle tmapLayout = Linker.nativeLinker().downcallHandle(
                    tmap.find(FunctionNames.tmapLayoutFromEdgeList).orElseThrow(),
                    // returns tuple of vectors and GraphProperties, input: int, edges (address), config(adress), bool, bool
                    FunctionDescriptor.of(ADDRESS, ADDRESS, ADDRESS, JAVA_BOOLEAN, JAVA_BOOLEAN));
            MethodHandle binomial = Linker.nativeLinker().downcallHandle(
                    ogdf.find(FunctionNames.ogdfBinomial).orElseThrow(),
                    // return, arg1, arg2, ...
                    FunctionDescriptor.of(JAVA_INT, JAVA_INT, JAVA_INT));

            // nice automatic casting for primitives...
            Object result = binomial.invoke(10, 2);
            System.out.println(result);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    void testTMAP(){
        // get std library (not the sexual kind)
        Linker linker = Linker.nativeLinker();
        SymbolLookup stdlib = linker.defaultLookup();

        // Allocate onHeap Memory
        Integer[] input1 = {1, 2, 3, 4, 1};
        Integer[] input2 = {2, 3, 4, 1, 3};
        Float[] weights = {0.5F, 1.0F, 0.5F, 1.0F, 1.2F};

        try (Arena offHeap = Arena.ofConfined()) {

            // Declare native std functions
            MethodHandle makeTuple;
            MethodHandle makeEmptyVector;
            MethodHandle appendtoVector;
            MethodHandle getTupleElement;
            MethodHandle getVectorElement;

            // Define native functions
            //TODO: Std function names differ from expected.

            /*
            makeTuple = linker.downcallHandle(stdlib.find("make_tuple").orElseThrow(),
                    // std::tuple<uint32_t, uint32_t, float>
                    FunctionDescriptor.of(ADDRESS, JAVA_INT, JAVA_INT, JAVA_FLOAT));

            makeVector = linker.downcallHandle(stdlib.find("vector").orElseThrow(),
                    // std::vector<std::tuple>
                    FunctionDescriptor.of(ADDRESS, ADDRESS));

            appendtoVector = linker.downcallHandle(stdlib.find("push_back").orElseThrow(),
                    // std::vector.push_back(std::tuple)
                    FunctionDescriptor.of(ADDRESS, ADDRESS));

            getTupleElement = linker.downcallHandle(stdlib.find("get").orElseThrow(),
                    // std::get<i>(tuple) -> i als Position in Tuple
                    FunctionDescriptor.of(ADDRESS, JAVA_INT, ADDRESS));

            getVectorElement = linker.downcallHandle(stdlib.find("").orElseThrow(),
                    // std::vector[i]
                    FunctionDescriptor.of());
            */

            // load our native lib
            SymbolLookup tmap = SymbolLookup.libraryLookup(libNames.tmapFileName, offHeap);
            SymbolLookup lib = SymbolLookup.libraryLookup("libLayoutFromEdgeList_internal.so", offHeap);

            // get tmap::LayoutConfiguration
            // TODO: -> Notwendig da optionaler parameter?

            // TODO: make edges
            // MemorySegment firstEdge = (MemorySegment) makeTuple.invoke(input1[0], input2[0], weights[0]);
            // MemorySegment edgeVector = (MemorySegment) makeVector.invoke(firstEdge);
            for(int i=1; i<weights.length; i++) { //Eintrag 0 schon in Vector?
                // MemorySegment edge = (MemorySegment) makeTuple.invoke(input1[i], input2[i], weights[i]);
                // MemorySegment edgeVector = (MemorySegment) appendtoVector.invoke(edge);
            }

            // get handle for layout fun
            /*
            MethodHandle tmapLayout = Linker.nativeLinker().downcallHandle(
                    tmap.find(FunctionNames.tmapLayoutFromEdgeList).orElseThrow(),
                    // returns tuple of vectors and GraphProperties, input: int, edges (address), config(adress), bool, bool
                    // FunctionDescriptor.of(ADDRESS, JAVA_INT, ADDRESS, JAVA_BOOLEAN, JAVA_BOOLEAN));
                    // Config(address), bool, bool might be optional
                    // optional Descriptor
                    FunctionDescriptor.of(ADDRESS, JAVA_INT, ADDRESS));
            */
            // call Function
            // MemorySegment result = tmapLayout.invoke(weights.length, edgeVector);

            // Extract results from Pointer
            // TODO:





            /**
             *  Alternative if we can include the custom script
             *
             **/

            // Allocate off-heap memory to store pointers
            MemorySegment input1mem = offHeap.allocateArray(JAVA_INT, 1, 2, 3, 4, 1);
            MemorySegment input2mem = offHeap.allocateArray(JAVA_INT, 2, 3, 4, 1, 3);
            MemorySegment input3mem = offHeap.allocateArray(JAVA_FLOAT, 0.5F, 1.0F, 0.5F, 1.0F, 1.2F);

            MethodHandle libLayout = Linker.nativeLinker().downcallHandle(
                    lib.find("_Z28LayoutFromEdgeList_internalsiPiS_Pfi").orElseThrow(),
                    // returns tuple of vectors and GraphProperties, input: int, edges (address), config(adress), bool, bool
                    //FunctionDescriptor.of(ADDRESS, JAVA_INT, ADDRESS, JAVA_BOOLEAN, JAVA_BOOLEAN));
                    FunctionDescriptor.of(ADDRESS, JAVA_INT, ADDRESS, ADDRESS, ADDRESS, JAVA_INT));

            MemorySegment result = (MemorySegment) libLayout.invoke(5, input1mem, input2mem, input3mem, 6);
            // Note: prints 0 to console, because I forgot to remove the print of GraphProperties.mst_weight (line 12)


            // Extract results from Pointer
            //TODO


            System.out.println(result);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

    }


    public static void main(String[] args) {
        ForeignAPI api = new ForeignAPI();
        api.testForeignGeneral();
        api.testTMAP();
    }


}
