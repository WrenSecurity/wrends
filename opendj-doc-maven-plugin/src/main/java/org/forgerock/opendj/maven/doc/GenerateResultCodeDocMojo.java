/*
 * The contents of this file are subject to the terms of the Common Development and
 * Distribution License (the License). You may not use this file except in compliance with the
 * License.
 *
 * You can obtain a copy of the License at legal/CDDLv1.0.txt. See the License for the
 * specific language governing permission and limitations under the License.
 *
 * When distributing Covered Software, include this CDDL Header Notice in each file and include
 * the License file at legal/CDDLv1.0.txt. If applicable, add the following below the CDDL
 * Header, with the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions Copyright [year] [name of copyright owner]".
 *
 * Copyright 2015 ForgeRock AS.
 * Portions Copyright 2026 Wren Security
 */
package org.forgerock.opendj.maven.doc;

import static org.forgerock.opendj.maven.doc.Utils.*;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.forgerock.opendj.ldap.ResultCode;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Generates documentation source for LDAP result codes based on
 * {@code org.forgerock.opendj.ldap.ResultCode}.
 * <br>
 * This implementation parses the source to match Javadoc comments with result codes.
 * It is assumed that the class's ResultCode fields are named with result code enum values,
 * and that those fields have Javadoc comments describing each result code.
 */
@Mojo(name = "generate-result-code-doc", defaultPhase = LifecyclePhase.COMPILE)
public class GenerateResultCodeDocMojo extends AbstractMojo {
    /**
     * The Java file containing the source of the ResultCode class,
     * {@code org.forgerock.opendj.ldap.ResultCode}.
     * <br>
     * For example, {@code opendj-core/src/main/java/org/forgerock/opendj/ldap/ResultCode.java}.
     */
    @Parameter(required = true)
    private File resultCodeSource;

    /** The XML file to generate. */
    @Parameter(required = true)
    private File xmlFile;

    /**
     * Generates documentation source for LDAP result codes.
     *
     * @throws MojoExecutionException   Generation failed
     * @throws MojoFailureException     Not used
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        final Map<String, Object> map = new HashMap<>();
        map.put("year", new SimpleDateFormat("yyyy").format(new Date()));

        // The overall explanation in the generated doc is the class comment.
        final ClassOrInterfaceDeclaration resultCodeClass;
        try {
            resultCodeClass = getJavaClass();
        } catch (IOException e) {
            throw new MojoExecutionException("Could not read " + resultCodeSource.getPath(), e);
        }
        map.put("classComment", cleanComment(resultCodeClass.getJavadocComment()
                .map(c -> c.parse().toText())
                .orElse("")));

        // Documentation for each result code comes from the Javadoc for the code,
        // and from the value and friendly name of the code.
        final Map<String, Object> comments = new HashMap<>();
        for (final FieldDeclaration field : resultCodeClass.getFields()) {
            for (final VariableDeclarator variable : field.getVariables()) {
                if (field.getElementType().asString().equals("ResultCode")) {
                    comments.put(variable.getNameAsString(), cleanComment(field.getJavadocComment()
                            .map(c -> c.parse().toText())
                            .orElse("")));
                }
            }
        }
        map.put("resultCodes", getResultCodesDoc(comments));

        final String template = "appendix-ldap-result-codes.ftl";
        try {
            writeStringToFile(applyTemplate(template, map), xmlFile);
        } catch (IOException e) {
            throw new MojoExecutionException("Could not write to " + xmlFile.getPath(), e);
        }
        getLog().info("Wrote " + xmlFile.getPath());
    }

    /**
     * Returns an object to access to the result code Java source.
     * @return An object to access to the result code Java source.
     * @throws IOException  Could not read the source
     */
    private ClassOrInterfaceDeclaration getJavaClass() throws IOException {
        final CompilationUnit cu = StaticJavaParser.parse(resultCodeSource);
        return cu.getClassByName("ResultCode")
                .orElseThrow(() -> new IOException("Could not find ResultCode class"));
    }

    /**
     * Returns a clean string for use in generated documentation.
     * @param comment   The comment to clean.
     * @return A clean string for use in generated documentation.
     */
    private String cleanComment(String comment) {
        return stripCodeValueSentences(stripTags(convertLineSeparators(comment))).trim();
    }

    /**
     * Returns a string with line separators converted to spaces.
     * @param string    The string to convert.
     * @return A string with line separators converted to spaces.
     */
    private String convertLineSeparators(String string) {
        return string.replaceAll(System.lineSeparator(), " ");
    }

    /**
     * Returns a string with the HTML tags removed.
     * @param string    The string to strip.
     * @return A string with the HTML tags removed.
     */
    private String stripTags(String string) {
        return string.replaceAll("<[^>]*>", "");
    }

    /**
     * Returns a string with lines sentences of the following form removed:
     * This result code corresponds to the LDAP result code value of &#x7b;&#x40;code 0&#x7d;.
     * @param string    The string to strip.
     * @return A string with lines sentences of the matching form removed.
     */
    private String stripCodeValueSentences(String string) {
        return string
                .replaceAll("This result code corresponds to the LDAP result code value of \\{@code \\d+\\}.", "");
    }

    /**
     * Returns a list of documentation objects for all result codes.
     * @param comments  A map of field names to the clean comments.
     * @return A list of documentation objects for all result codes.
     */
    private List<Map<String, Object>> getResultCodesDoc(Map<String, Object> comments) {
        final List<Map<String, Object>> list = new LinkedList<>();
        if (comments == null || comments.isEmpty()) {
            return list;
        }

        for (ResultCode resultCode : ResultCode.values()) {
            final Map<String, Object> doc = new HashMap<>();
            doc.put("intValue", resultCode.intValue());
            doc.put("name", resultCode.getName());
            final Object comment = comments.get(resultCode.asEnum().toString());
            doc.put("comment", comment);
            list.add(doc);
        }
        return list;
    }
}
