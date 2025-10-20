package com.compiler;

import org.junit.jupiter.api.Test;

public class TestMain {
    @Test
    public void testMain1() {
        String[] args = new String[1];
        args[0] = "src/main/resources/test1.mini";
        com.compiler.Main.main(args);
    }

    @Test
    public void testMain2() {
        String[] args = new String[1];
        args[0] = "src/main/resources/test2.mini";
        com.compiler.Main.main(args);
    }

    @Test
    public void testMainUndefined() {
        String[] args = new String[1];
        args[0] = "src/main/resources/testUndefined.mini";
        com.compiler.Main.main(args);
    }
}
