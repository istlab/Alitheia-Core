# This Makefile is part of the Alitheia system produced by the SQO-OSS
# consortium and is covered by the same LICENSE as the rest of the system:
# the 2-Clause FreeBSD license which you may find in the LICENSE file
# in the devel/trunk/ directory of the Alitheia development repository.

# This is the Makefile for the SQO-OSS repository.
# It is used to mangle the pristine third-party
# JAR files (such as org.apache.commons.codec.jar)
# into bundles which are under eu.sqooss.extlibs.
# This needs to be done just once (to update the
# repository).
#
# For a development environment in a given directory 
# you must run the following command once.
#
# make install TOP_SRCDIR=/path/to/trunk
#
# (If you are working in trunk/ and not in a branch,
# you may leave out the TOP_SRCDIR, as the default is
# .., which *is* the path to trunk/)
#
# This will install all the external libraries. If you
# use 'make clean-all' in the development environment
# or otherwise damage the third-party libraries in 
# there, run make install again.

TOP_SRCDIR?=..
include $(TOP_SRCDIR)/Makefile.config

all : build

# We replace the standard clean with a message, because re-jarring 
# is a slow and expensive operation. Use clean-all to get rid of
# everything which was installed into the eu/sqooss/extlibs group
# and force a rebuild.
clean :
	@echo "# Use 'make clean-all' to remove the re-jarred packages."

