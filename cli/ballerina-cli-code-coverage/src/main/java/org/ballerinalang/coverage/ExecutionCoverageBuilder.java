/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.ballerinalang.coverage;

import org.ballerinalang.coverage.buildcontext.BuildContext;
import org.ballerinalang.tool.LauncherUtils;
import org.wso2.ballerinalang.compiler.util.CompilerContext;
import org.wso2.ballerinalang.compiler.util.ProjectDirConstants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Java class to generate the coverage report.
 *
 * @since 1.0.0
 */
public class ExecutionCoverageBuilder {

    private Path executableJarPath;
    private Path sourceRootPath;
    private Path targetPath;
    private Path compiledSourceJarPath;
    private String orgName;
    private Path balHome;
    private String packageName;
    private BuildContext buildContext;

    public ExecutionCoverageBuilder(Path sourceRootPath, Path executableJarPath, Path sourcePath, Path targetPath,
                                    Path compiledSourceJarPath, CompilerContext compilerContext) {
        this.sourceRootPath = sourceRootPath;
        this.executableJarPath = executableJarPath;
        this.targetPath = targetPath;
        this.compiledSourceJarPath = compiledSourceJarPath;
        this.packageName = sourcePath.toString();
        this.balHome = Paths.get(System.getProperty("ballerina.home"));
        this.buildContext = new BuildContext(this.sourceRootPath, this.targetPath, sourcePath, compilerContext);
    }

