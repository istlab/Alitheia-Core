# This is the default bundle Makefile. If you don't do anything
# special with the bundle, just sources under src/ and the usual,
# then this should be enough: you get targets 'build' and 'install'
# and 'clean' which do the right things.

TOP_SRCDIR=../..

include $(TOP_SRCDIR)/Makefile.bundle

#
# The remainder is special for skeleton and is used as a clone-
# wizard to create new plug-ins.
#
clone : check-arguments create-tree copy-files

check-arguments :
	@test -n "$(TARGET)" || { echo "! You must set TARGET to the name of the new plug-in." ; echo "!     For example, 'make clone TARGET=mycounter'." ; exit 1 ; }
	@echo "$(TARGET)" | grep -v '[^a-zA-Z0-9_]' > /dev/null 2>&1 || { echo "! Your TARGET setting ($(TARGET)) includes bad characters." ; echo "!     Please use only a-zA-Z0-9_." ; exit 1 ; }
	@mkdir ../$(TARGET) || { echo "! Failed to create the target directory." ; exit 1 ; }
	@test -d ../$(TARGET) || { echo "! The target directory ($(TARGET)) could not be created." ; exit 1 ; }
	@touch ../$(TARGET)/Makefile || { echo "! The target directory ($(TARGET)) can not be written." ; exit 1 ; }

create-tree :
	mkdir -p ../$(TARGET)/src/eu/sqooss/metrics/$(TARGET)
	mkdir -p ../$(TARGET)/src/eu/sqooss/impl/metrics/$(TARGET)
	mkdir -p ../$(TARGET)/manifest

copy-files :
	@target=`perl -e 'print lcfirst("$(TARGET)");'` ;  \
	TARGET=`perl -e 'print ucfirst("$(TARGET)");'` ;  \
	for i in manifest/manifest.mf \
		src/eu/sqooss/metrics/skeleton/Skeleton.java \
		src/eu/sqooss/metrics/skeleton/SkeletonActivator.java ; \
	do \
		T=`echo "$$i" | sed -e "s/skeleton/$$target/g" -e "s/Skeleton/$$TARGET/g"` ; \
		mkdir -p "../$(TARGET)/"`dirname "$$T"` ; \
		echo `basename "$$i"` "->" `basename "$$T"` ; \
		sed -e "s/skeleton/$$target/g" -e "s/Skeleton/$$TARGET/g" < "$$i" > "../$(TARGET)/$$T" ; \
	done
	@echo "Makefile -> Makefile" ; head Makefile | grep -v '^#' > ../$(TARGET)/Makefile



