/*
 * Copyright 2015-2025 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 *
 * https://www.eclipse.org/legal/epl-v20.html
 */

package org.junit.platform.commons.support;

import static org.apiguardian.api.API.Status.MAINTAINED;

import java.util.function.Function;

import org.apiguardian.api.API;
import org.junit.platform.commons.util.ClassUtils;

/**
 * {@code ClassSupport} provides static utility methods for common tasks
 * regarding {@linkplain Class classes} &mdash; for example, generating a
 * comma-separated list of fully qualified class names for a set of supplied
 * classes.
 *
 * <p>{@link org.junit.platform.engine.TestEngine TestEngine} and extension
 * authors are encouraged to use these supported methods in order to align with
 * the behavior of the JUnit Platform.
 *
 * @since 1.1
 * @see AnnotationSupport
 * @see ModifierSupport
 * @see ReflectionSupport
 */
@API(status = MAINTAINED, since = "1.1")
public final class ClassSupport {

	private ClassSupport() {
		/* no-op */
	}

	/**
	 * Generate a comma-separated list of fully qualified class names for the
	 * supplied classes.
	 *
	 * @param classes the classes whose names should be included in the
	 * generated string
	 * @return a comma-separated list of fully qualified class names, or an empty
	 * string if the supplied class array is {@code null} or empty
	 * @see #nullSafeToString(Function, Class...)
	 */
	public static String nullSafeToString(Class<?>... classes) {
		return ClassUtils.nullSafeToString(classes);
	}

	/**
	 * Generate a comma-separated list of mapped values for the supplied classes.
	 *
	 * <p>The values are generated by the supplied {@code mapper}
	 * (e.g., {@code Class::getName}, {@code Class::getSimpleName}, etc.), unless
	 * a class reference is {@code null} in which case it will be mapped to
	 * {@code "null"}.
	 *
	 * @param mapper the mapper to use; never {@code null}
	 * @param classes the classes to map
	 * @return a comma-separated list of mapped values, or an empty string if
	 * the supplied class array is {@code null} or empty
	 * @see #nullSafeToString(Class...)
	 */
	public static String nullSafeToString(Function<? super Class<?>, ? extends String> mapper, Class<?>... classes) {
		return ClassUtils.nullSafeToString(mapper, classes);
	}

}
