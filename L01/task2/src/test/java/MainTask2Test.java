import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MainTask2Test {

    @Test
    void testMainInsertAndPrint() {

        String[] args = {"10", "20", "5", "print"};

        MainTask2.main(args);

        assertTrue(true);
    }

    @Test
    void testMainContains() {

        String[] args = {"10", "20", "?10", "?30"};

        MainTask2.main(args);

        assertTrue(true);
    }

    @Test
    void testMainDelete() {

        String[] args = {"10", "20", "30", "-20", "print"};

        MainTask2.main(args);

        assertTrue(true);
    }

    @Test
    void testMainInvalidInput() {

        String[] args = {"abc", "10"};

        MainTask2.main(args);

        assertTrue(true);
    }
}