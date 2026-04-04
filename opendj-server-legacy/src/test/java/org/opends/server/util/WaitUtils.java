/*
 * The contents of this file are subject to the terms of the Common Development and
 * Distribution License (the License). You may not use this file except in compliance with the
 * License.
 *
 * You can obtain a copy of the License at legal/CDDLv1.1.txt. See the License for the
 * specific language governing permission and limitations under the License.
 *
 * When distributing Covered Software, include this CDDL Header Notice in each file and include
 * the License file at legal/CDDLv1.1.txt. If applicable, add the following below the CDDL
 * Header, with the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions copyright [year] [name of copyright owner]".
 *
 * Copyright 2026 Wren Security
 */
package org.opends.server.util;

import static org.assertj.core.api.Assertions.fail;

import java.time.Duration;

/**
 * Convenience asynchronous process wait utility methods (inspired by Awaitility).
 */
public class WaitUtils {

  private static Duration DEFAULT_MAX_WAIT = Duration.ofSeconds(10);

  private static Duration DEFAULT_POLL_WAIT = Duration.ofMillis(100);

  /**
   * Wait for condition to evaluate to true.
   * @param condition Condition to evaluate. Never null.
   */
  public static void waitForCondition(WaitCondition condition) throws Exception
  {
    long startTime = System.currentTimeMillis();
    while (true)
    {
      if (condition.test())
      {
        break;
      }
      if (System.currentTimeMillis() - startTime > DEFAULT_MAX_WAIT.toMillis())
      {

      }
      Thread.sleep(DEFAULT_POLL_WAIT.toMillis());
    }
  }

  /**
   * Wait for assertion to pass.
   * @param assertion Assertion to evaluate. Never null.
   */
  public static void waitForAssertion(WaitAssertion assertion) throws Exception {
    long startTime = System.currentTimeMillis();
    while (true)
    {
      try
      {
        assertion.test();
        break;
      } catch (AssertionError e)
      {
        if (System.currentTimeMillis() - startTime > DEFAULT_MAX_WAIT.toMillis())
        {
          fail("Waiting for a condition timed-out", e);
        }
      }
      Thread.sleep(DEFAULT_POLL_WAIT.toMillis());
    }
  }

  /** Waiting for condition to return true. */
  @FunctionalInterface
  public interface WaitCondition
  {
    /** Evaluate wait condition. */
    boolean test() throws Exception;
  }

  /** Waiting for test assertions to pass. */
  @FunctionalInterface
  public interface WaitAssertion
  {
    /** Evaluate test assertions. */
    void test() throws Exception;
  }

}
