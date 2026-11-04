package org.example;

public interface Visitor {
    void visit(FileNode file);
    void visit(DirectoryNode directory);
}