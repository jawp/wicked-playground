#!/usr/bin/env bash

#quick start workbench scalajs

echo "refresh browser"
echo "http://localhost:12345/modules/frontend/target/scala-2.11/classes/index-dev.html"
./activator "project frontend" ~frontend/fastOptJS
