# This is a GNU Makefile, requiring GNU make 3.80 or later.
#

all : build

SUBDIRS=alitheia metrics

define subdir_template
$(1)-$(2) :
	cd $(2) && $(MAKE) $(1)
endef

$(foreach d,$(SUBDIRS),$(eval $(call subdir_template,build,$(d))))
$(foreach d,$(SUBDIRS),$(eval $(call subdir_template,clean,$(d))))

build : $(foreach d,$(SUBDIRS),build-$(d))

PREFIX=equinox

install :
	rm -Rf ${PREFIX}/configuration/org.eclipse.osgi
	rm -f ${PREFIX}/configuration/*.log
	T="" ; \
	for i in $(SUBDIRS) ; do \
		for j in $$i/*/target/*.jar ; do \
			if test -f $$j ; then \
				T="$$T ,"`basename $$j`"@start" ; \
				cp $$j $(PREFIX) ; \
			fi ; \
		done ; \
	done ; \
	sed "s/@@ALITHEIA@@/$$T/" < $(PREFIX)/configuration/config.ini.in > $(PREFIX)/configuration/config.ini

run :
	cd $(PREFIX) && \
	java -jar org.eclipse.osgi_3.3.0.v20070321.jar -console

clean : $(foreach d,$(SUBDIRS),clean-$(d))
	
