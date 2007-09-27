all : build

BUNDLES=logger \
	messaging \
	updater \
	webui

define subdir_template
$(1)-$(2) :
	cd $(2) && $(3)
endef

$(foreach d,$(BUNDLES),$(eval $(call subdir_template,build,$(d),mvn package)))
$(foreach d,$(BUNDLES),$(eval $(call subdir_template,install,$(d),cp target/*.jar ../../equinox/)))

build :
	mvn package

install :
	for i in $(BUNDLES) ; do \
		cp $$i/target/*.jar ../equinox/ ; \
	done

clean :
	for i in $(BUNDLES) ; do \
		for j in $$i/target/*.jar ; do \
			rm -f $(PREFIX)/`basename $$j` ; \
		done ; \
		rm -f $$i/target/*.jar ; \
	done

