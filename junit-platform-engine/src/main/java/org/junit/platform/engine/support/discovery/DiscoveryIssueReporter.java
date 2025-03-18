/*
 * Copyright 2015-2025 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 *
 * https://www.eclipse.org/legal/epl-v20.html
 */

package org.junit.platform.engine.support.discovery;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import org.apiguardian.api.API;
import org.junit.platform.commons.util.Preconditions;
import org.junit.platform.engine.DiscoveryIssue;
import org.junit.platform.engine.EngineDiscoveryListener;
import org.junit.platform.engine.UniqueId;

/**
 * {@code DiscoveryIssueReporter} defines the API for reporting
 * {@link DiscoveryIssue DiscoveryIssues}.
 *
 * @since 1.13
 * @see SelectorResolver.Context
 */
@API(status = EXPERIMENTAL, since = "1.13")
@FunctionalInterface
public interface DiscoveryIssueReporter {

	/**
	 * Create a new {@code DiscoveryIssueReporter} that reports issues to the
	 * supplied {@link EngineDiscoveryListener} for the specified engine.
	 *
	 * @param engineDiscoveryListener the listener to report issues to; never
	 * {@code null}
	 * @param engineId the unique identifier of the engine; never {@code null}
	 */
	static DiscoveryIssueReporter create(EngineDiscoveryListener engineDiscoveryListener, UniqueId engineId) {
		Preconditions.notNull(engineDiscoveryListener, "engineDiscoveryListener must not be null");
		Preconditions.notNull(engineId, "engineId must not be null");
		return issue -> engineDiscoveryListener.issueEncountered(engineId, issue);
	}

	/**
	 * Build the supplied {@link DiscoveryIssue.Builder Builder} and report the
	 * resulting {@link DiscoveryIssue}.
	 */
	default void reportIssue(DiscoveryIssue.Builder builder) {
		reportIssue(builder.build());
	}

	/**
	 * Report the supplied {@link DiscoveryIssue}.
	 */
	void reportIssue(DiscoveryIssue issue);

	/**
	 * Create a {@link Condition} that reports a {@link DiscoveryIssue} when the
	 * supplied {@link Predicate} is not met.
	 *
	 * @param predicate the predicate to test; never {@code null}
	 * @param issueCreator the function to create the issue with; never {@code null}
	 * @return a new {@code Condition}; never {@code null}
	 */
	default <T> Condition<T> createReportingCondition(Predicate<T> predicate,
			Function<T, DiscoveryIssue> issueCreator) {
		Preconditions.notNull(predicate, "predicate must not be null");
		Preconditions.notNull(issueCreator, "issueCreator must not be null");
		return value -> {
			if (predicate.test(value)) {
				return true;
			}
			else {
				reportIssue(issueCreator.apply(value));
				return false;
			}
		};
	}

	/**
	 * A {@code Condition} is a union of {@link Predicate} and {@link Consumer}.
	 *
	 * <p>Instances of this type may be used as {@link Predicate Predicates} or
	 * {@link Consumer Consumers}. For example, a {@code Condition} may be
	 * passed to {@link java.util.stream.Stream#filter(Predicate)} if it is used
	 * for filtering, or to {@link java.util.stream.Stream#peek(Consumer)} if it
	 * is only used for reporting or other side effects.
	 *
	 * @see #createReportingCondition(Predicate, Function)
	 */
	@FunctionalInterface
	interface Condition<T> extends Predicate<T>, Consumer<T> {

		/**
		 * Return a composed condition that represents a logical AND of the
		 * supplied conditions without short-circuiting.
		 *
		 * <p><em>All</em> of the supplied conditions will be evaluated even if
		 * one or more of them return {@code false} to ensure that all issues
		 * are reported.
		 *
		 * @param conditions the conditions to compose; never {@code null}, not
		 * empty, and must not contain any {@code null} elements
		 * @return the composed condition; never {@code null}
		 */
		@SafeVarargs
		@SuppressWarnings("varargs")
		static <T> Condition<T> allOf(Condition<? super T>... conditions) {
			Preconditions.notNull(conditions, "conditions must not be null");
			Preconditions.notEmpty(conditions, "conditions must not be empty");
			Preconditions.containsNoNullElements(conditions, "conditions must not contain null elements");
			return value -> {
				boolean result = true;
				for (Condition<? super T> condition : conditions) {
					result &= condition.test(value);
				}
				return result;
			};
		}

		/**
		 * Evaluate the {@code #test(Object)} method of this condition to
		 * potentially report an issue.
		 */
		@Override
		default void accept(T value) {
			test(value);
		}
	}
}
