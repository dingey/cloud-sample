package com.d.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisCache {
    /**
     * 缓存key前缀
     */
    @AliasFor("cacheNames")
    String value() default "";

    /**
     * 缓存key前缀
     */
    @AliasFor("value")
    String cacheName() default "";

    String key() default "";

    /**
     * 用于制作方法的Spring表达式语言（SpEL）表达式
     * 有条件地缓存。
     * <p>默认为{@code  }，表示方法结果始终被缓存。
     * <p> SpEL表达式针对提供以下内容的专用上下文进行评估：
     * 以下元数据：
     * <ul>
     * <li> {@code ＃root.method}，{@code ＃root.target}和{@code ＃root.caches}用于
     * 引用{@link java.lang.reflect.Method 方法}，目标对象和
     * 分别影响了缓存。</ li>
     * <li>方法名称（{@code ＃root.methodName}）和目标类的快捷方式
     * （{@code ＃root.targetClass}）也可用。
     * <li>方法参数可以通过索引访问。例如第二个参数
     * 可以通过{@code ＃root.args [1]}，{@code ＃p1}或{@code ＃a1}访问。争论
     * 也可以按名称访问该信息。</ li>
     * </ ul>
     */
    String condition() default "";

    /**
     * Spring表达式语言（SpEL）表达式用于否决方法缓存。
     * <p>与{@link #condition}不同，此表达式在方法之后求值
     * 已被调用，因此可以引用{@code result}。
     * <p>默认值为{@code ""}，这意味着永远不会否决缓存。
     * <p> SpEL表达式针对提供以下内容的专用上下文进行评估：
     * 以下元数据：
     * <ul>
     * <li> {@code #result}，以引用方法调用的结果。对于
     * 受支持的包装器，例如{@code Optional}，{@code #result}是指实际
     * 对象，而不是包装器</ li>
     * <li> {@code ＃root.method}，{@code ＃root.target}和{@code ＃root.caches}用于
     * 引用{@link java.lang.reflect.Method 方法}，目标对象和
     * 分别影响了缓存。</ li>
     * <li>方法名称（{@code ＃root.methodName}）和目标类的快捷方式
     * （{@code ＃root.targetClass}）也可用。
     * <li>方法参数可以通过索引访问。例如第二个参数
     * 可以通过{@code ＃root.args [1]}，{@code ＃p1}或{@code ＃a1}访问。争论
     * 也可以按名称访问该信息。</ li>
     * </ ul>
     */
    String unless() default "";

    /**
     * 是否异步
     * 如果有多个线程，则同步底层方法的调用
     * 尝试为同一键加载值。 同步导致
     * 有两个限制：
     * <ol>
     * 不支持<li> {@link #unless（）} </ li>
     * <li>只能指定一个缓存</ li>
     * <li>没有其他与缓存相关的操作可以组合</ li>
     * </ ol>
     * 这实际上是一个提示，您是实际的缓存提供者
     * 使用可能无法以同步方式支持它。 检查您的提供者
     * 有关实际语义的更多详细信息的文档。
     */
    boolean sync() default false;

    /**
     * 过期时间,单位 秒, 默认600秒
     */
    long expire() default 600;

    /**
     * 如果缓存不存在，是否缓存执行结果
     */
    boolean cacheResult() default true;

    /**
     * 存在熔断是否缓存结果
     */
    boolean cacheFallback() default false;
}
