import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InsertTest {

    private BTree tree;

    @BeforeEach
    void setUp() {
        tree = new BTree();
    }

    @Test
    void testInsertAndInOrder() {
        tree.insert(10);
        tree.insert(20);
        tree.insert(30);
        tree.insert(15);

        List<Integer> keys = tree.inOrder();
        assertEquals(List.of(10, 15, 20, 30), keys);
    }
}