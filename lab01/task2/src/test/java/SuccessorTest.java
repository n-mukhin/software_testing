import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class SuccessorTest {

    @Test
    void testDeleteUsesSuccessor() {
        BTree tree = new BTree();

        int[] values = {20, 10, 30, 25, 40};
        for (int v : values) {
            tree.insert(v);
        }

        tree.delete(20);

        assertFalse(tree.contains(20));
        assertEquals(List.of(10, 25, 30, 40), tree.inOrder());
    }
}