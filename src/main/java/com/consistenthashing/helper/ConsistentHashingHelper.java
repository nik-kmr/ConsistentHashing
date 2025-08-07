package com.consistenthashing.helper;

import com.consistenthashing.constants.Constants;
import com.consistenthashing.model.Node;
import com.consistenthashing.util.HashUtil;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.AllArgsConstructor;

import java.util.NavigableMap;

@Singleton
@AllArgsConstructor(onConstructor_ = {@Inject})
public class ConsistentHashingHelper {
    private final NavigableMap<Long, Node> ring;

    public Node getNodeForKey(final String key) {
        final long hash = HashUtil.getHash(key);
        // Try to find the closest node >= hash
        Long target = ring.ceilingKey(hash);
        if (target == null) {
            target = ring.firstKey();
        }
        return ring.get(target);
    }

    public void addNode(final Node node) {
        for (int i = 0; i < Constants.TOTAL_VIRTUAL_NODES; i++) {
            final String virtualId = node.getId() + "#" + i;
            final long virtualNodeHash = HashUtil.getHash(virtualId);
            if (ring.containsKey(virtualNodeHash)) {
                throw new IllegalStateException("Hash collision detected for key: " + node.getId() + "#" + i);
            }
            /** Do data migration here
             * For each virtual node being added:
             * 1. Find the **successor** and **predecessor** in ring
             * 2. Ask the successor to give all keys in this range (predecessorNodeHash, virtualNodeHash]
             * 3. For each of those keys:
             *    a. Re-run consistent hashing to determine the new owner
             *    b. If the new owner is the newly added virtual node, migrate the key from successor
             * 4. Insert the virtual node into the ring
             */
            ring.put(virtualNodeHash, node);
        }
    }

    public void removeNode(final Node node) {
        for (int i = 0; i < Constants.TOTAL_VIRTUAL_NODES; i++) {
            final String virtualId = node.getId() + "#" + i;
            final long virtualNodeHash = HashUtil.getHash(virtualId);
            /** Do data migration here
             * For each virtual node being removed:
             * 1. Find the **successor** and **predecessor** in ring
             * 2. Ask the current node (being deleted) to give all keys in the range (predecessorNodeHash, virtualNodeHash]
             * 3. For each of those keys:
             *    a. Re-run consistent hashing to determine the new owner
             *    b. If the new owner is not the current node (i.e., successor is now the owner), migrate the key
             * 4. Remove the virtual node from the ring
             */
            ring.remove(virtualNodeHash);
        }
    }
}
