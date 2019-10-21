package org.ballerinalang.testerina.core.entity;

import org.ballerinalang.model.elements.PackageID;
import org.ballerinalang.tool.util.BFileUtil;
import org.wso2.ballerinalang.compiler.tree.BLangPackage;

import java.util.HashMap;

public class TestMetaData {
    private String initFunctionName;
    private String startFunctionName;
    private String stopFunctionName;
    private String testInitFunctionName;
    private String testStartFunctionName;
    private String testStopFunctionName;
    private PackageID packageID;
    private boolean hasTestablePackages;
    private String packageName;
    private static HashMap<String, String> callableFunctionNames;
    private static HashMap<String, String> testFunctionNames;

    public TestMetaData(BLangPackage bLangPackage, String packageName) {
        this.initFunctionName = bLangPackage.initFunction.name.value;
        this.startFunctionName = bLangPackage.startFunction.name.value;
        this.stopFunctionName = bLangPackage.stopFunction.name.value;
        this.testInitFunctionName = bLangPackage.getTestablePkg().initFunction.name.value;
        this.testStartFunctionName = bLangPackage.getTestablePkg().startFunction.name.value;
        this.testStopFunctionName = bLangPackage.getTestablePkg().stopFunction.name.value;
        this.packageID = bLangPackage.packageID;
        this.hasTestablePackages = bLangPackage.hasTestablePackage();
        this.packageName = packageName;
        computePackageFunctions(bLangPackage);
    }

    public void setStartFunctionName(String startFunctionName) {
        this.startFunctionName = startFunctionName;
    }

    public void setStopFunctionName(String stopFunctionName) {
        this.stopFunctionName = stopFunctionName;
    }

    public void setTestInitFunctionName(String testInitFunctionName) {
        this.testInitFunctionName = testInitFunctionName;
    }

    public void setTestStartFunctionName(String testStartFunctionName) {
        this.testStartFunctionName = testStartFunctionName;
    }

    public void setTestStopFunctionName(String testStopFunctionName) {
        this.testStopFunctionName = testStopFunctionName;
    }

    public void setHasTestablePackages(boolean hasTestablePackages) {
        this.hasTestablePackages = hasTestablePackages;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public TestMetaData () {}

    public String getInitFunctionName() {
        return initFunctionName;
    }

    public void setInitFunctionName(String initFunctionName) {
        this.initFunctionName = initFunctionName;
    }

    public String getStartFunctionName() {
        return startFunctionName;
    }

    public String getStopFunctionName() {
        return stopFunctionName;
    }

    public String getTestInitFunctionName() {
        return testInitFunctionName;
    }

    public String getTestStartFunctionName() {
        return testStartFunctionName;
    }

    public String getTestStopFunctionName() {
        return testStopFunctionName;
    }

    public PackageID getPackageID() {
        return packageID;
    }

    public void setPackageID(PackageID packageID) {
        this.packageID = packageID;
    }

    public static void setCallableFunctionNames(HashMap<String, String> callableFunctionNames) {
        TestMetaData.callableFunctionNames = callableFunctionNames;
    }

    public static void setTestFunctionNames(HashMap<String, String> testFunctionNames) {
        TestMetaData.testFunctionNames = testFunctionNames;
    }

    private static void computePackageFunctions(BLangPackage bLangPackage) {
        callableFunctionNames = new HashMap<>();
        testFunctionNames = new HashMap<>();

        bLangPackage.functions.stream().forEach(function -> {
            try {
                String functionClassName = BFileUtil.getQualifiedClassName(bLangPackage.packageID.orgName.value,
                        bLangPackage.packageID.name.value,
                        getClassName(function.pos.src.cUnitName));
                callableFunctionNames.put(function.name.value, functionClassName);
            } catch (RuntimeException e) {
                // we do nothing here
            }
        });

        bLangPackage.getTestablePkg().functions.stream().forEach(function -> {
            try {
                String functionClassName = BFileUtil.getQualifiedClassName(bLangPackage.packageID.orgName.value,
                        bLangPackage.packageID.name.value,
                        getClassName(function.pos.src.cUnitName));
                testFunctionNames.put(function.name.value, functionClassName);
            } catch (RuntimeException e) {
                // we do nothing here
            }
        });
    }

    private static String getClassName(String function) {
        return function.replace(".bal", "").replace("/", ".");
    }

    public HashMap<String, String> getNormalFunctionNames() {
        return callableFunctionNames;
    }

    public HashMap<String, String> getTestFunctionNames() {
        return testFunctionNames;
    }

    public boolean isHasTestablePackages() {
        return hasTestablePackages;
    }

    public String getPackageName() {
        return packageName;
    }
}
