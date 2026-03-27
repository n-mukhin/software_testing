import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DuplicateTest {

    @Test
    void testDuplicateInsertIsIgnored() {
        BTree tree = new BTree();

        tree.insert(10);
        tree.insert(20);
        tree.insert(10);
        tree.insert(20);
        tree.insert(30);

        assertEquals(List.of(10, 20, 30), tree.inOrder());
    }
}