/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.lr.blueprint.plugin;

import java.io.File;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.xbean.finder.ClassFinder;

/**
 * Generates blueprint from spring annotations
 *
 * @goal blueprint-generate
 * @phase process-classes
 * @execute phase="generate-resources"
 * @requiresDependencyResolution compile+runtime
 * @inheritByDefault false
 * @description Generates blueprint file from spring annotations @Component, @Autowire and @Value
 */
public class GenerateBlueprint extends AbstractMojo {

    /**
     * The maven project.
     *
     * @parameter default-value="${project}"
     * @required
     */
    protected MavenProject project;

    /**
     * @parameter
     * @required
     */
    protected List<String> scanPaths;

    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            ClassFinder finder = createProjectScopeFinder();
            String buildDir = project.getBuild().getOutputDirectory();
            File file = new File(buildDir, "OSGI-INF/blueprint/autowire.xml");
            file.getParentFile().mkdirs();
            new Generator(finder, scanPaths.toArray(new String[]{})).generate(new FileOutputStream(file));
        } catch (Exception e) {
            throw new MojoExecutionException("Error building commands help", e);
        }
    }

    private ClassFinder createProjectScopeFinder() throws MalformedURLException {
        List<URL> urls = new ArrayList<>();

        urls.add( new File(project.getBuild().getOutputDirectory()).toURI().toURL() );
        for ( Artifact artifact : project.getArtifacts() ) {
            File file = artifact.getFile();
            if ( file != null ) {
                urls.add( file.toURI().toURL() );
            }
        }
        ClassLoader loader = new URLClassLoader(urls.toArray(new URL[urls.size()]), getClass().getClassLoader());
        return new ClassFinder(loader, urls);
    }

}