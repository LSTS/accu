cd src
find . -name '*.java' -exec grep -l @AccuAction {}  \; > ../assets/plugin.list
