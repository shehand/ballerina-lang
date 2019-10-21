package org.ballerinalang.packerina.task;

import org.ballerinalang.packerina.buildcontext.BuildContext;
import org.ballerinalang.packerina.buildcontext.BuildContextField;
import org.ballerinalang.packerina.buildcontext.sourcecontext.SingleFileContext;
import org.ballerinalang.packerina.buildcontext.sourcecontext.SingleModuleContext;
import org.wso2.ballerinalang.compiler.tree.BLangPackage;
import org.wso2.ballerinalang.compiler.util.ProjectDirConstants;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.ballerinalang.tool.LauncherUtils.createLauncherException;
import static org.wso2.ballerinalang.compiler.util.ProjectDirConstants.BLANG_COMPILED_JAR_EXT;

public class CreateTestExecutableTask implements Task {

    private boolean skipCopyLibsFromDist = false;

    public Path getTestExecutableJarPath() {
        return testExecutableJarPath;
    }

    private Path testExecutableJarPath;

    public CreateTestExecutableTask(boolean skipCopyLibsFromDist) {
        this.skipCopyLibsFromDist = skipCopyLibsFromDist;
    }

    public CreateTestExecutableTask() {}

    @Override
    public void execute(BuildContext buildContext) {
        Optional<BLangPackage> modulesWithEntryPoints = buildContext.getModules().stream()
                .filter(m -> m.symbol.entryPointExists)
                .findAny();

        if (modulesWithEntryPoints.isPresent()) {
            buildContext.out().println();
            buildContext.out().println("Generating test executables");
            for (BLangPackage module : buildContext.getModules()) {
                if (module.symbol.entryPointExists) {
                    Path executablePath = buildContext.getTestExecutablePathFromTarget(module.packageID);
                    testExecutableJarPath = executablePath;
                    copyJarFromCachePath(buildContext, module, executablePath);
                    // Copy ballerina runtime all jar
                    URI uberJarUri = URI.create("jar:" + executablePath.toUri().toString());
                    // Load the to jar to a file system
                    try (FileSystem toFs = FileSystems.newFileSystem(uberJarUri, Collections.emptyMap())) {
                        if (!skipCopyLibsFromDist) {
                            copyRuntimeAllJar(buildContext, toFs);
                        }
                        copyTestStarterJar(buildContext, toFs);
                        assembleExecutable(buildContext, module, toFs);
                        toFs.close();
                        createManifestFile(buildContext.get(BuildContextField.TARGET_DIR), buildContext, executablePath);
                    } catch (IOException e) {
                        throw createLauncherException("unable to extract the uber jar :" + e.getMessage());
                    }
                }
            }
        } else {
            switch (buildContext.getSourceType()) {
                case SINGLE_BAL_FILE:
                    SingleFileContext singleFileContext = buildContext.get(BuildContextField.SOURCE_CONTEXT);
                    throw createLauncherException("no entry points found in '" + singleFileContext.getBalFile() + "'.");
                case SINGLE_MODULE:
                    SingleModuleContext singleModuleContext = buildContext.get(BuildContextField.SOURCE_CONTEXT);
                    throw createLauncherException("no entry points found in '" + singleModuleContext.getModuleName() +
                            "'.\n" +
                            "Use `ballerina build -c` to compile the module without building executables.");
                case ALL_MODULES:
                    throw createLauncherException("no entry points found in any of the modules.\n" +
                            "Use `ballerina build -c` to compile the modules without building executables.");
                default:
                    throw createLauncherException("unknown source type found when creating executable.");
            }
        }
    }

