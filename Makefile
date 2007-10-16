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
	for i in $(SUBDIRS) ; do \
		for j in $$i/*/target/*.jar ; do \
			echo $$j ; \
		done ; \
	done

# $(CONFIG) would typically be used to set system properties.
run :
	cd $(PREFIX) && \
	java $(CONFIG) -jar org.eclipse.osgi_3.3.0.v20070321.jar -console

clean : $(foreach d,$(SUBDIRS),clean-$(d))
	rm -f $(PREFIX)/alitheia.log

show-log :
	cat $(PREFIX)/configuration/org.eclipse.osgi/bundles/[0-9]*/data/logs/alitheia*.log

