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


main()
{
	echo "### Alitheia MakeRelease started" `date`
	echo "#"
	check_required_files
	cleanup_cruft
	create_source_tarball
	build_tarball
	create_binary_tarball
	echo "#"
	echo "### Alitheia MakeRelease finished" `date`
}

check_required_files()
{
	echo "# Checking for required files ..."
	for i in tools/makerelease.sh \
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
}

create_source_tarball()
{
	echo "# Creating source tarball ..."
}

build_tarball()
{
	echo "# Building Alitheia ..."
}

create_binary_tarball()
{
	echo "# Creating binary distribution tarball ..."
}

main

