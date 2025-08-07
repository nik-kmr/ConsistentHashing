package com.consistenthashing.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Node {
    private final String id;        // Unique identifier (e.g., IP:Port or DB name)
}
