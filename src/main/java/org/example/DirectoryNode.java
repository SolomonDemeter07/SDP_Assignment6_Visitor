package org.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DirectoryNode implements Element {
    private final String name;
    private final List<Element> children;
    private DirectoryNode parent;

    public DirectoryNode(String name) {
        validateName(name);
        this.name = name.trim();
        this.children = new ArrayList<>();
    }

    private void validateName(String candidateName) {
        if (candidateName == null || candidateName.trim().isEmpty() || candidateName.contains("/")) {
            throw new IllegalArgumentException("Invalid directory name: " + candidateName);
        }
    }

    public DirectoryNode addChild(Element child) {
        if (child == null) {
            throw new IllegalArgumentException("Child element cannot be null");
        }
        ensureNoCycle(child);
        ensureUniqueSiblingName(child.getName());
        child.assignParent(this);
        children.add(child);
        return this;
    }

    private void ensureNoCycle(Element candidateChild) {
        DirectoryNode current = this;
        while (current != null) {
            if (current == candidateChild) {
                throw new IllegalArgumentException("Cycle detected involving directory: " + candidateChild.getName());
            }
            current = current.getParent();
        }
    }

    private void ensureUniqueSiblingName(String childName) {
        for (Element existing : children) {
            if (existing.getName().equals(childName)) {
                throw new IllegalArgumentException("Duplicate sibling name in directory " + name + ": " + childName);
            }
        }
    }

    public List<Element> getChildren() {
        return Collections.unmodifiableList(children);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public DirectoryNode getParent() {
        return parent;
    }

    @Override
    public void assignParent(DirectoryNode newParent) {
        if (newParent == null) {
            throw new IllegalArgumentException("Parent directory cannot be null");
        }
        if (this.parent != null) {
            throw new IllegalStateException("Element already belongs to a parent directory: " + name);
        }
        this.parent = newParent;
    }

    @Override
    public void accept(Visitor visitor) {
        if (visitor == null) {
            throw new IllegalArgumentException("Visitor cannot be null");
        }
        visitor.visit(this);
    }
}
