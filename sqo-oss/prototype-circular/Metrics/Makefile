METCOMD=	Metrics.Common
METCOND=	Metrics.Console
METPLUD=	Metrics.Plugins
METPBTD=	Metrics.Plugins.BrokenTime
METRUID=	Metrics.UI
BINDIR=		bin
#Metrics.Plugins.BrokenTime.dll
all:Metrics.Common.dll Metrics.Console.exe Metrics.Plugins.dll Metrics.UI.exe
	@echo Done

clean:
	@echo Making clean
	@rm $(BINDIR)/*.*

Metrics.Common.dll: $(METCOMD)/CommitLog.cs $(METCOMD)/CommitLogEntry.cs \
		$(METCOMD)/CvsFileEntry.cs $(METCOMD)/CvsRepository.cs $(METCOMD)/Diff.cs \
		$(METCOMD)/FileEntry.cs $(METCOMD)/Repository.cs $(METCOMD)/Revision.cs \
		$(METCOMD)/SvnFileEntry.cs $(METCOMD)/SvnRepository.cs
	@echo Compiling $@
	@mkdir -p $(BINDIR)
	gmcs -t:library $^ -out:$(BINDIR)/$@

Metrics.Console.exe: $(METCOND)/Program.cs $(METCOND)/Properties/AssemblyInfo.cs
	@echo Compiling $@
	gmcs $^ -out:$(BINDIR)/$@ -r:$(BINDIR)/Metrics.Common.dll

Metrics.Plugins.dll: $(METPLUD)/IPlugin.cs $(METPLUD)/IPluginHost.cs \
		$(METPLUD)/MetricPluginAttribute.cs $(METPLUD)/PluginBase.cs \
		$(METPLUD)/PluginState.cs $(METPLUD)/Logging/FileLogger.cs \
		$(METPLUD)/Logging/ILogger.cs $(METPLUD)/Logging/LoggerBase.cs \
		$(METPLUD)/Logging/LoggerEntry.cs $(METPLUD)/Logging/LogLevel.cs \
		$(METPLUD)/Logging/LoggerEntryType.cs $(METPLUD)/Logging/QueueLogger.cs \
		$(METPLUD)/Properties/AssemblyInfo.cs
	@echo Compiling $@
	gmcs -t:library $^ -out:$(BINDIR)/$@ -r:$(BINDIR)/Metrics.Common.dll

Metrics.UI.exe: $(METRUID)/Form1.cs $(METRUID)/Form1.Designer.cs $(METRUID)/Program.cs \
		$(METRUID)/PluginController.cs $(METRUID)/Properties/AssemblyInfo.cs
	@echo Compiling $@
	gmcs $^ -out:$(BINDIR)/$@ -r:$(BINDIR)/Metrics.Common.dll \
		-r:$(BINDIR)/Metrics.Plugins.dll -r:System.Windows.Forms.dll \
		-r:System.Drawing.dll -r:System.Drawing.Design.dll -r:System.Data.dll 
