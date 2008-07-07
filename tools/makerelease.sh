#! /bin/sh
#
# This script creates an Alitheia release by tarring up sources,
# building them, creating a binary distro and calling it a day.
# Everything is done in /tmp.
#
# This script is released under a 2-clause BSD license, just like
# the rest of Alitheia. See LICENSE for details.
#
# Copyright (C) 2008 by Adriaan de Groot

VERSION=0.8.1

main()
{
	echo "### Alitheia MakeRelease started" `date`
	echo "#"
	create_workdir
	check_required_files
	cleanup_cruft
	create_source_tarball
	build_tarball
	create_binary_tarball
	echo "#"
	echo "### Alitheia MakeRelease finished" `date`
}

create_workdir()
{
	TMPDIR=/tmp/alitheia-$$
	mkdir $TMPDIR || { echo "! Could not create temporary directory." ; exit 1 ; }
	chmod 700 $TMPDIR
	rm -rf $TMPDIR/*
}

check_required_files()
{
	echo "# Checking for required files ..."
	test -f tools/makerelease.sh || { echo "! You must run this script from the trunk/ directory." ; exit 1 ; }
	for i in \
		LICENSE \
		README.txt \
		equinox/configuration/config.ini \
		Makefile.config ; do
		test -f $i || { echo "! Missing '$i'" ; exit 1 ; }
	done
}

cleanup_cruft()
{
	echo "# Cleaning up source tree ..."
	rm -rf `svn st --no-ignore | awk '/^[?I]/{print $2}`
	svn revert --recursive `svn st --no-ignore | awk '/^[!CM]/{print $2}'`
}

create_source_tarball()
{
	echo "# Creating source tarball ..."
	mkdir $TMPDIR/alitheia-$VERSION
	$TAR cf - --exclude .svn . | $TAR xf - -C $TMPDIR/alitheia-$VERSION
	cd $TMPDIR
	$TAR czf alitheia-$VERSION.tar.gz alitheia-$VERSION || { echo "! Could not create source tarball." ; exit 1 ; }
	rm -rf alitheia-$VERSION
}

build_tarball()
{
	echo "# Building Alitheia ..."
	cd $TMPDIR
	$TAR xzf alitheia-$VERSION.tar.gz || { echo "! Could not unpack source tarball." ; exit 1 ; }
	cd alitheia-$VERSION || { echo "! Could not enter sources." ; exit 1 ; }
	check_required_files
	$MAKE clean build install > ../build.log 2>&1 || { echo "! Build failed. See $TMPDIR/build.log." ; exit 1 ; }
}

create_binary_tarball()
{
	echo "# Creating binary distribution tarball ..."
	cd $TMPDIR
	$TAR czf alitheia-bin-$VERSION.tar.gz \
		alitheia-$VERSION/equinox \
		alitheia-$VERSION/tomcat \
		alitheia-$VERSION/LICENSE* \
		alitheia-$VERSION/README* \
		alitheia-$VERSION/Makefile.run || \
		{ echo "! Failed to create binary distribution." ; exit 1 ; }
}

test -n "$1" && VERSION="$1"

TAR=tar
test SunOS = `uname` && TAR=/usr/sfw/bin/gtar
MAKE=make
test SunOS = `uname` && MAKE=/usr/bin/gmake

main

rm -rf $TMPDIR/alitheia-$VERSION

test -f $TMPDIR/alitheia-$VERSION.tar.gz || { echo "! Missing source tarball." ; exit 1 ; }
test -f $TMPDIR/alitheia-bin-$VERSION.tar.gz || { echo "! Missing binary tarball." ; exit 1 ; }

echo "" ; echo "Distribute the following two files:"
echo "    $TMPDIR/alitheia-$VERSION.tar.gz     (source)"
echo "    $TMPDIR/alitheia-bin-$VERSION.tar.gz (binary)"

