package com.consistenthashing;

import com.consistenthashing.module.ConsistentHashingModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;

public class ConsistentHashing {
    public static void main(String[] args) {
        Injector injector;
        try {
            injector = Guice.createInjector(
                    Stage.PRODUCTION,
                    new ConsistentHashingModule()
            );
        } catch (Exception e) {
            System.exit(1);
            return;
        }
    }
}
