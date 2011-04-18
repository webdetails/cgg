#!/bin/sh

cgg_dir=`pwd`

dist="$cgg_dir/dist-package"
stub="$cgg_dir/emptyJar.jar"

echo "Building cgg distribution patch"
echo "using $cgg_dir as plugin origin"

# Clean  and recreate dir
rm -rf $dist
mkdir $dist

# validate the server location

echo "server location is $1"
if [ -e "$1/webapps/pentaho/WEB-INF/lib" ]
then
  echo "found server folder"
  server=$1/webapps/pentaho/WEB-INF/lib
else
  echo "couldn't find server folder. You need to pass it as argument of $0. Exiting";
  exit 1;
fi;

# Old libs will turn into empty jars
echo "Removing old batik libraries." 
pushd $server >> /dev/null
for lib in `find . -iname 'batik-*.jar'`
do
  echo Creating empty stub with name $lib
  cp $stub $dist/$lib
done

popd >> /dev/null


echo "Copying batik-1.7 to dist location"
for lib in dev-lib/batik-* dev-lib/xml* dev-lib/fop-*
do
  # we can't use batik's rhino!
  if  [[ $lib != *batik-js* ]]
  then
    echo "Copying $lib"
    cp $lib $dist
  fi
done
