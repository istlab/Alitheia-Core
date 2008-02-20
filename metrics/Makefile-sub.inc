# This is a GNU Makefile

# This Makefile is part of the Alitheia system produced by the SQO-OSS
# consortium and is covered by the same LICENSE as the rest of the system:
# the 2-Clause FreeBSD license which you may find in the LICENSE file.

# This Makefile builds OSGi bundles in a straightforward fashion. It
# has none of the sophistication of the Maven setup in that it doesn't
# check for updates, downloads nothing, ignores other installed files,
# and doesn't handle multiple concurrent versions of the build. Some people
# consider this a feature. To use it, *include* it from a subdirectory.
# Before including, you should set two variables:
#
# 	BUNDLE_NAME	The name of the bundle itself, probably derived
#			from the directory name or the classes in the bundle.
#	BUNDLE_VERSION	The version number of the bundle; this should match
#			what is in the manifest and is appended to the
#			name of the jar file.
#
# Targets build, install and clean are defined.

# Extra dependencies for the metrics bundle. These should point to the sources
# of other bundles. Everyone depends on the core. Metrics depend on OSGi. Metrics
# also depend on codecs.
EXTRA_CP:=$(EXTRA_CP)../../alitheia/core/src:
# EXTRA_CP+=:$(PREFIX)/org.eclipse.osgi_3.3.0.v20070321.jar:$(PREFIX)/org.eclipse.osgi.util_3.1.100.v20060918.jar:$(PREFIX)/org.eclipse.osgi.services_3.1.100.v20060918.jar
# EXTRA_CP+=:$(PREFIX)/org.apache.commons.codec_1.3.0.jar

# Optionally, the manifest name may be different from the bundle name.
# This is a historical mistake, but not easy to correct.
BUNDLE_MANIFEST_NAME?=$(BUNDLE_NAME)

# Namespace to prefix to jar names.
NAMESPACE=eu.sqooss.metrics

#
# END OF USER CONFIGURATION
#
###

include ../../Makefile-sub.common