    private void copyJarFromCachePath(BuildContext buildContext, BLangPackage bLangPackage, Path executablePath) {
        Path jarFromCachePath = buildContext.getTestJarPathFromTargetCache(bLangPackage.packageID);
        try {
            // Copy the jar from cache to bin directory
            Files.copy(jarFromCachePath, executablePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw createLauncherException("unable to copy the jar from cache path :" + e.getMessage());
        }
    }

    private void copyTestStarterJar(BuildContext buildContext, FileSystem toFs) {
        String balHomePath = buildContext.get(BuildContextField.HOME_REPO).toString();
        String ballerinaVersion = System.getProperty("ballerina.version");
        String testStarterName = "testerina-launcher-" + ballerinaVersion + BLANG_COMPILED_JAR_EXT;
        String testJarName = "test.jar";
        Path testStarterJar = Paths.get(balHomePath, "bre", "lib", testStarterName);
        Path testJar = Paths.get(balHomePath, "bre", "lib", testJarName);
        try {
            copyFromJarToJar(testJar, toFs);
            copyFromJarToJar(testStarterJar, toFs);
        } catch (IOException e) {
            throw createLauncherException("unable to copy the ballerina runtime all jar :" + e.getMessage());
        }
    }

    private void copyRuntimeAllJar(BuildContext buildContext, FileSystem toFs) {
        String balHomePath = buildContext.get(BuildContextField.HOME_REPO).toString();
        String ballerinaVersion = System.getProperty("ballerina.version");
        String runtimeJarName = "ballerina-rt-" + ballerinaVersion + BLANG_COMPILED_JAR_EXT;
        Path runtimeAllJar = Paths.get(balHomePath, "bre", "lib", runtimeJarName);
        try {
            copyFromJarToJar(runtimeAllJar, toFs);
        } catch (IOException e) {
            throw createLauncherException("unable to copy the ballerina runtime all jar :" + e.getMessage());
        }
    }

    private void assembleExecutable(BuildContext buildContext, BLangPackage bLangPackage, FileSystem toFs) {
        try {
            Path targetDir = buildContext.get(BuildContextField.TARGET_DIR);
            Path tmpDir = targetDir.resolve(ProjectDirConstants.TARGET_TMP_DIRECTORY);
            // Check if the package has an entry point.
            if (bLangPackage.symbol.entryPointExists) {
                for (File file : tmpDir.toFile().listFiles()) {
                    if (!file.isDirectory()) {
                        copyFromJarToJar(file.toPath(), toFs);
                    }
                }
            }
            // Copy dependency jar
            // Copy dependency libraries
            // Executable is created at give location.
            // If no entry point is found we do nothing.
        } catch (IOException | NullPointerException e) {
            throw createLauncherException("unable to create the executable: " + e.getMessage());
        }
    }

    private static void copyFromJarToJar(Path fromJar, FileSystem toFs) throws IOException {
        Path to = toFs.getRootDirectories().iterator().next();
        URI moduleJarUri = URI.create("jar:" + fromJar.toUri().toString());
        // Load the from jar to a file system.
        try (FileSystem fromFs = FileSystems.newFileSystem(moduleJarUri, Collections.emptyMap())) {
            Path from = fromFs.getRootDirectories().iterator().next();
            // Walk and copy the files.
            Files.walkFileTree(from, new CreateExecutableTask.Copy(from, to));
        }
    }

    private static void createManifestFile(Path targetDir, BuildContext buildContext, Path executableJarPath) {
        List<String> lines = new ArrayList<>();
        lines.add("Manifest-Version: 1.0\n" +
                "Main-Class: org/ballerinalang/starter/Starter");
        try{
            Files.createDirectories(targetDir.resolve(ProjectDirConstants.CACHES_DIR_NAME).resolve(ProjectDirConstants.MANIFEST_CACHE_DIR_NAME));
            Path manifestFilePath = targetDir.resolve(ProjectDirConstants.CACHES_DIR_NAME).resolve(ProjectDirConstants.MANIFEST_CACHE_DIR_NAME).resolve("MANIFEST.MF");
            try {
                Files.write(manifestFilePath, lines, StandardCharsets.UTF_8);
                updateExistingManifestFile(executableJarPath.toString(), manifestFilePath.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            buildContext.err().println(e);
        }

    }

    private static void updateExistingManifestFile(String generatedJarPath, String manifestFilePath) {
        Map<String, String> env = new HashMap<>();
        env.put("create", "true");
        // locate file system by using the syntax
        // defined in java.net.JarURLConnection
        URI uri = URI.create("jar:file:" + generatedJarPath);

        try (FileSystem zipfs = FileSystems.newFileSystem(uri, env)) {
            Path externalTxtFile = Paths.get(manifestFilePath);
            Path pathInZipfile = zipfs.getPath("META-INF/MANIFEST.MF");
            // copy a file into the zip file
            Files.copy(externalTxtFile, pathInZipfile,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
