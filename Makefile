# This is a GNU Makefile, requiring GNU make 3.80 or later.
#
PREFIX=equinox
SUBDIRS=alitheia

ABS_PREFIX=$(shell cd $(PREFIX) && pwd)

all : build


define subdir_template
$(1)-$(2) :
	cd $(2) && $(MAKE) $(1) PREFIX=$(ABS_PREFIX)
endef

$(foreach d,$(SUBDIRS),$(eval $(call subdir_template,build,$(d))))
$(foreach d,$(SUBDIRS),$(eval $(call subdir_template,clean,$(d))))
$(foreach d,$(SUBDIRS),$(eval $(call subdir_template,install,$(d))))

build : $(foreach d,$(SUBDIRS),build-$(d))

install : $(foreach d,$(SUBDIRS),install-$(d))
	rm -Rf ${PREFIX}/configuration/org.eclipse.osgi
	rm -f ${PREFIX}/configuration/*.log

clean : $(foreach d,$(SUBDIRS),clean-$(d))
	rm -f $(PREFIX)/alitheia.log
	rm -rf $(PREFIX)/configuration/org.eclipse.osgi


# $(CONFIG) would typically be used to set system properties.
run :
	cd $(PREFIX) && \
	java $(CONFIG) -jar org.eclipse.osgi_3.3.0.v20070321.jar -console

show-log :
	cat $(PREFIX)/configuration/org.eclipse.osgi/bundles/[0-9]*/data/logs/alitheia*.log

