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
        try (Arena offHeap = Arena.ofConfined()) {

            // load our native lib
            SymbolLookup tmap = SymbolLookup.libraryLookup(libNames.tmapFileName, offHeap);

            // get tmap::LayoutConfiguration
            // TODO:

            // make edges
            // TODO:

            // reserve output vector
            // TODO:

            // get handle for layout fun
            MethodHandle tmapLayout = Linker.nativeLinker().downcallHandle(
                    tmap.find(FunctionNames.tmapLayoutFromEdgeList).orElseThrow(),
                    // returns tuple of vectors and GraphProperties, input: int, edges (address), config(adress), bool, bool
                    FunctionDescriptor.of(ADDRESS, ADDRESS, ADDRESS, JAVA_BOOLEAN, JAVA_BOOLEAN));

            // call
            // TODO:
            tmapLayout.invoke();


        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

    }


    public static void main(String[] args) {
        ForeignAPI api = new ForeignAPI();
        api.testForeignGeneral();
    }


}