    public boolean generateExecFile() {

        if (!generateDirectories()) {
            throw LauncherUtils.createLauncherException("Couldn't create the directories -> Coverage, Extracted.");
        }

        String cmd = "java -javaagent:"
                + this.balHome
                .resolve(ProjectDirConstants.BALLERINA_HOME_BRE)
                .resolve(ProjectDirConstants.BALLERINA_HOME_LIB)
                .resolve(CoverageConstants.AGENT_FILE_NAME).toString()
                + "=destfile="
                + this.targetPath
                .resolve(ProjectDirConstants.TARGET_COVERAGE_DIRECTORY)
                .resolve(CoverageConstants.EXEC_FILE_NAME).toString()
                + " -jar "
                + this.executableJarPath.toString();

        try {
            Process proc = Runtime.getRuntime().exec(cmd);
            proc.waitFor();
            return true;
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }

    // got the code from https://stackoverflow.com/questions/1529611/
    // how-to-write-a-java-program-which-can-extract-a-jar-file-and-store-its-data-in-s
    public void unzipCompiledSource() {
        boolean directoriesCreated = false;

        InputStream is = null;
        FileOutputStream fo = null;
        try (JarFile jarFile = new JarFile(new File(this.compiledSourceJarPath.toString()))) {
            Enumeration<JarEntry> enu = jarFile.entries();

            while (enu.hasMoreElements()) {
                String destDir = this.targetPath
                        .resolve(ProjectDirConstants.TARGET_COVERAGE_DIRECTORY)
                        .resolve(CoverageConstants.EXTRACTED_DIRECTORY_NAME).toString();

                JarEntry je = enu.nextElement();

                File fl = new File(destDir, je.getName());
                if (!fl.exists()) {
                    directoriesCreated = fl.getParentFile().mkdirs();
                    fl = new File(destDir, je.getName());
                }
                if (je.isDirectory()) {
                    continue;
                }

                is = jarFile.getInputStream(je);
                fo = new FileOutputStream(fl);
                while (is.available() > 0) {
                    fo.write(is.read());
                }
            }

            if (directoriesCreated) {
                this.buildContext.out().println();
            }

            String path = this.targetPath
                    .resolve(ProjectDirConstants.TARGET_COVERAGE_DIRECTORY)
                    .resolve(CoverageConstants.EXTRACTED_DIRECTORY_NAME).toString();
            getOrgName();
            removeUnnecessaryFilesFromUnzip(path);


        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        } catch (Exception ignored) {

        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception ignored) {

                }
            }
            if (fo != null) {
                try {
                    fo.close();
                } catch (Exception ignored) {

                }
            }
        }
    }

    private void getOrgName() {

        try {
            BufferedReader tomlReader = Files.newBufferedReader(this.sourceRootPath
                    .resolve(ProjectDirConstants.MANIFEST_FILE_NAME), StandardCharsets.UTF_8);
            String lines;
            String [] lineItems = null;
            while ((lines = tomlReader.readLine()) != null) {
                if (lines.startsWith("org-name")) {
                    lineItems = lines.split("=");
                }
            }
            tomlReader.close();

            if (lineItems == null) {
                throw new RuntimeException("Please update your "
                        + ProjectDirConstants.MANIFEST_FILE_NAME
                        + " with your organization name to proceed.");
            } else {
                String orgNameWithMarks = lineItems[1];
                this.orgName = orgNameWithMarks.substring(2, orgNameWithMarks.length() - 1);
            }

        } catch (RuntimeException | IOException ignored) {

        }
    }

    private void removeUnnecessaryFilesFromUnzip(String path) {

        Path filePath = Paths.get(path);
        File folder = new File(filePath.resolve(this.orgName).resolve(this.packageName).toString());

        File[] listOfFiles = folder.listFiles();

        boolean filesRemoved = false;

        if (listOfFiles != null) {
            for (File listOfFile : listOfFiles) {
                if (listOfFile.isFile()) {
                    String fileName = listOfFile.toString();
                    File file = new File(fileName);
                    if (listOfFile.toString().contains("___init")) {
                        filesRemoved = file.delete();
                    } else if (listOfFile.toString().contains("Frame")
                            && listOfFile.toString().contains("module")) {
                        filesRemoved = file.delete();
                    } else if (listOfFile.toString().contains("Frame")
                            && listOfFile.toString().contains(this.orgName)) {
                        filesRemoved = file.delete();
                    }
                }
            }
        }

        if (filesRemoved) {
            this.buildContext.out().println();
        }
    }

    public void createSourceFileDirectory() {

        // to copy the source files to coverage directory
        String src = this.sourceRootPath
                .resolve(ProjectDirConstants.SOURCE_DIR_NAME).toString();
        String dest = this.targetPath
                .resolve(ProjectDirConstants.TARGET_COVERAGE_DIRECTORY)
                .resolve(this.orgName).toString();

        File srcDir = new File(src);
        File destDir = new File(dest);

        if (!srcDir.exists()) {
            throw new RuntimeException("Directory doesn't exists");

        } else {

            try {
                copyFolder(srcDir, destDir);
            } catch (RuntimeException e) {
                throw new RuntimeException(e);
            } catch (Exception e) {
                this.buildContext.out().println(e);
            }
        }

        // delete already copied test source
        String deletingDir = this.targetPath
                .resolve(ProjectDirConstants.TARGET_COVERAGE_DIRECTORY)
                .resolve(this.orgName)
                .resolve(this.packageName)
                .resolve(ProjectDirConstants.TEST_DIR_NAME).toString();

        File deletingFile = new File(deletingDir);

        boolean deletedCopiedTestDir = deleteDirectory(deletingFile);

        if (deletedCopiedTestDir) {
            // to copy the test source from coverage directory
            String testSrc = this.sourceRootPath
                    .resolve(ProjectDirConstants.SOURCE_DIR_NAME)
                    .resolve(this.packageName)
                    .resolve(ProjectDirConstants.TEST_DIR_NAME).toString();
            Path testDir = this.targetPath
                    .resolve(ProjectDirConstants.TARGET_COVERAGE_DIRECTORY)
                    .resolve(this.orgName)
                    .resolve(this.packageName)
                    .resolve(ProjectDirConstants.TEST_DIR_NAME)
                    .resolve(ProjectDirConstants.TEST_DIR_NAME);

            File testSrcDir = new File(testSrc);
            boolean createTestDir = generateDirectoryForGivenPath(testDir);
            File testDestDir = new File(testDir.toString());

            if (!testSrcDir.exists() && !createTestDir) {
                throw new RuntimeException("Directory doesn't exists");
            } else {

                try {
                    copyFolder(testSrcDir, testDestDir);
                } catch (RuntimeException e) {
                    throw new RuntimeException(e);
                } catch (Exception e) {
                    this.buildContext.out().println(e);
                }
            }
        }
    }

    private void copyFolder(File src, File dest) {

        if (src.isDirectory()) {

            boolean directoryCreated = true;
            //if directory not exists, create it
            if (!dest.exists()) {
                directoryCreated = dest.mkdir();
            }

            if (directoryCreated) {
                //list all the directory contents
                String[] files = src.list();

                if (files != null) {
                    for (String file : files) {
                        //construct the src and dest file structure
                        File srcFile = new File(src, file);
                        File destFile = new File(dest, file);
                        //recursive copy
                        copyFolder(srcFile, destFile);
                    }
                }
            }

        } else {
            //if file, then copy it
            //Use bytes stream to support all file types
            try (InputStream in = new FileInputStream(src); OutputStream out = new FileOutputStream(dest)) {
                byte[] buffer = new byte[1024];

                int length;
                //copy the file content in bytes
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }

            } catch (RuntimeException e) {
                throw new RuntimeException(e);
            } catch (Exception ignored) {

            }
        }
    }

    public void generateCoverageReport() {

        String execFilePath = this.targetPath
                .resolve(ProjectDirConstants.TARGET_COVERAGE_DIRECTORY)
                .resolve(CoverageConstants.EXEC_FILE_NAME).toString();
        String classFilesPath = this.targetPath
                .resolve(ProjectDirConstants.TARGET_COVERAGE_DIRECTORY)
                .resolve(CoverageConstants.EXTRACTED_DIRECTORY_NAME)
                .resolve(this.orgName).toString();
        String sourceFilesPath = this.targetPath
                .resolve(ProjectDirConstants.TARGET_COVERAGE_DIRECTORY).toString();
        String reportPath = this.targetPath
                .resolve(ProjectDirConstants.TARGET_COVERAGE_DIRECTORY).toString();

        String cmd = "java -jar " + this.balHome
                .resolve(ProjectDirConstants.BALLERINA_HOME_BRE)
                .resolve(ProjectDirConstants.BALLERINA_HOME_LIB)
                .resolve(CoverageConstants.CLI_FILE_NAME).toString()
                + " report " + execFilePath
                + " --classfiles " + classFilesPath
                + " --sourcefiles " + sourceFilesPath
                + " --html " + reportPath
                + " --name " + CoverageConstants.COVERAGE_REPORT_NAME;

        try {
            Process proc = Runtime.getRuntime().exec(cmd);
            proc.waitFor();
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw LauncherUtils.createLauncherException(e.toString());
        }
    }

    private boolean generateDirectories() {
        boolean coverageDirectoryCreated;
        boolean extractedDirectoryCreated;

        coverageDirectoryCreated = generateDirectoryForGivenPath(this.targetPath
                .resolve(ProjectDirConstants.TARGET_COVERAGE_DIRECTORY));
        extractedDirectoryCreated = generateDirectoryForGivenPath(this.targetPath
                .resolve(ProjectDirConstants.TARGET_COVERAGE_DIRECTORY)
                .resolve(CoverageConstants.EXTRACTED_DIRECTORY_NAME));

        return coverageDirectoryCreated && extractedDirectoryCreated;
    }

    // got the code from https://stackoverflow.com/questions/3634853/how-to-create-a-directory-in-java/3634879
    private boolean generateDirectoryForGivenPath(Path path){
        boolean generated;
        generated = new File(path.toString()).mkdirs();
        return generated;
    }

    // got the code from https://stackoverflow.com/questions/20281835/how-to-delete-a-folder-with-files-using-java
    private boolean deleteDirectory(File file) {
        boolean deleted;

        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                if (! Files.isSymbolicLink(f.toPath())) {
                    deleteDirectory(f);
                }
            }
        }
        deleted = file.delete();
        return deleted;
    }
}
