TOP_SRCDIR=..
include $(TOP_SRCDIR)/Makefile.config

FILES= $(wildcard src/*)
BUILDDIR=build

INPUT=${BUILDDIR}/documentation.xml
PDFOUTPUT=documentation.pdf
HTMLOUTPUT=documentation.html

.PHONY: all pdf html clean

all: prepare pdf html

prepare:
	-mkdir -p build
	-cp ${FILES} ${BUILDDIR}

html: ${HTMLOUTPUT}

pdf: ${PDFOUTPUT}

${HTMLOUTPUT}: ${INPUT} prepare 
	xmllint --xinclude --postvalid $< | xsltproc -o $@ ${XSLT_DOCBOOK}/html/docbook.xsl -

${PDFOUTPUT} : ${INPUT} ${FILES}
	xmllint --xinclude --postvalid $< | xsltproc -o tmp.fo ${XSLT_DOCBOOK}/fo/docbook.xsl - &&\
	fop tmp.fo $@ &&\
	rm tmp.fo

clean:
	rm -fR ${BUILDDIR}
	rm -f ${HTMLOUTPUT} ${PDFOUTPUT} 