# This throws out all the re-jarred bundles.
clean-all :
	rm -rf eu/sqooss/extlibs/*

# This macro does the major magic of re-jarring an existing jar
# into a bundle with a new manifest and (optionally) extra content.
# It is called with four arguments:
#
# $1 group
#	This is the groupId of the jar as it is installed in the
#	repo. The separator is / not . so it looks like a directory.
# $2 artifact
#	This is the artifactId of the jar. 
# $3 version
#	The version of the jar. You should be able to find the
#	jar itself at $1/$2/$3/$2-$3.jar in the repo.
# $4 commands
#	These are extra commands that are run while re-jarring.
#	You can use these to add extra files to the jar.
#
# Normally you call this macro with only the first three parameters.
# This will re-jar the library into a bundle. The new bundle manifest
# is taken from $1.$2.mf with the / replaced by . in the group name.
#
define rejar_template
REJARS+=$(1)$(2)

build-$(1)$(2) : eu/sqooss/extlibs/$(subst /,.,$(1)).$(2)/$(3)/$(subst /,.,$(1)).$(2)-$(3).jar

clean-$(1)$(2) :
	rm -f eu/sqooss/extlibs/$(subst /,.,$(1)).$(2)/$(3)/$(subst /,.,$(1)).$(2)-$(3).jar

install-$(1)$(2) : build-$(1)$(2)
	cp eu/sqooss/extlibs/$(subst /,.,$(1)).$(2)/$(3)/$(subst /,.,$(1)).$(2)-$(3).jar $(PREFIX)

.PHONY : build-$(1)$(2) clean-$(1)$(2) install-$(1)$(2)

eu/sqooss/extlibs/$(subst /,.,$(1)).$(2)/$(3)/$(subst /,.,$(1)).$(2)-$(3).jar :
	@rm -rf tmp
	@mkdir tmp
	@echo "# Re-jarring $(2) ..."
	@cp $(1)/$(2)/$(3)/$(2)-$(3).jar tmp/$(subst /,.,$(1)).$(2)-$(3).jar
	@rm -f tmp/META-INF/MANIFEST
	@$(4)
	@( cd tmp && jar cvfm new.jar ../$(subst /,.,$(1)).$(2).mf * $$(QUIET) )
	@echo "# Installing $(2) to SQO-OSS repository ..."
	$(MAVEN) install:install-file \
		-DgroupId=eu.sqooss.extlibs \
		-DcreateChecksum=true \
		-DgeneratePom=true \
		-DlocalRepositoryId=sqooss \
		-DlocalRepositoryPath=. \
		-DartifactId=$(subst /,.,$(1)).$(2) \
		-Dversion=$(3) \
		-Dpackaging=jar \
		-Dfile=tmp/new.jar $$(QUIET)
	@rm -rf tmp
endef


define install_template
INSTALLS+=$(1)$(2)
install-$(1)$(2) :
	cp $(1)/$(2)/$(3)/$(2)-$(3).jar $(PREFIX)/$(subst /,.,$(1)).$(2)-$(3).jar

.PHONY : install-$(1)$(2)

endef

	
$(eval $(call rejar_template,javax,mail,1.4,cp javax/activation/1.1/activation-1.1.jar tmp && mkdir -p tmp/META-INF && cp javax.mail.resources/* tmp/META-INF ))
$(eval $(call rejar_template,javax,servlet,2.4.0.v200612120446))
$(eval $(call rejar_template,javax/stax,api,1.0.1))
$(eval $(call rejar_template,org/apache/axiom,api,1.2.2,mkdir -p tmp/META-INF/services && cp org.apache.axiom.api.resources/* tmp/META-INF/services))
$(eval $(call rejar_template,org/apache/axiom,dom,1.2.2))
$(eval $(call rejar_template,org/apache/axiom,impl,1.2.2))
$(eval $(call rejar_template,org/apache/axis2,databinding,1.1.0))
$(eval $(call rejar_template,org/apache/axis2,kernel,1.1.0,cp edu/emory/mathcs/concurrent/2.2/concurrent-2.2.jar tmp/ && mkdir -p tmp/META-INF/services && cp org.apache.axis2.resources/* tmp/META-INF/services))
$(eval $(call rejar_template,org/apache/commons,codec,1.3.0))
$(eval $(call rejar_template,org/apache/commons,collections,3.2))
$(eval $(call rejar_template,org/apache/commons,httpclient,3.0.1))
$(eval $(call rejar_template,org/apache/commons,logging,1.1.1))
$(eval $(call rejar_template,org/apache/ws/commons,schema,1.2.0))
$(eval $(call rejar_template,org/apache,derby,10.3.2.1,cp org/apache/derby/10.3.2.1/derbyLocale*.jar tmp))
$(eval $(call rejar_template,org/apache,log4j,1.2.15))
$(eval $(call rejar_template,org/apache,neethi,2.0.0))
$(eval $(call rejar_template,org/apache,velocity,1.5,$(MAKE) extra-velocity))
$(eval $(call rejar_template,org/apache,wsdl4j,1.6.1))
$(eval $(call rejar_template,org/codehaus,stax2,3.0.1))
$(eval $(call rejar_template,org,dom4j,1.6.1))
$(eval $(call rejar_template,org,hibernate,3.2.5,$(MAKE) extra-hibernate))
$(eval $(call rejar_template,org,junit,4.4))
$(eval $(call rejar_template,org/postgresql,jdbc,8.2.505))
$(eval $(call rejar_template,org/tmatesoft,svnkit,1.1.4))

extra-hibernate :
	cp javax/transaction/1.1/transaction-1.1.jar tmp/javax.transaction-1.1.jar
	cp net/sf/cglib/2.1.3/cglib-2.1.3.jar tmp/net.sf.cglib-2.1.3.jar
	cp org/objectweb/asm/1.5.3/asm-1.5.3.jar tmp/org.objectweb.asm-1.5.3.jar
	cp org/objectweb/asm/attrs/1.5.3/attrs-1.5.3.jar tmp/org.objectweb.asm.attrs-1.5.3.jar
	cp org/antlr/2.7.6/antlr-2.7.6.jar tmp/

extra-velocity :
	cp org/apache/commons/collections/3.2/collections-3.2.jar tmp/
	cp org/apache/commons/lang/2.4/lang-2.4.jar tmp/
	cp org/apache/jakarta/oro/2.0.8/oro-2.0.8.jar tmp/


$(eval $(call install_template,javax/saaj,api,1.3.0))
$(eval $(call install_template,org/apache/muse,complete,2.2.0))
$(eval $(call install_template,org/apache/muse/osgi/soa,axis2,1.0.0))
$(eval $(call install_template,org/apache/ws,java2wsdl,1.1.0))
$(eval $(call install_template,org/apache,xalan,2.7.0))
$(eval $(call install_template,org/apache,xerces,2.8.0.M20070504-incubation))
$(eval $(call install_template,org/apache/xml,resolver,1.1.0.M20070504-incubation))
$(eval $(call install_template,org/eclipse/corona/soa,common,0.9.0.M20070504-incubation))
$(eval $(call install_template,org/eclipse/corona/soa/common,axis2,0.9.0.M20070504-incubation))
$(eval $(call install_template,org/eclipse/corona/soa/provider,axis2,0.9.0.M20070504-incubation))
$(eval $(call install_template,org/eclipse/corona/soa/provider/axis2,util,0.9.0.M20070504-incubation))
$(eval $(call install_template,org/eclipse/equinox,ds,1.0.0.v20070226))
$(eval $(call install_template,org/eclipse/equinox/http,jetty,1.0.0.v20070318))
$(eval $(call install_template,org/eclipse/equinox/http,servlet,1.0.0.v20070318))
$(eval $(call install_template,org/eclipse/equinox,log,1.0.100.v20070226))
$(eval $(call install_template,org/eclipse/equinox,osgi,3.3.0))
$(eval $(call install_template,org/eclipse/osgi,services,3.1.100.v20060918))
$(eval $(call install_template,org/eclipse/osgi,util,3.1.100.v20060918))
$(eval $(call install_template,org/mortbay,jetty,5.1.11))
$(eval $(call install_template,org/w3c,dom,1.3.3.M20070504-incubation))

build : $(foreach j,$(REJARS),build-$(j))

install : $(foreach j,$(REJARS),install-$(j)) $(foreach j,$(INSTALLS),install-$(j))

.PHONY : all clean clean-all build install

build-dir :

help :
	@echo $(MAVEN) install:install-file \
		-DcreateChecksum=true \
		-DgeneratePom=true \
		-Dpackaging=jar \
		-DlocalRepositoryId=sqooss \
		-DlocalRepositoryPath=. \
		-DgroupId=id \
		-DartifactId=id \
		-Dversion=ver \
		-Dfile=file

.PHONY : help
