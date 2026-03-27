import org.junit.jupiter.api.Test;
import utils.Cosine;

import static org.junit.jupiter.api.Assertions.*;

class EdgeCaseTest {

    @Test
    void shouldReturnNaNForNaNInput() {
        assertTrue(Double.isNaN(Cosine.cos(Double.NaN)));
    }

    @Test
    void shouldReturnNaNForInfiniteInput() {
        assertTrue(Double.isNaN(Cosine.cos(Double.POSITIVE_INFINITY)));
        assertTrue(Double.isNaN(Cosine.cos(Double.NEGATIVE_INFINITY)));
    }

    @Test
    void shouldThrowExceptionForInvalidN() {
        assertThrows(IllegalArgumentException.class, () -> Cosine.cos(1.0, 0));
        assertThrows(IllegalArgumentException.class, () -> Cosine.cos(1.0, -5));
    }
}