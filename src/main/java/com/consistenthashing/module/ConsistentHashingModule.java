package com.consistenthashing.module;
import com.consistenthashing.constants.Constants;
import com.consistenthashing.model.Node;
import com.consistenthashing.util.HashUtil;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

public class ConsistentHashingModule extends AbstractModule {

    @Provides
    @Singleton
    NavigableMap<Long, Node> provideHashRing() {
        final NavigableMap<Long, Node> ring = new TreeMap<>();
        final int virtualNodes = Constants.TOTAL_VIRTUAL_NODES;
        final List<Node> initialNodes = Constants.NODES.stream().map(this::buildNode).toList();

        for (final Node node : initialNodes) {
            for (int i = 0; i < virtualNodes; i++) {
                long hash = HashUtil.getHash(node.getId() + "#" + i);
                if (ring.containsKey(hash)) {
                    throw new IllegalStateException("Hash collision detected for key: " + node.getId() + "#" + i);
                }
                ring.put(hash, node);
            }
        }

        return ring;
    }

    private Node buildNode(final String identifier) {
        return Node.builder()
                .id(identifier)
                .build();
    }
}
