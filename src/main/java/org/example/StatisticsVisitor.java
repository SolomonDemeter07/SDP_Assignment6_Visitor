package org.example;

public class StatisticsVisitor implements Visitor {
    private int fileCount;
    private int directoryCount;
    private int maxDepth;
    private int activeTraversalDepth;

    public void reset() {
        this.fileCount = 0;
        this.directoryCount = 0;
        this.maxDepth = 0;
        this.activeTraversalDepth = 0;
    }

    @Override
    public void visit(FileNode file) {
        if (activeTraversalDepth == 0) {
            fileCount = 0;
            directoryCount = 0;
            maxDepth = 0;
        }
        fileCount++;
        if (activeTraversalDepth > maxDepth) {
            maxDepth = activeTraversalDepth;
        }
    }

    @Override
    public void visit(DirectoryNode directory) {
        if (activeTraversalDepth == 0) {
            fileCount = 0;
            directoryCount = 0;
            maxDepth = 0;
        }
        directoryCount++;
        if (activeTraversalDepth > maxDepth) {
            maxDepth = activeTraversalDepth;
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

    public int getFileCount() {
        return fileCount;
    }

    public int getDirectoryCount() {
        return directoryCount;
    }

    public int getMaxDepth() {
        return maxDepth;
    }
}