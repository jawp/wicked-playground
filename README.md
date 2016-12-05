
# Wicked Playground
... is a project for learning purposes.
Here I test some snippets from various tutorials, howtos and documentations trying to understand what is going on

[![Build Status](https://travis-ci.org/jawp/wicked-playground.svg?branch=master)](https://travis-ci.org/jawp/wicked-playground)

# Run and play

Run:

    ./sbt.sh '~reRstart'

... and open a browser at http://localhost:8080/

#Projects

## server
Akka http server based app.

## frontend
Simple project for working with scalajs. 

Most every change in code should autoamtically restart server and reload web page.

## shared
code which is shared between all other projects.

## clapi
Stub for command line app/library which can talk to server.

## sparky
Examples related to spark.

-----------
Learning resources:
- [Functional Programming in Scala, by Paul Chiusano and Rúnar Bjarnason](https://www.manning.com/books/functional-programming-in-scala) (book)
- [David Sankel: Monoids, Monads, and Applicative Functors: Repeated Software Patterns](https://www.youtube.com/watch?v=DiisKQAkGM4)
- [Why the free Monad isn't free - by Kelley Robinson](https://www.youtube.com/watch?v=U0lK0hnbc4U)
- [Move Over Free Monads: Make Way for Free Applicatives! by John de Goes](https://www.youtube.com/watch?v=H28QqxO7Ihc), slides [here](https://github.com/jdegoes/scalaworld-2015/blob/master/presentation.md)
- [Composable application architecture with reasonably priced monads - by Rúnar Bjarnason](https://www.youtube.com/watch?v=M258zVn4m2M) (about free monads)
