package org.example;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        DirectoryNode root = new DirectoryNode("gym-tracker-project");

        DirectoryNode docs = new DirectoryNode("docs");
        docs.addChild(new FileNode("nutrition-plan.pdf", 4500));
        docs.addChild(new FileNode("workout-log.txt", 1200));

        DirectoryNode src = new DirectoryNode("src");
        DirectoryNode core = new DirectoryNode("core");
        core.addChild(new FileNode("SquatCalculator.java", 3200));
        core.addChild(new FileNode("BenchPressTracker.java", 2800));
        core.addChild(new FileNode("notes.txt", 300));

        DirectoryNode assets = new DirectoryNode("assets");
        DirectoryNode emptyArchive = new DirectoryNode("archive");
        assets.addChild(emptyArchive);
        assets.addChild(new FileNode("barbell-icon.png", 8100));

        src.addChild(core);
        root.addChild(docs);
        root.addChild(src);
        root.addChild(assets);
        root.addChild(new FileNode("README.md", 900));

        TotalSizeVisitor sizeVisitor = new TotalSizeVisitor();
        root.accept(sizeVisitor);
        System.out.println("=== 1. Total Size Operation ===");
        System.out.println("Total subtree size: " + sizeVisitor.getTotalSize() + " bytes");

        SearchVisitor searchVisitor = new SearchVisitor(".java", 2500);
        root.accept(searchVisitor);
        List<String> matches = searchVisitor.getMatchedPaths();
        System.out.println("\n=== 2. Search Operation (filter='.java', minSize=2500) ===");
        for (String path : matches) {
            System.out.println("Matched: " + path);
        }

        StatisticsVisitor statsVisitor = new StatisticsVisitor();
        root.accept(statsVisitor);
        System.out.println("\n=== 3. Statistics Operation ===");
        System.out.println("Files: " + statsVisitor.getFileCount());
        System.out.println("Directories: " + statsVisitor.getDirectoryCount());
        System.out.println("Max Depth: " + statsVisitor.getMaxDepth());
    }
}