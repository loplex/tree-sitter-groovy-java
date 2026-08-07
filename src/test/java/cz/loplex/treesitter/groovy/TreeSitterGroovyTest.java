package cz.loplex.treesitter.groovy;

import org.junit.jupiter.api.Test;
import org.treesitter.TSParser;
import org.treesitter.TSTree;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TreeSitterGroovyTest {

    @Test
    public void testParserInitializationAndParsing() {
        // A simple Groovy source code snippet
        String sourceCode = "def hello = 'world'";

        // Initialize parser and our custom Groovy language JNI bindings
        // This will automatically trigger the static initialization block
        // and extract/load the native library.
        TreeSitterGroovy language = new TreeSitterGroovy();

        TSTree tree;
        try (TSParser parser = new TSParser()) {
            parser.setLanguage(language);

            // Parse the code
            tree = parser.parseString(null, sourceCode);
        }

        // Verify the tree is valid
        assertNotNull(tree, "The parsed tree should not be null");
        assertNotNull(tree.getRootNode(), "The root node should not be null");
        
        // Verify the parser successfully identified the tree structure
        String sExpr = tree.getRootNode().toString();
        assertTrue(sExpr.contains("source_file"), "Root node should be a 'source_file'");
        assertTrue(sExpr.contains("string_literal"), "Tree should contain 'string_literal'");
        
        // Clean up resources if necessary
        // In the java-tree-sitter wrapper, TSTree and TSParser are usually managed by the JVM 
        // or have explicit close() depending on the wrapper version.
    }
}
