package com.hhandoko.realworld

package object core {

  object lang {

    object pipe {

      implicit class PipeSyntax[T](val t: T) extends AnyVal {

        private[core] def pipe[U](fn: T => U): U = fn(t)
        def |>[U](fn: T => U): U = fn(t)

      }

    }

  }

}
