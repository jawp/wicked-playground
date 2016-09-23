#!/bin/sh

for f in `find . -type f| grep -v '\\.git\/' | grep '\\.sh$'`; do echo "gitchmoding: $f" && git update-index --chmod=+x $f; done

customFiles=" \
"

for f in $customFiles; do echo "gitchmoding: $f" && git update-index --chmod=+x $f; done

echo "Done" 
echo "Now run 'git ls-files -s' and investigate filemodes. '*.sh' file should have 755 and other should have 644."