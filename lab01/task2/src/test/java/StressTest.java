import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class StressTest {

    private BTree tree;

    @BeforeEach
    void setUp() {
        tree = new BTree();
    }

    @Test
    void testManyOperations() {

        List<Integer> bag = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            tree.insert(i);
            bag.add(i);
            assertStateEqualsBag(bag);
        }

        for (int i = 0; i < 100; i += 2) {
            tree.delete(i);
            bag.remove(Integer.valueOf(i));
            assertStateEqualsBag(bag);
        }

        for (int i = 1; i < 100; i += 2) {
            tree.delete(i);
            bag.remove(Integer.valueOf(i));
            assertStateEqualsBag(bag);
        }

        assertEquals(List.of(), tree.inOrder());
    }

    private void assertStateEqualsBag(List<Integer> bag) {

        List<Integer> expected = new ArrayList<>(bag);
        Collections.sort(expected);

        List<Integer> actual = tree.inOrder();
        assertEquals(expected, actual);

        if (!expected.isEmpty()) {
            int mid = expected.get(expected.size() / 2);
            assertTrue(tree.contains(mid));
        }
    }
}