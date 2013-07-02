package de.friedenhagen.javasquid.mavenplugin;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import java.io.File;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.sonar.graph.DirectedGraph;
import org.sonar.java.JavaConfiguration;
import org.sonar.java.JavaSquid;
import org.sonar.squid.api.CheckMessage;
import org.sonar.squid.api.CodeVisitor;
import org.sonar.squid.api.SourceCode;
import org.sonar.squid.api.SourceCodeEdge;
import org.sonar.squid.indexer.SquidIndex;

/**
 * Goal which touches a timestamp file.
 */
@Mojo(name = "touch", defaultPhase = LifecyclePhase.VERIFY)
public class MyMojo extends AbstractMojo {

    /**
     * Charset of the source files.
     */
    @Parameter(defaultValue = "UTF-8", property = "project.build.sourceEncoding", required = true)
    private String sourceEncoding;

    /**
     * Location of the sources.
     */
    @Parameter(property = "project.build.sourceDirectory", required = true, readonly = true)
    private File sourceDirectory;

    /**
     * Location of the file.
     */
    @Parameter(property = "project.build.outputDirectory", required = true, readonly = true)
    private File outputDirectory;

    public void execute() throws MojoExecutionException {
        final JavaSquid squid = new JavaSquid(new JavaConfiguration(Charset.forName(sourceEncoding)), new CodeVisitor[0]);
        getLog().info("sourceDirectory:" + sourceDirectory);
        getLog().info("outputDirectory:" + outputDirectory);
        squid.scanDirectories(Collections.singleton(sourceDirectory), Collections.singleton(outputDirectory));
        final DirectedGraph<SourceCode, SourceCodeEdge> graph = squid.getGraph();
        final Set<SourceCode> vertices = graph.getVertices();
        System.err.println("vertices.size:" + vertices.size());
        for (SourceCode sourceCode : vertices) {
            Set<CheckMessage> checkMessages = sourceCode.getCheckMessages();
            getLog().info("checkMessages:" + checkMessages);
        }
        List<SourceCodeEdge> edges = graph.getEdges(vertices);
        System.err.println("edges.size:" + edges.size());
        for (SourceCodeEdge edge : edges) {
            System.err.printf("%s:%d%n", edge.toString(), edge.getWeight());
        }               
    }
}
