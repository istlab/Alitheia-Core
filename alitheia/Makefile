# This is a GNU Makefile

# This Makefile is part of the Alitheia system produced by the SQO-OSS
# consortium and is covered by the same LICENSE as the rest of the system:
# the 2-Clause FreeBSD license which you may find in the LICENSE file.

# This is an intermediate Makefile in the Alitheia build system. It just
# passes targets on to the sub-directories. You may invoke a Maven build
# by setting WITH_MAVEN to a non-empty value.

# Subdirectories to build bundles in. Ignored in a Maven build (which takes
# the same information from pom.xml).
BUNDLES=db \
	fds \
	logger \
	messaging \
	scheduler \
	security \
	tds \
	updater \
	webadmin

#
# END OF USER CONFIGURABLE AREA
#
###

TOP_SRCDIR?=$(shell cd .. && pwd)
PREFIX?=$(shell cd ../equinox && pwd)

all : build

# Template to pass targets on to sub-directories.
define subdir_template
$(1)-$(2) :
	cd $(2) && $(MAKE) $(3) TOP_SRCDIR=$(TOP_SRCDIR) PREFIX=$(PREFIX)
endef

$(foreach d,$(BUNDLES),$(eval $(call subdir_template,build,$(d),)))
$(foreach d,$(BUNDLES),$(eval $(call subdir_template,install,$(d),install)))
$(foreach d,$(BUNDLES),$(eval $(call subdir_template,clean,$(d),clean)))
$(foreach d,$(BUNDLES),$(eval $(call subdir_template,depend,$(d),depend)))

# Maven short circuits the build by compiling from here.
ifeq ($(WITH_MAVEN),)
build : $(foreach d,$(BUNDLES),build-$(d)) check

else
build :
	mvn package install
endif

install : $(foreach d,$(BUNDLES),install-$(d))

clean : $(foreach d,$(BUNDLES),clean-$(d))
	rm -f `find . -name '*.class'`

depend : $(foreach d,$(BUNDLES),depend-$(d))

# This is a double check that all of the subdirectories listed in 
# BUNDLES exist and that all of the subdirectories that exist are
# listed in BUNDLES. This helps avoid embarrassing mismatches between
# the Maven, Makefile and filesystem setups.
check:
	@echo "# Unused subdirs:"
	@( for i in `find . -maxdepth 1 -type d | grep -v '^\.$$' | grep -v '^./.svn'` ; do \
		D=`basename $$i` ; echo $$D ; done ; \
	for i in $(BUNDLES) ; do echo $$i ; done ) | \
	sort | uniq -u

