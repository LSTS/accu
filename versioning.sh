#/bin/bash

echo "Auto Version: `pwd`"

CODE=`git tag | grep -c ^v[0-9]`
NAME=`git describe --dirty | sed -e 's/^v//'`
COMMITS=`echo ${NAME} | sed -e 's/[0-9\.]*//'`

DATE=`date +"%d-%m-%Y"`


if [ "x${COMMITS}x" = "xx" ] ; then
    VERSION="${NAME}"
else
    BRANCH=" (`git branch | grep "^\*" | sed -e 's/^..//'`)"
    VERSION="${NAME}${BRANCH}"
fi

echo "   Code: ${CODE}"
echo "   Ver:  ${VERSION}"

cat ./AndroidManifest.xml | \
    sed -e "s/android:versionCode=\"[0-9][0-9]*\"/android:versionCode=\"${CODE}\"/" \
        -e "s/android:versionName=\".*\"/android:versionName=\"${VERSION}\"/" \
    > ./bin/AndroidManifest.xml

cat ./src/pt/lsts/accu/AboutPanel.java | \
	sed -e "s/String versionString = \".*\";/String versionString = \"${VERSION}\";/" \
		-e "s/String dateString = \".*\";/String dateString = \"${DATE}\";/" \
	> ./bin/AboutPanel.java

rm AndroidManifest.xml
cp bin/AndroidManifest.xml AndroidManifest.xml
rm bin/AndroidManifest.xml

rm ./src/pt/lsts/accu/AboutPanel.java
cp ./bin/AboutPanel.java ./src/pt/lsts/accu/AboutPanel.java
rm ./bin/AboutPanel.java

exit 0