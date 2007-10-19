all : build

BUNDLES=logger \
	messaging \
	db \
	scheduler \
	tds \
	updater \
	webui

define subdir_template
$(1)-$(2) :
	cd $(2) && $(3)
endef

$(foreach d,$(BUNDLES),$(eval $(call subdir_template,build,$(d),make)))
$(foreach d,$(BUNDLES),$(eval $(call subdir_template,install,$(d),make install)))
$(foreach d,$(BUNDLES),$(eval $(call subdir_template,clean,$(d),make clean)))

build : $(foreach d,$(BUNDLES),build-$(d)) check

install : $(foreach d,$(BUNDLES),install-$(d))

clean : $(foreach d,$(BUNDLES),clean-$(d))
	rm -f `find . -name '*.class'`

check:
	@echo "# Unused subdirs:"
	@( for i in `find . -type d -maxdepth 1 | grep -v '^\.$$'` ; do \
		D=`basename $$i` ; echo $$D ; done ; \
	for i in $(BUNDLES) ; do echo $$i ; done ) | \
	sort | uniq -u

