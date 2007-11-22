#! /bin/sh
#
# This script builds an Alitheia system twice: once with make,
# and once with Maven. Then it compares the resulting builds.
# This allows us to check that the build systems are in-sync.
#
# Author:    Adriaan de Groot <groot@kde.org>
# Copyright: SQO-OSS Consortium members, <http://www.sqo-oss.eu/>
# License:   2-clause BSD

CLEAN_REPO=sqo
MVN_REPO=build-maven
MAKE_REPO=build-make

echo "# Updating SVN ..          " `date`
svn up $CLEAN_REPO > /dev/null 2>&1
echo "# Cleaning up ..           " `date`
rm -rf $MVN_REPO/*
rm -rf $MAKE_REPO/*
rm -f sqo.tar files.make files.mvn
echo "# Creating build trees ..  " `date`
( cd $CLEAN_REPO/devel/trunk && tar cf - . ) > sqo.tar
( cd $MVN_REPO && tar xf ../sqo.tar )
( cd $MAKE_REPO && tar xf ../sqo.tar )
echo "# Preparing tools ..       " `date`
( cd $MAKE_REPO && gmake install-deps ) > /dev/null 2>&1
( cd $MVN_REPO && gmake install-deps ) > /dev/null 2>&1
echo "# Building with make ..    " `date`
( cd $MAKE_REPO && gmake build install ) > /dev/null 2>&1
echo "# Building with Maven ..   " `date`
( cd $MVN_REPO && gmake build install WITH_MAVEN=YES ) > /dev/null 2>&1
echo "# Comparing ..             " `date`
( cd $MAKE_REPO/equinox && find . ) | sort > files.make
( cd $MVN_REPO/equinox && find . ) | sort > files.mvn
diff -u files.make files.mvn
echo "# Done.                    " `date`

