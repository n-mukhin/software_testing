import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BreadcrumbsTest {

    @Test
    void inOrderShouldReturnSortedKeys() {
        BTree tree = new BTree();

        tree.insert(40);
        tree.insert(10);
        tree.insert(30);
        tree.insert(20);
        tree.insert(50);
        tree.insert(60);

        List<Integer> result = tree.inOrder();

        assertEquals(List.of(10, 20, 30, 40, 50, 60), result);
    }
}