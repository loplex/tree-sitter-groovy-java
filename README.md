# Tree-sitter Groovy for Java

This project provides a pre-packaged JNI wrapper for the [Tree-sitter Groovy](https://github.com/dekobon/tree-sitter-groovy) parser, allowing you to parse Groovy source code directly from Java.

The project features a **Fat JAR** build pipeline that automatically cross-compiles native bindings for Linux, macOS, and Windows. This means you do not need to worry about compiling the native C code on the end-user's machine. The correct native library is automatically extracted and loaded at runtime.

## Installation

Add the dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>cz.loplex</groupId>
    <artifactId>tree-sitter-groovy-java</artifactId>
    <version>0.2.2-1</version>
</dependency>
```

## Usage

This library binds the Groovy parser directly into the JVM. It is built on top of the standard `java-tree-sitter` bindings.

```java
import org.treesitter.TSParser;
import org.treesitter.TSTree;
import cz.loplex.treesitter.groovy.TreeSitterGroovy;

public class Main {
    public static void main(String[] args) {
        // Create a new parser instance
        TSParser parser = new TSParser();
        
        // Initialize the Groovy language
        // (This automatically extracts and loads the correct native JNI library)
        parser.setLanguage(new TreeSitterGroovy());
        
        // Parse some Groovy code
        String sourceCode = "def hello = 'world'";
        TSTree tree = parser.parseString(null, sourceCode);
        
        // Print the syntax tree
        System.out.println(tree.getRootNode().toString());
    }
}
```

## Building Locally

To compile the Java code and the native JNI library locally, you just need a standard JDK 17+ and a C compiler (`gcc` or `clang`).

```bash
# Clone the repository and fetch the parser submodule
git clone --recursive https://github.com/loplex/tree-sitter-groovy-java.git

cd tree-sitter-groovy-java

# Build everything
mvn clean install
```

The Maven `exec-maven-plugin` will automatically invoke the `build-native.sh` script to compile the native library for your current OS and architecture, and then package it into the resulting JAR.

## Releasing and Publishing

See [RELEASING.md](RELEASING.md) for detailed instructions on how the GitHub Actions pipeline works, how cross-compilation is configured, and how to publish new versions to Maven Central.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
