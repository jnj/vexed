package vexed;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

class TestSupport {

    /**
     * Asserts that two collections have the same elements, ignoring order.
     */
    static void assertEqualContents(Collection<?> expected, Collection<?> provided) {
        Set<?> firstSet = new HashSet<>(expected);
        Set<?> secondSet = new HashSet<>(provided);
        assertEquals(firstSet, secondSet);
    }
}
