package vexed;

import java.util.Collection;
import java.util.Set;

import junit.framework.Assert;

public class TestSupport {

    /**
     * Asserts that two collections have the same elements, ignoring order.
     */
    public static void assertEqualContents(Collection<?> expected, Collection<?> provided) {
        Set<Object> firstSet = Containers.newHashSet(expected);
        Set<Object> secondSet = Containers.newHashSet(provided);
        Assert.assertEquals(firstSet, secondSet);
    }
}
