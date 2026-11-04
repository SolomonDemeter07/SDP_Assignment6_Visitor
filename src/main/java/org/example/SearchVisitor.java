package org.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchVisitor implements Visitor {
    private final String nameCriterion;
    private final long minSizeThreshold;
    private final List<String> matchedPaths;
    private final List<String> currentPathSegments;
    private int activeTraversalDepth;

    public SearchVisitor(String nameCriterion, long minSizeThreshold) {
        if (nameCriterion == null) {
            throw new IllegalArgumentException("Filename criterion cannot be null");
        }
        if (minSizeThreshold < 0) {
            throw new IllegalArgumentException("Minimum size threshold cannot be negative");
        }
        this.nameCriterion = nameCriterion;
        this.minSizeThreshold = minSizeThreshold;
        this.matchedPaths = new ArrayList<>();
        this.currentPathSegments = new ArrayList<>();
        this.activeTraversalDepth = 0;
    }

    public void reset() {
        this.matchedPaths.clear();
        this.currentPathSegments.clear();
        this.activeTraversalDepth = 0;
    }

    @Override
    public void visit(FileNode file) {
        if (activeTraversalDepth == 0) {
            matchedPaths.clear();
            currentPathSegments.clear();
        }
        if (matches(file)) {
            matchedPaths.add(buildRelativePath(file.getName()));
        }
        if (activeTraversalDepth == 0) {
            Collections.sort(matchedPaths);
        }
    }

    @Override
    public void visit(DirectoryNode directory) {
        boolean isRoot = (activeTraversalDepth == 0);
        if (isRoot) {
            matchedPaths.clear();
            currentPathSegments.clear();
        } else {
            currentPathSegments.add(directory.getName());
        }

        activeTraversalDepth++;
        try {
            for (Element child : directory.getChildren()) {
                child.accept(this);
            }
        } finally {
            activeTraversalDepth--;
            if (!isRoot) {
                currentPathSegments.remove(currentPathSegments.size() - 1);
            } else {
                Collections.sort(matchedPaths);
            }
        }
    }

    private boolean matches(FileNode file) {
        return file.getName().contains(nameCriterion) && file.getSize() >= minSizeThreshold;
    }

    private String buildRelativePath(String fileName) {
        if (currentPathSegments.isEmpty()) {
            return fileName;
        }
        return String.join("/", currentPathSegments) + "/" + fileName;
    }

    public List<String> getMatchedPaths() {
        return List.copyOf(matchedPaths);
    }
}