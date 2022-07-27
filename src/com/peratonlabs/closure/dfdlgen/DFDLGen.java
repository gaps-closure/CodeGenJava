/**
 * Copyright (c) 2021 All rights reserved
 * Peraton Labs, Inc.
 *
 * Proprietary and confidential. Unauthorized copy or use of this file, via
 * any medium or mechanism is strictly prohibited. 
 *
 * @author tchen
 *
 * Mar 7, 2022
 */
package com.peratonlabs.closure.dfdlgen;

import java.io.File;
import java.io.IOException;

import clsvis.ProjectConfigIO;
import clsvis.model.Class_;
import clsvis.model.ProjectConfig;
import clsvis.process.importer.BaseProjectImporter;
import clsvis.process.importer.CompiledClassImporter;

public class DFDLGen
{
    private BaseProjectImporter projectImporter;
    private ProjectConfig config;
    
    private String rootClass = "com.peratonlabs.closure.codegen.rpc.serialization.RPCObject";
    private String jarFile = "test/rpc.jar";
    
    private void addClasses() throws Exception {
        if (config.isPathToBeUsedForProjectLoad()) {
            loadProjectConfig();
        }
        else if (config.isPathSet()) {
        }

        if (!config.classPaths.isEmpty()) {
            addToClasspath();
        }
        if (!config.importPaths.isEmpty()) {
            importClasses();
        }
    }

    private void loadProjectConfig() throws IOException {
        logInfo( "Opening project: " + config.path );

        ProjectConfig newConfig = ProjectConfigIO.load( config.path );
        config.setContent( config.path, newConfig );
    }

    private void addToClasspath() {
        logInfo("Adding elements to the class path");

        int before = projectImporter.getClassPaths().size();
        projectImporter.addClassPaths(config.classPaths);
        int after = projectImporter.getClassPaths().size();
        int delta = after - before;

        if (delta > 0) {
            logInfo(String.format("%d new element(s) added to class path; total count: %d", delta, after));
        }
        else {
            logWarning("No new elements added to class path");
        }
    }

    private void importClasses() {
        CompiledClassImporter classImporter = projectImporter.getClassImporter();

        int before = classImporter.getImportedClasses().size();
        projectImporter.importProject(config.importPaths);
        int after = classImporter.getImportedClasses().size();
        int failed = classImporter.getNotImportedClassesCount();
        int delta = after - before;

        if (delta > 0) {
            String msg = (failed == 0)
                    ? String.format("imported %d classes", delta)
                    : String.format("imported %d classes, failed %d classes", delta, failed);
            logInfo(msg);
        }
        else {
            logWarning("No classes imported");
        }
    }

    private void logInfo(String s) {
        System.out.println(s);
    } 
    private void logWarning(String s) {
        System.err.println(s);
    } 
    
    public void run(File path) {
        File jar = new File(jarFile);
        File[] files = new File[] { jar };
    
        config = new ProjectConfig(null, new File[0], files);
        config.path = null;
        projectImporter = new BaseProjectImporter();
        CompiledClassImporter classImporter = projectImporter.getClassImporter();
        projectImporter.initClassLoader(jar);
        
        classImporter.importClass(rootClass);
    }
    
    public void load() {
        File jar = new File(jarFile);
        File[] files = new File[] { jar };
    
        config = new ProjectConfig(null, new File[0], files);
        config.path = null;
        projectImporter = new BaseProjectImporter();
        
        try {
            addClasses();
            
            CompiledClassImporter classImporter = projectImporter.getClassImporter();
            Class_ clazz = classImporter.getImportedClass(rootClass);
            
            if (clazz == null) {
                System.err.println("class not loaded: " + rootClass);
                return;
            }
            ClassToDFDL converter = new ClassToDFDL(classImporter);
            converter.convert(clazz);
            classImporter.showImported();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        DFDLGen bridge = new DFDLGen();
        bridge.load();
    }
}
