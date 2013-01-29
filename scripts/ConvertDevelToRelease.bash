#!/bin/sh
# This is a cygwin bash script. Run using cygwin.
# Change to the project/scripts directory and run using 
# ./ConvertDevelToRelease.bash
# Before doing this, it may be necessary to run d2u ./ConvertDevelToRelease.bash to get rid of dos endlines. 

# export PATH=/usr/bin;$PATH

DEVEL_DIR='uscteensver1'
DEVEL='uscteensver1'

RELEASE_DIR='uscteensrel1'
RELEASE='uscteensrel1'

NAME='USCTeens'

cd ../../

echo "Converting from USCTeens development version to release version."
echo "Convert ${DEVEL} to ${RELEASE}"

echo "Copy the development directory to a release directory"

cp -r ${DEVEL_DIR} ${RELEASE_DIR}

cd $RELEASE_DIR

rm -rf `find . -type d -name .svn`
rm -rf `find . -type d -name .git`

echo "Cleaning project in ${RELEASE_DIR}"

find . -name "*.settings" -type d -exec rm -r '{}' \;
find . -name "bin" -type d -exec rm -r '{}' \;
find . -name "gen" -type d -exec rm -r '{}' \;
find . -name "*~" -type f -exec rm -r '{}' \;
find . -name ".svn" -type d -exec rm -r '{}' \;
find . -name ".git" -type d -exec rm -r '{}' \;

export LANG=en-US.UTF-8

echo "Converting XMLs..."

find . -name "*.xml" -type f 

find . -name "*.xml" -type f -exec sed -i "s/\.neu\.android\.mhealth\.${DEVEL}/\.neu\.android\.mhealth\.${RELEASE}/g" '{}' \;

echo "Converting java..."

find . -name "*.java" -type f 

find . -name "*.java" -type f -exec sed -i "s/\.neu\.android\.mhealth\.${DEVEL}/\.neu\.android\.mhealth\.${RELEASE}/g" '{}' \;

# Change the src directory names

echo "Moving directories that have to be renamed ..."

find . -name "${DEVEL}" -type d | while read LINE ; do
DIR=$(dirname "$LINE")
FILE=$(basename "$LINE")
echo ${DIR}/${FILE} to ${DIR}/${RELEASE}
mv "${DIR}/${FILE}" "${DIR}/${RELEASE}"
done

# Need to add code to change the website locations 

# Need to add code to change the datastore if needed



