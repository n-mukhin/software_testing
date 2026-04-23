import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SearchTest {

    private BTree tree;

    @BeforeEach
    void setUp() {
        tree = new BTree();
    }

    @Test
    void testContains() {
        tree.insert(10);
        tree.insert(20);
        tree.insert(30);

        assertTrue(tree.contains(20));
        assertFalse(tree.contains(40));
    }
}