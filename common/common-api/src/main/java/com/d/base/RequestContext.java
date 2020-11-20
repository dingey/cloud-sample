package com.d.base;

import java.util.Objects;

public final class RequestContext {
    private static final ThreadLocal<Boolean> fallback = new InheritableThreadLocal<>();

    public static void removeFallback() {
        fallback.remove();
    }

    public static void setFallback() {
        fallback.set(true);
    }

    public static boolean hasFallback() {
        return Objects.equals(fallback.get(), true);
    }

    public static boolean noFallback() {
        return Objects.equals(fallback.get(), false);
    }
}
