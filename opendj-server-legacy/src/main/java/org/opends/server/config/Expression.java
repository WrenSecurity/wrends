/*
 * The contents of this file are subject to the terms of the Common Development and
 * Distribution License (the License). You may not use this file except in compliance with the
 * License.
 *
 * You can obtain a copy of the License at legal/CDDLv1.0.txt. See the License for the
 * specific language governing permission and limitations under the License.
 *
 * When distributing Covered Software, include this CDDL Header Notice in each file and include
 * the License file at legal/CDDLv1.0.txt. If applicable, add the following below the CDDL
 * Header, with the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions Copyright [year] [name of copyright owner]".
 *
 * Copyright 2016 ForgeRock AS.
 * Portions Copyright 2026 Wren Security
 */
package org.opends.server.config;

import jakarta.el.ELContext;
import jakarta.el.ELException;
import jakarta.el.ELManager;
import jakarta.el.ExpressionFactory;
import jakarta.el.StandardELContext;
import jakarta.el.ValueExpression;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.forgerock.util.Reject;
import org.glassfish.expressly.ExpressionFactoryImpl;

/**
 * A Unified Expression Language read-only expression. Creating an expression is the equivalent to
 * compiling it. Once created, an expression can be evaluated with an optional set of bindings. Expressions are
 * thread safe.
 *
 * @param <T>
 *         expected result type
 */
final class Expression<T> {
    /** Factory for compiling expressions. */
    private static final ExpressionFactory FACTORY = new ExpressionFactoryImpl();
    /** Context used when compiling expressions and evaluating expressions that don't have bindings. */
    private static final ELContext EL_CONTEXT = getELContext0(null);

    /**
     * Compiles the provided expression and evaluates it without any bindings.
     *
     * @param <T>
     *         Expected result type
     * @param expression
     *         The expression to compile.
     * @param expectedType
     *         The expected result type of the expression.
     * @return The result of the expression evaluation.
     * @throws IllegalArgumentException
     *         If the expression could not be compiled, evaluated, or the returned value did not have the expected
     *         type.
     */
    public static <T> T eval(final String expression, final Class<T> expectedType) {
        return compile(expression, expectedType).eval();
    }

    /**
     * Compiles the provided expression and evaluates it using the provided bindings.
     *
     * @param <T>
     *         Expected result type
     * @param expression
     *         The expression to compile.
     * @param expectedType
     *         The expected result type of the expression.
     * @param bindings
     *         The bindings, which may be empty or {@code null}.
     * @return The result of the expression evaluation.
     * @throws IllegalArgumentException
     *         If the expression could not be compiled, evaluated, or the returned value did not have the expected
     *         type.
     */
    public static <T> T eval(final String expression, final Class<T> expectedType, final Map<String, Object> bindings) {
        return compile(expression, expectedType).eval(bindings);
    }

    /** The expected type of this expression. */
    private final Class<T> expectedType;
    /** The compiled EL expression. */
    private final ValueExpression valueExpression;

    /**
     * Compiles the provided expression string.
     *
     * @param <T>
     *         Expected result type
     * @param expression
     *         The expression to compile.
     * @param expectedType
     *         The expected result type of the expression.
     * @return The compiled expression.
     * @throws IllegalArgumentException
     *         If the expression was not syntactically correct or contained unrecognized functions.
     */
    public static <T> Expression<T> compile(final String expression, final Class<T> expectedType) {
        Reject.ifNull(expression, "expression must not be null");
        Reject.ifNull(expectedType, "expectedType must not be null");
        try {
            final ValueExpression valueExpression = FACTORY.createValueExpression(EL_CONTEXT, expression, expectedType);
            return new Expression<>(expectedType, valueExpression);
        } catch (final ELException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private Expression(final Class<T> expectedType, final ValueExpression valueExpression) {
        this.expectedType = expectedType;
        this.valueExpression = valueExpression;
    }

    /**
     * Evaluates this expression without any bindings.
     *
     * @return The result of the expression evaluation.
     * @throws IllegalArgumentException
     *         If the expression could not be evaluated or the returned value did not have the expected type.
     */
    public T eval() {
        return eval(Collections.<String, Object>emptyMap());
    }

    /**
     * Evaluates this expression using the provided bindings.
     *
     * @param bindings
     *         The bindings, which may be empty or {@code null}.
     * @return The result of the expression evaluation.
     * @throws IllegalArgumentException
     *         If the expression could not be evaluated or the returned value did not have the expected type.
     */
    public T eval(final Map<String, Object> bindings) {
        try {
            return expectedType.cast(valueExpression.getValue(getELContext(bindings)));
        } catch (final ELException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public String toString() {
        return valueExpression.getExpressionString();
    }

    private static ELContext getELContext(final Map<String, Object> bindings) {
        if (bindings == null || bindings.isEmpty()) {
            return EL_CONTEXT;
        }
        return getELContext0(bindings);
    }

    private static ELContext getELContext0(final Map<String, Object> bindings) {
        ELManager manager = new ELManager();
        manager.setELContext(new StandardELContext(FACTORY));

        manager.defineBean("env", Collections.unmodifiableMap(System.getenv()));
        manager.defineBean("system", Collections.unmodifiableMap(System.getProperties()));

        if (bindings != null) {
            for (Map.Entry<String, Object> entry : bindings.entrySet()) {
                manager.defineBean(entry.getKey(), entry.getValue());
            }
        }

        for (Map.Entry<String, Method> function : getPublicStaticMethods(Functions.class).entrySet()) {
            manager.mapFunction("", function.getKey(), function.getValue());
        }

        return manager.getELContext();
    }

    private static Map<String, Method> getPublicStaticMethods(final Class<?> target) {
        final Map<String, Method> methods = new HashMap<>();
        for (final Method method : target.getMethods()) {
            if (Modifier.isStatic(method.getModifiers())) {
                method.setAccessible(true);
                methods.put(method.getName(), method);
            }
        }
        return methods;
    }
}
