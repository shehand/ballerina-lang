package org.ballerinalang.starter;

import com.google.gson.Gson;
import org.ballerinalang.model.elements.PackageID;
import org.ballerinalang.testerina.core.TesterinaConstants;
import org.ballerinalang.testerina.core.TesterinaRegistry;
import org.ballerinalang.testerina.core.entity.Test;
import org.ballerinalang.testerina.core.entity.TestJsonData;
import org.ballerinalang.testerina.core.entity.TestMetaData;
import org.ballerinalang.testerina.core.entity.TestSuite;
import org.ballerinalang.testerina.util.TestarinaClassLoader;
import org.ballerinalang.testerina.util.TesterinaUtils;
import org.wso2.ballerinalang.compiler.util.Name;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class MockTestSuit {

    private static PrintStream outsStream;
    private static PrintStream errStream;

    MockTestSuit(PrintStream outStream, PrintStream errStream) {
        errStream = errStream;
        outsStream = outStream;
    }

    void readJson() {
        Path jsonCachePath = Paths.get(System.getProperty("java.io.tmpdir"), TesterinaConstants.TESTERINA_TEST_SUITE);
        try {
            BufferedReader br = new BufferedReader(new FileReader(jsonCachePath.toString()));

            //convert the json string back to object
            Gson gson = new Gson();
            TestJsonData response = gson.fromJson(br, TestJsonData.class);
            initTestSuit(Paths.get(response.getSourceRootPath()), Paths.get(response.getJarPath()), response.getModuleJarName(), response);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    private static void initTestSuit(Path sourceRootPath, Path jarPath, String moduleJarName, TestJsonData testJsonData) {
        TestMetaData testMetaData = new TestMetaData();

        // set the PackageID
        Name orgName = new Name(testJsonData.getOrgName());
        Name version = new Name(testJsonData.getVersion());
        List<Name> nameComp = new ArrayList<>(Arrays.asList(testJsonData.getNameComps()));
        testMetaData.setPackageID(new PackageID(orgName, nameComp, version));

        // set the tests into the test suit and init the test suit
        List<Test> testNames = new ArrayList<>();
        Map<String, String> tmpTestMap = testJsonData.getTestFunctionNames();
        tmpTestMap.forEach((functionName, className) -> {
            if(!functionName.contains("$")) {
                Test test = new Test();
                test.setTestName(functionName);
                testNames.add(test);
            }
        });
        TestSuite testSuite = new TestSuite(testJsonData.getPackageName());
        testSuite.setTests(testNames);

        // create the test suit map
        Map<String, TestSuite> testSuiteMap = new HashMap<>();
        testSuiteMap.put(testJsonData.getPackageName(), testSuite);

        // set the test suit into the Testerina registry
        TesterinaRegistry testerinaRegistry = TesterinaRegistry.getInstance();
        testerinaRegistry.setTestSuites(testSuiteMap);

        // setting the function name maps with class names
        HashMap<String, String> callableFunctionNames = testJsonData.getCallableFunctionNames();
        HashMap<String, String> testFunctionNames = testJsonData.getTestFunctionNames();
        testMetaData.setCallableFunctionNames(callableFunctionNames);
        testMetaData.setTestFunctionNames(testFunctionNames);

        // set the init/start/stop function names for both normal and test functions
        testMetaData.setInitFunctionName(testJsonData.getInitFunctionName());
        testMetaData.setStartFunctionName(testJsonData.getStartFunctionName());
        testMetaData.setStopFunctionName(testJsonData.getStopFunctionName());
        testMetaData.setTestInitFunctionName(testJsonData.getTestInitFunctionName());
        testMetaData.setTestStartFunctionName(testJsonData.getTestStartFunctionName());
        testMetaData.setTestStopFunctionName(testJsonData.getTestStopFunctionName());

        // set rest required data
        testMetaData.setPackageName(testJsonData.getPackageName());
        testMetaData.setHasTestablePackages(true);

        // create testerina class loader to run the tests
        TestarinaClassLoader testarinaClassLoader = new TestarinaClassLoader(jarPath,
                Paths.get(sourceRootPath.toString(), "target", "tmp").toFile(), moduleJarName);

        HashMap<TestMetaData, TestarinaClassLoader> testMetaDataMap = new HashMap<>();
        testMetaDataMap.put(testMetaData, testarinaClassLoader);
        startTestSuit(sourceRootPath, testMetaDataMap);
    }

    void sapleTestSuit(Path sourceRootPath, Path jarPath, String moduleJarName) {
        TestMetaData metaData = new TestMetaData();

        Name orgName = new Name("shehan");
        Name version = new Name("0.1.0");
        List<Name> nameComp = new ArrayList<>();
        nameComp.add(new Name("sample"));

        List<Test> tests = new ArrayList<>();
        Test test = new Test();
        test.setTestName("testFunction");
        tests.add(test);

        TestSuite testSuite = new TestSuite("shehan/sample:0.1.0");
        testSuite.setTests(tests);

        Map<String, TestSuite> testSuiteMap = new HashMap<>();
        testSuiteMap.put("shehan/sample:0.1.0", testSuite);

        TesterinaRegistry testerinaRegistry = TesterinaRegistry.getInstance();
        testerinaRegistry.setTestSuites(testSuiteMap);

        HashMap<String, String> normalFunctionNames = new HashMap<>();
        HashMap<String, String> testFunctionNames = new HashMap<>();
        normalFunctionNames.put("add", "shehan.sample.main");
        normalFunctionNames.put("main", "shehan.sample.main");
        testFunctionNames.put("testFunction", "shehan.sample.tests.main_test");
        testFunctionNames.put("$annot_func$0", "shehan.sample.tests.main_test");

        metaData.setInitFunctionName("shehan/sample:0.1.0.<init>");
        metaData.setStartFunctionName("shehan/sample:0.1.0.<start>");
        metaData.setStopFunctionName("shehan/sample:0.1.0.<stop>");
        metaData.setTestInitFunctionName("shehan/sample:0.1.0.<testinit>");
        metaData.setTestStartFunctionName("shehan/sample:0.1.0.<teststart>");
        metaData.setTestStopFunctionName("shehan/sample:0.1.0.<teststop>");
        metaData.setPackageID(new PackageID(orgName, nameComp, version));
        metaData.setPackageName("shehan/sample:0.1.0");
        metaData.setCallableFunctionNames(normalFunctionNames);
        metaData.setTestFunctionNames(testFunctionNames);
        metaData.setHasTestablePackages(true);

        TestarinaClassLoader testarinaClassLoader = new TestarinaClassLoader(jarPath,
                Paths.get(sourceRootPath.toString(), "target", "tmp").toFile(), moduleJarName);

        HashMap<TestMetaData, TestarinaClassLoader> testMetaDataMap = new HashMap<>();
        testMetaDataMap.put(metaData, testarinaClassLoader);
        startTestSuit(sourceRootPath, testMetaDataMap);
    }

    private static void startTestSuit(Path sourceRootPath, Map<TestMetaData, TestarinaClassLoader> classLoaderMap) {
        TesterinaUtils.execTests(sourceRootPath, classLoaderMap, outsStream, errStream);
    }
}
