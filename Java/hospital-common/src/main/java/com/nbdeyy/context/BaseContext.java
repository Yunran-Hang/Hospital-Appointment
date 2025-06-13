package com.nbdeyy.context;

public class BaseContext {

    public static class Context {
        public Long userId;
        public String username;
    }

    private static final ThreadLocal<Context> threadLocal = new ThreadLocal<>();

    // 获取或创建Context对象
    private static Context getOrCreateContext() {
        Context context = threadLocal.get();
        if (context == null) {
            context = new Context();
            threadLocal.set(context);
        }
        return context;
    }

    public static void setCurrentId(Long id) {
        getOrCreateContext().userId = id;
    }

    public static Long getCurrentId() {
        Context context = threadLocal.get();
        return context != null ? context.userId : null;
    }

    public static void setUsername(String username) {
        getOrCreateContext().username = username;
    }

    public static String getUsername() {
        Context context = threadLocal.get();
        return context != null ? context.username : null;
    }

    public static void clear() {
        threadLocal.remove();
    }
}
