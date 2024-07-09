package whackamole.whackamole;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import whackamole.whackamole.helpers.TestBase;

public class ResourceManagerTest extends TestBase {
    public static File testFileDirectory = new File("src/test/resources/resourceMerges");
    public static File outputFileDirectory = new File("output/resourceMerges");


    public static List<String> getTestFiles() {
        var testFilesIndexes = new ArrayList<String>();
        for (File i : testFileDirectory.listFiles()) {
            if (i.isDirectory()) {
                testFilesIndexes.add(i.getName());
            }
        }
        return testFilesIndexes;
    }

    @BeforeAll()
    public static void CleanOuputFolder() {
        if (! outputFileDirectory.exists()) {
            outputFileDirectory.mkdirs();
        } else {
            for(var file : outputFileDirectory.listFiles()) {
                file.delete();
            }
        }
    }

    @MethodSource("getTestFiles")
    @ParameterizedTest(name = "{index} merge: {0}")
    public void PropertyFileMergesCorrectly(String folder) throws NoSuchMethodException, SecurityException, FileNotFoundException, IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        var merge = ResourceManager.class.getDeclaredMethod("mergeLanguageFile", String.class, String.class);
        merge.setAccessible(true);
        try (
            var sourceS = new FileInputStream(testFileDirectory + "/" + folder + "/src.prop");
            var destS = new FileInputStream(testFileDirectory + "/" + folder + "/dst.prop");
            var resultS = new FileInputStream(testFileDirectory + "/" + folder + "/res.prop");
            var outputS = new FileOutputStream(outputFileDirectory + "/" + folder + "_out.prop")
        ) {
            String  sourceData = new String(sourceS.readAllBytes()),
                    destinationData = new String(destS.readAllBytes()),
                    resultData = new String(resultS.readAllBytes());

            String result = (String) merge.invoke(null, sourceData, destinationData);
            outputS.write(result.getBytes("utf-8"));

            softly.then(result).isEqualTo(resultData).as("Merged result is not equal to output");
        }
    }
}
