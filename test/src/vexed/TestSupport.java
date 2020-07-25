package vexed;

import java.util.Collection;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;

class TestSupport {

    /**
     * Asserts that two collections have the same elements, ignoring order.
     */
    static void assertEqualContents(Collection<?> expected, Collection<?> provided) {
        assertEquals(expected.size(), provided.size());
        assertEquals(new HashSet<>(expected), new HashSet<>(provided));
    }
}
