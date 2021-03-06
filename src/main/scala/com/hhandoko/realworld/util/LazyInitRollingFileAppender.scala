package com.hhandoko.realworld.util

import java.util.concurrent.atomic.AtomicBoolean

import ch.qos.logback.core.rolling.RollingFileAppender
import org.graalvm.nativeimage.ImageInfo

/**
 * RollingFileAppender with lazy initialization on GraalVM native image.
 *
 * Logback's rolling file appended does not work with Graal native image out of
 * the box. Reflection config has been added on `reflect-config.json` as well
 * to make this work.
 *
 * See:
 *   - https://github.com/oracle/graal/issues/1323
 *   - https://gist.github.com/begrossi/d807280f54d3378d407e9c9a95e5d905
 *
 * @tparam T Event object type parameter.
 */
class LazyInitRollingFileAppender[T] extends RollingFileAppender[T] {
  private[this] val started: AtomicBoolean = new AtomicBoolean(false)

  override def start(): Unit =
    if (!ImageInfo.inImageBuildtimeCode()) {
      super.start()
      this.started.set(true)
    }

  override def doAppend(eventObject: T): Unit =
    if (!ImageInfo.inImageBuildtimeCode()) {
      if (!this.started.get()) maybeStart()

      super.doAppend(eventObject)
    }

  /**
   * Synchronised method to avoid double start from `doAppender()`.
   */
  private[this] def maybeStart(): Unit = {
    lock.lock()

    try {
      if (!this.started.get()) this.start()
    } finally {
      lock.unlock()
    }
  }
}
