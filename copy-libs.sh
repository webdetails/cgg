#!/bin/sh

cgg_dir=`pwd`
echo "using $cgg_dir as plugin origin"

# validate the server location

echo "server location is $1"
if [ -e "$1/webapps/pentaho/WEB-INF/lib" ]
then
  echo "found server folder"
  server=$1/webapps/pentaho/WEB-INF/lib
else
  echo "couldn't find server folder. Exiting";
  exit 1;
fi;

# remove old libs
echo "Removing old batik libraries." 
pushd $server >> /dev/null
for lib in `find . -iname 'batik-*.jar'`
do
  echo "Deleting $lib"
  rm -f $lib
done
popd >> /dev/null
echo "done.\n"
echo "Copying batik-1.7 to server location"
for lib in dev-lib/batik-* dev-lib/xml* dev-lib/fop-*
do
  # we can't use batik's rhino!
  if  [[ $lib != *batik-js* ]]
  then
    echo "Copying $lib"
    cp $lib $server
  fi
done
