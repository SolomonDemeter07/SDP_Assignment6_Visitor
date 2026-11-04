package org.example;

public class FileNode implements Element {
    private final String name;
    private final long size;
    private DirectoryNode parent;

    public FileNode(String name, long size) {
        validateName(name);
        if (size < 0) {
            throw new IllegalArgumentException("File size cannot be negative: " + size);
        }
        this.name = name.trim();
        this.size = size;
    }

    private void validateName(String candidateName) {
        if (candidateName == null || candidateName.trim().isEmpty() || candidateName.contains("/")) {
            throw new IllegalArgumentException("Invalid file name: " + candidateName);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    public long getSize() {
        return size;
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