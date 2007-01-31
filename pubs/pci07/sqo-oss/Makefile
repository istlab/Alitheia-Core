## Makefile for PCI'07 publication
##
##

all:
	pdflatex pci07.tex
	bibtex pci07
	pdflatex pci07.tex
	pdflatex pci07.tex

clean:
	-rm -f *.bbl
	-rm -f *.aux
	-rm -f *.dvi
	-rm -f *.log
	-rm -f *.blg
	-rm -f *.backup
	-rm -f *.bbl
	-rm -f pci07.pdf
	-rm -f *~