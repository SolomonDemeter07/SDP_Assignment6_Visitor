import org.example.*;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class FileSystemVisitorTest {

    private DirectoryNode buildSampleTree() {
        DirectoryNode root = new DirectoryNode("workspace");

        DirectoryNode alpha = new DirectoryNode("alpha");
        alpha.addChild(new FileNode("report-final.pdf", 5000));
        alpha.addChild(new FileNode("report-draft.pdf", 800));

        DirectoryNode beta = new DirectoryNode("beta");
        DirectoryNode deep = new DirectoryNode("deep");
        deep.addChild(new FileNode("report-2026.pdf", 6200));
        deep.addChild(new FileNode("summary.txt", 400));
        beta.addChild(deep);
        beta.addChild(new DirectoryNode("empty-folder"));

        root.addChild(alpha);
        root.addChild(beta);
        root.addChild(new FileNode("report-root.pdf", 3000));
        return root;
    }

    @Test
    void t1_bothElementTypesReachCorrectOverloadedVisitMethods() {
        List<String> callLog = new ArrayList<>();
        Visitor spyVisitor = new Visitor() {
            @Override
            public void visit(FileNode file) {
                callLog.add("FILE:" + file.getName());
            }

            @Override
            public void visit(DirectoryNode directory) {
                callLog.add("DIR:" + directory.getName());
                for (Element child : directory.getChildren()) {
                    child.accept(this);
                }
            }
        };

        DirectoryNode dir = new DirectoryNode("programs");
        FileNode file = new FileNode("deadlift.csv", 1024);
        dir.addChild(file);

        dir.accept(spyVisitor);

        assertEquals(List.of("DIR:programs", "FILE:deadlift.csv"), callLog);
    }

    @Test
    void t2_sizeAccumulationAcrossMultipleNestingLevelsIsCorrect() {
        DirectoryNode root = buildSampleTree();
        TotalSizeVisitor sizeVisitor = new TotalSizeVisitor();

        root.accept(sizeVisitor);

        assertEquals(15400L, sizeVisitor.getTotalSize());
    }

    @Test
    void t3_searchCombinesBothConfigurableFilterConditions() {
        DirectoryNode root = buildSampleTree();
        SearchVisitor searchVisitor = new SearchVisitor("report", 3000);

        root.accept(searchVisitor);

        List<String> expected = List.of(
                "alpha/report-final.pdf",
                "beta/deep/report-2026.pdf",
                "report-root.pdf"
        );
        assertEquals(expected, searchVisitor.getMatchedPaths());
    }

    @Test
    void t4_searchProducesDeterministicLexicographicallySortedRootRelativePaths() {
        DirectoryNode root = new DirectoryNode("root");
        DirectoryNode zDir = new DirectoryNode("z-folder");
        DirectoryNode aDir = new DirectoryNode("a-folder");

        zDir.addChild(new FileNode("log-b.txt", 200));
        aDir.addChild(new FileNode("log-a.txt", 200));
        root.addChild(zDir);
        root.addChild(aDir);
        root.addChild(new FileNode("log-root.txt", 200));

        SearchVisitor searchVisitor = new SearchVisitor("log", 100);
        root.accept(searchVisitor);

        List<String> expected = List.of(
                "a-folder/log-a.txt",
                "log-root.txt",
                "z-folder/log-b.txt"
        );
        assertEquals(expected, searchVisitor.getMatchedPaths());
    }

    @Test
    void t5_statisticsAreCorrectForNestingCountingAndMaximumDepth() {
        DirectoryNode root = buildSampleTree();
        StatisticsVisitor statsVisitor = new StatisticsVisitor();

        root.accept(statsVisitor);

        assertEquals(5, statsVisitor.getFileCount());
        assertEquals(5, statsVisitor.getDirectoryCount());
        assertEquals(3, statsVisitor.getMaxDepth());
    }

    @Test
    void t6_emptyStructuresAndNoMatchSearchCasesAreHandled() {
        DirectoryNode emptyRoot = new DirectoryNode("empty-root");

        TotalSizeVisitor sizeVisitor = new TotalSizeVisitor();
        emptyRoot.accept(sizeVisitor);
        assertEquals(0L, sizeVisitor.getTotalSize());

        StatisticsVisitor statsVisitor = new StatisticsVisitor();
        emptyRoot.accept(statsVisitor);
        assertEquals(0, statsVisitor.getFileCount());
        assertEquals(1, statsVisitor.getDirectoryCount());
        assertEquals(0, statsVisitor.getMaxDepth());

        DirectoryNode populatedRoot = buildSampleTree();
        SearchVisitor noMatchVisitor = new SearchVisitor("non-existent-file", 999999);
        populatedRoot.accept(noMatchVisitor);
        assertTrue(noMatchVisitor.getMatchedPaths().isEmpty());
    }

    @Test
    void t7_repeatExecutionsDoNotLeakOrMultiplyAccumulatedState() {
        DirectoryNode root = buildSampleTree();

        TotalSizeVisitor sizeVisitor = new TotalSizeVisitor();
        root.accept(sizeVisitor);
        long firstSize = sizeVisitor.getTotalSize();
        root.accept(sizeVisitor);
        assertEquals(firstSize, sizeVisitor.getTotalSize());

        SearchVisitor searchVisitor = new SearchVisitor("report", 3000);
        root.accept(searchVisitor);
        List<String> firstSearch = searchVisitor.getMatchedPaths();
        root.accept(searchVisitor);
        assertEquals(firstSearch, searchVisitor.getMatchedPaths());

        StatisticsVisitor statsVisitor = new StatisticsVisitor();
        root.accept(statsVisitor);
        int firstFileCount = statsVisitor.getFileCount();
        int firstDirCount = statsVisitor.getDirectoryCount();
        root.accept(statsVisitor);
        assertEquals(firstFileCount, statsVisitor.getFileCount());
        assertEquals(firstDirCount, statsVisitor.getDirectoryCount());
    }

    @Test
    void t8_invalidTreeMutationsAreRejectedWhileValidNestingRemainsSupported() {
        assertThrows(IllegalArgumentException.class, () -> new FileNode("bad.txt", -10));

        DirectoryNode root = new DirectoryNode("root");
        root.addChild(new FileNode("config.yaml", 100));

        assertThrows(IllegalArgumentException.class, () -> root.addChild(new FileNode("config.yaml", 200)));
        assertThrows(IllegalArgumentException.class, () -> root.addChild(new DirectoryNode("config.yaml")));

        FileNode sharedFile = new FileNode("shared.txt", 50);
        DirectoryNode sub1 = new DirectoryNode("sub1");
        DirectoryNode sub2 = new DirectoryNode("sub2");
        sub1.addChild(sharedFile);
        assertThrows(IllegalStateException.class, () -> sub2.addChild(sharedFile));

        root.addChild(sub1);
        DirectoryNode sub1Child = new DirectoryNode("sub1Child");
        sub1.addChild(sub1Child);

        assertThrows(IllegalArgumentException.class, () -> root.addChild(root));
        assertThrows(IllegalArgumentException.class, () -> sub1Child.addChild(root));
        assertThrows(IllegalArgumentException.class, () -> sub1Child.addChild(sub1));
    }
}