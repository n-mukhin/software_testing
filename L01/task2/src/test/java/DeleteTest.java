import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DeleteTest {

    private BTree tree;

    @BeforeEach
    void setUp() {
        tree = new BTree();
    }

    @Test
    void testDeleteLeafKey() {
        tree.insert(10);
        tree.insert(20);
        tree.insert(30);
        tree.insert(15);

        tree.delete(15);

        assertFalse(tree.contains(15));
        assertEquals(List.of(10, 20, 30), tree.inOrder());
    }

    @Test
    void testDeleteInternalKey() {
        int[] values = {10, 20, 5, 6, 12, 30, 7, 17};

        for (int v : values) tree.insert(v);

        tree.delete(10);

        assertFalse(tree.contains(10));

        List<Integer> expected = new ArrayList<>();
        for (int v : values) if (v != 10) expected.add(v);
        Collections.sort(expected);

        assertEquals(expected, tree.inOrder());
    }

    @Test
    void testDeleteRootSingle() {
        tree.insert(10);
        tree.delete(10);

        assertFalse(tree.contains(10));
        assertEquals(List.of(), tree.inOrder());
        assertNotNull(tree.getRoot());
    }

    @Test
    void testDeleteNotFound() {
        tree.insert(10);
        tree.insert(20);

        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class,
                        () -> tree.delete(30));

        assertEquals("Key not found", ex.getMessage());
    }
}