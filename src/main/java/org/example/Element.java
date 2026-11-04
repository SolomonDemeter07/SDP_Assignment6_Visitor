package org.example;

public interface Element {
    String getName();
    DirectoryNode getParent();
    void assignParent(DirectoryNode parent);
    void accept(Visitor visitor);
}