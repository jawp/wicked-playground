#!/usr/bin/env bash

#quick start workbench scalajs

echo "refresh browser"
echo "http://localhost:12345/modules/workbenchScalajs/target/scala-2.11/classes/index-dev.html"
./activator "project workbenchScalajs" ~workbenchScalajs/fastOptJS
