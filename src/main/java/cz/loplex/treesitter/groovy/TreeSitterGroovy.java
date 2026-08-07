package cz.loplex.treesitter.groovy;

import org.treesitter.TSLanguage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.HexFormat;

public class TreeSitterGroovy extends TSLanguage {
    static {
        loadNativeLibrary();
    }

    public TreeSitterGroovy() {
        super(tree_sitter_groovy());
    }

    @Override
    public TSLanguage copy() {
        return new TreeSitterGroovy();
    }

    public static native long tree_sitter_groovy();

    private static boolean loaded = false;

    public static synchronized void loadNativeLibrary() {
        if (loaded) return;
        
        String osName = System.getProperty("os.name").toLowerCase();
        String archName = System.getProperty("os.arch").toLowerCase();
        
        String os;
        String extension;
        if (osName.contains("mac")) {
            os = "darwin";
            extension = ".dylib";
        } else if (osName.contains("win")) {
            os = "windows";
            extension = ".dll";
        } else {
            os = "linux";
            extension = ".so";
        }
        
        String arch;
        if (archName.equals("x86_64")) {
            arch = "amd64";
        } else if (archName.equals("arm64")) {
            arch = "aarch64";
        } else {
            arch = archName;
        }
        
        String resourcePath = "/native/" + os + "-" + arch + "/libtree-sitter-groovy" + extension;
        
        try (InputStream is = TreeSitterGroovy.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new RuntimeException("Native library not found in classpath for platform " + os + "-" + arch + ": " + resourcePath);
            }
            extractAndLoad(is, extension, os, arch);
            loaded = true;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load native library for tree-sitter-groovy", e);
        }
    }
    
    private static void extractAndLoad(InputStream is, String extension, String os, String arch) throws Exception {
        Path cacheDir = Paths.get(System.getProperty("user.home"), ".cache", "tree-sitter-groovy");
        Files.createDirectories(cacheDir);

        // Extract to a temporary file
        Path tempLib = Files.createTempFile(cacheDir, "libtree-sitter-groovy", extension);
        Files.copy(is, tempLib, StandardCopyOption.REPLACE_EXISTING);
        
        // Calculate hash to uniquely identify this binary version
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (
            InputStream fis = Files.newInputStream(tempLib);
            DigestInputStream dis = new DigestInputStream(fis, digest)
        ) {
            dis.readAllBytes();
        }

        String hash = HexFormat.of().formatHex(digest.digest(), 0, 8);
        
        // Final filename now includes the hash
        Path nativeLib = cacheDir.resolve("libtree-sitter-groovy-" + os + "-" + arch + "-" + hash + extension);

        if (!Files.exists(nativeLib)) {
            try {
                Files.move(tempLib, nativeLib, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                Files.copy(tempLib, nativeLib, StandardCopyOption.REPLACE_EXISTING);
            }
        }
        Files.deleteIfExists(tempLib);

        System.load(nativeLib.toAbsolutePath().toString());
    }
}
