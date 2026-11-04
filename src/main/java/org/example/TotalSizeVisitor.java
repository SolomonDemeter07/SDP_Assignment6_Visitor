package org.example;

public class TotalSizeVisitor implements Visitor {
    private long totalSize;
    private int activeTraversalDepth;

    public void reset() {
        this.totalSize = 0L;
        this.activeTraversalDepth = 0;
    }

    @Override
    public void visit(FileNode file) {
        if (activeTraversalDepth == 0) {
            totalSize = 0L;
        }
        totalSize += file.getSize();
    }

    @Override
    public void visit(DirectoryNode directory) {
        if (activeTraversalDepth == 0) {
            totalSize = 0L;
        }
        activeTraversalDepth++;
        try {
            for (Element child : directory.getChildren()) {
                child.accept(this);
            }
        } finally {
            activeTraversalDepth--;
        }
    }

    public long getTotalSize() {
        return totalSize;
    }
}