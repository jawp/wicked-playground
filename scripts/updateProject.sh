#!/usr/bin/env bash



#on checkIn (commit) text EOLs in files will be converted to LF
git config --local core.eol lf

#after checkout files will have their original EOL
git config --local core.autocrlf false

