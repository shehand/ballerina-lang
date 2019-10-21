package org.ballerinalang.testerina.core.entity;

import org.ballerinalang.model.elements.PackageID;
import org.wso2.ballerinalang.compiler.util.Name;
import java.util.HashMap;

public class TestJsonData {
    private String orgName;
    private String version;
    private Name[] nameComps;
    private String initFunctionName;
    private String startFunctionName;
    private String stopFunctionName;
    private String testInitFunctionName;
    private String testStartFunctionName;
    private String testStopFunctionName;
    private String hasTestablePackages;
    private String packageName;
    private String sourceRootPath;
    private String jarPath;
    private String moduleJarName;
    private HashMap<String, String> callableFunctionNames;
    private HashMap<String, String> testFunctionNames;
    private PackageID packageID;

    public PackageID getPackageID() {
        return packageID;
    }

    public void setPackageID(PackageID packageID) {
        this.packageID = packageID;
    }

    public String getSourceRootPath() {
        return sourceRootPath;
    }

    public void setSourceRootPath(String sourceRootPath) {
        this.sourceRootPath = sourceRootPath;
    }

    public String getJarPath() {
        return jarPath;
    }

    public void setJarPath(String jarPath) {
        this.jarPath = jarPath;
    }

    public String getModuleJarName() {
        return moduleJarName;
    }

    public void setModuleJarName(String moduleJarName) {
        this.moduleJarName = moduleJarName;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Name[] getNameComps() {
        return nameComps;
    }

    public void setNameComps(Name[] nameComps) {
        this.nameComps = nameComps;
    }

    public String getInitFunctionName() {
        return initFunctionName;
    }

    public void setInitFunctionName(String initFunctionName) {
        this.initFunctionName = initFunctionName;
    }

    public String getStartFunctionName() {
        return startFunctionName;
    }

    public void setStartFunctionName(String startFunctionName) {
        this.startFunctionName = startFunctionName;
    }

    public String getStopFunctionName() {
        return stopFunctionName;
    }

    public void setStopFunctionName(String stopFunctionName) {
        this.stopFunctionName = stopFunctionName;
    }

    public String getTestInitFunctionName() {
        return testInitFunctionName;
    }

    public void setTestInitFunctionName(String testInitFunctionName) {
        this.testInitFunctionName = testInitFunctionName;
    }

    public String getTestStartFunctionName() {
        return testStartFunctionName;
    }

    public void setTestStartFunctionName(String testStartFunctionName) {
        this.testStartFunctionName = testStartFunctionName;
    }

    public String getTestStopFunctionName() {
        return testStopFunctionName;
    }

    public void setTestStopFunctionName(String testStopFunctionName) {
        this.testStopFunctionName = testStopFunctionName;
    }

    public String isHasTestablePackages() {
        return hasTestablePackages;
    }

    public void setHasTestablePackages(String hasTestablePackages) {
        this.hasTestablePackages = hasTestablePackages;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public HashMap<String, String> getCallableFunctionNames() {
        return callableFunctionNames;
    }

    public void setCallableFunctionNames(HashMap<String, String> callableFunctionNames) {
        this.callableFunctionNames = callableFunctionNames;
    }

    public HashMap<String, String> getTestFunctionNames() {
        return testFunctionNames;
    }

    public void setTestFunctionNames(HashMap<String, String> testFunctionNames) {
        this.testFunctionNames = testFunctionNames;
    }
}
