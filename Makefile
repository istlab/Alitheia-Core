all :
	@echo "# You need Maven2 installed and should run make for one of:"
	@echo "#     build"

build :
	( cd alitheia && mvn package )
	( cd metrics && mvn package )
