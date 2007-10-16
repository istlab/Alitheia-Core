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
			START_LEVEL="" ; \
			INIT_STATE="start"; \
			MODULE_PATH=`echo $$j | sed 's/\/target\/.*.jar//'` ; \
			if test -f $$MODULE_PATH/startlevel.cfg ; then \
				START_LEVEL=`cat $$MODULE_PATH/startlevel.cfg | awk -F ':' '{print $$1}'`":" ; \
				INIT_STATE=`cat $$MODULE_PATH/startlevel.cfg | awk -F ':' '{print $$2}'`":" ; \
			fi ; \
			if test -f $$j ; then \
				T="$$T, "`basename $$j`"@$$START_LEVEL$$INIT_STATE" ; \
				cp $$j $(PREFIX) ; \
			fi ; \
		done ; \
	done ; \
	sed "s/@@ALITHEIA@@/$$T/" < $(PREFIX)/configuration/config.ini.in > $(PREFIX)/configuration/config.ini

# $(CONFIG) would typically be used to set system properties.
run :
	cd $(PREFIX) && \
	java $(CONFIG) -jar org.eclipse.osgi_3.3.0.v20070321.jar -console

clean : $(foreach d,$(SUBDIRS),clean-$(d))
	rm -f $(PREFIX)/alitheia.log

show-log :
	cat $(PREFIX)/configuration/org.eclipse.osgi/bundles/[0-9]*/data/logs/alitheia*.log

