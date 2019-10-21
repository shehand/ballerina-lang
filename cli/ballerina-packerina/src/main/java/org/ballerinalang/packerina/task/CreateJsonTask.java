package org.ballerinalang.packerina.task;

import com.google.gson.Gson;
import org.ballerinalang.packerina.buildcontext.BuildContext;
import org.ballerinalang.packerina.buildcontext.BuildContextField;
import org.ballerinalang.testerina.core.TesterinaConstants;
import org.ballerinalang.testerina.core.entity.TestJsonData;
import org.ballerinalang.testerina.util.TestarinaClassLoader;
import org.ballerinalang.tool.util.BFileUtil;
import org.wso2.ballerinalang.compiler.tree.BLangPackage;
import org.wso2.ballerinalang.compiler.util.Name;

import java.io.FileWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Task for create json for test execution.
 */
public class CreateJsonTask implements Task {

    @Override
    public void execute(BuildContext buildContext) {
        Path sourceRootPath = buildContext.get(BuildContextField.SOURCE_ROOT);

        Map<BLangPackage, TestarinaClassLoader> programFileMap = new HashMap<>();
        List<BLangPackage> moduleBirMap = buildContext.getModules();
        // Only tests in packages are executed so default packages i.e. single bal files which has the package name
        // as "." are ignored. This is to be consistent with the "ballerina test" command which only executes tests
        // in packages.
        moduleBirMap.stream()
                .forEach(bLangPackage -> {

                    Path jarPath = buildContext.getTestJarPathFromTargetCache(bLangPackage.packageID);
                    Path modulejarPath = buildContext.getJarPathFromTargetCache(bLangPackage.packageID).getFileName();
                    // subsitute test jar if module jar if tests not exists
                    if (Files.notExists(jarPath)) {
                        jarPath = modulejarPath;
                    }
                    String modulejarName = modulejarPath != null ? modulejarPath.toString() : "";
                    TestarinaClassLoader classLoader = new TestarinaClassLoader(jarPath,
                            Paths.get(sourceRootPath.toString(), "target", "tmp").toFile(),
                            modulejarName);
                    programFileMap.put(bLangPackage, classLoader);
                    extractDataFromBLangPackage(programFileMap, sourceRootPath, jarPath, modulejarName);
                });
    }

    /**
     * Write the content into a json
     *
     * @param testMetaData Data that are parsed to the json
     */
    private static void writeToJson(TestJsonData testMetaData) {
        String tmpProperty = System.getProperty("java.io.tmpdir");
        Path tmpJsonPath = Paths.get(tmpProperty, TesterinaConstants.TESTERINA_TEST_SUITE);
        try (Writer writer = new FileWriter(tmpJsonPath.toString())) {
            Gson gson = new Gson();

            String json = gson.toJson(testMetaData);
            writer.write(json);
        }catch (Exception e) {
            System.err.println(e);
        }
    }

    /**
     * Extract data from the given bLangPackage
     *
     * @param programFileMap map of the bLangPackage and TesterinaClassLoader
     */
    private static void extractDataFromBLangPackage(Map<BLangPackage, TestarinaClassLoader> programFileMap,
                                                    Path sourceRootPath, Path jarPath, String moduleJarName) {

        programFileMap.forEach((source, classLoader) -> {
            String initFunctionName = source.initFunction.name.value;
            String startFunctionName = source.startFunction.name.value;
            String stopFunctionName = source.stopFunction.name.value;
            String testInitFunctionName = source.getTestablePkg().initFunction.name.value;
            String testStartFunctionName = source.getTestablePkg().startFunction.name.value;
            String testStopFunctionName = source.getTestablePkg().stopFunction.name.value;
            String orgName = source.packageID.getOrgName().value;
            String version = source.packageID.getPackageVersion().value;
            Name[] nameComps = new Name[source.packageID.getNameComps().size()];
            nameComps = source.packageID.getNameComps().toArray(nameComps);
            String packageName;
            if (source.packageID.getName().getValue().equals(".")) {
                packageName = source.packageID.getName().getValue();
            } else {
                packageName = orgName + "/" + source.packageID.getName().value + ":" + version;
                //packageName = TesterinaUtils.getFullModuleName(source.packageID.getName().getValue());
            }
            String hasTestablePackages = Boolean.toString(source.hasTestablePackage());

            HashMap<String, String> normalFunctionNames = new HashMap<>();
            HashMap<String, String> testFunctionNames = new HashMap<>();

            source.functions.stream().forEach(function -> {
                try {
                    String functionClassName = BFileUtil.getQualifiedClassName(source.packageID.orgName.value,
                            source.packageID.name.value,
                            getClassName(function.pos.src.cUnitName));
                    normalFunctionNames.put(function.name.value, functionClassName);
                } catch (RuntimeException e) {
                    // we do nothing here
                }
            });

            source.getTestablePkg().functions.stream().forEach(function -> {
                try {
                    String functionClassName = BFileUtil.getQualifiedClassName(source.packageID.orgName.value,
                            source.packageID.name.value,
                            getClassName(function.pos.src.cUnitName));
                    testFunctionNames.put(function.name.value, functionClassName);
                } catch (RuntimeException e) {
                    // we do nothing here
                }
            });

            // set data
            TestJsonData testJsonData = new TestJsonData();
            testJsonData.setInitFunctionName(initFunctionName);
            testJsonData.setStartFunctionName(startFunctionName);
            testJsonData.setStopFunctionName(stopFunctionName);
            testJsonData.setTestInitFunctionName(testInitFunctionName);
            testJsonData.setTestStartFunctionName(testStartFunctionName);
            testJsonData.setTestStopFunctionName(testStopFunctionName);
            testJsonData.setCallableFunctionNames(normalFunctionNames);
            testJsonData.setTestFunctionNames(testFunctionNames);
            testJsonData.setPackageName(packageName);
            testJsonData.setOrgName(orgName);
            testJsonData.setVersion(version);
            testJsonData.setNameComps(nameComps);
            testJsonData.setHasTestablePackages(hasTestablePackages);
            testJsonData.setSourceRootPath(sourceRootPath.toString());
            testJsonData.setJarPath(jarPath.toString());
            testJsonData.setModuleJarName(moduleJarName);
            testJsonData.setPackageID(source.packageID);
            // write to json
            writeToJson(testJsonData);
        });
    }

    /**
     * return the function name
     *
     * @param function String value of a function
     * @return function name
     */
    private static String getClassName(String function) {
        return function.replace(".bal", "").replace("/", ".");
    }

}
