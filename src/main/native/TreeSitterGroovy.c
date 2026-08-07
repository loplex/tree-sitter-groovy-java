#include <jni.h>

void *tree_sitter_groovy();

JNIEXPORT jlong JNICALL Java_cz_loplex_treesitter_groovy_TreeSitterGroovy_tree_1sitter_1groovy(JNIEnv *env, jclass cls) {
    return (jlong) tree_sitter_groovy();
}
