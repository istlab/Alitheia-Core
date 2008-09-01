/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2008 by Sebastian Kuegler <sebas@kde.org>
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *
 *     * Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *       with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package eu.sqooss.webui;

import java.io.File;
import java.util.Locale;

import eu.sqooss.webui.view.VerboseFileView;

public class SelectedSettings {
    // Common settings
    private Locale userLocale = Locale.US;
    private File tempFolder = null;

    private boolean showAllMetrics = false;
    private boolean showDevelopers = true;

    // ProjectView related
    private boolean showPVMetadata = true;
    private boolean showPVVersions = true;
    private boolean showPVDevelopers = true;
    private boolean showPVFileStat = true;
    private boolean showPVMetrics = true;
    private boolean showFileResultsOverview = false;

    // FileView related
    private boolean showFVDirBrowser = true;
    private boolean showFVFolderList = true;
    private boolean showFVFileList = true;

    // VerboseFileView related
    private boolean showVfvInfoPanel = true;
    private boolean showVfvControlPanel = true;
    private boolean showVfvResultPanel = true;
    private String[] vfvSelectedMetrics = null;
    private String[] vfvSelectedVersions = null;
    private int vfvChartType = VerboseFileView.TABLE_CHART;

    // VersionVerboseView related
    private boolean showVvvInfoPanel = true;
    private boolean showVvvControlPanel = true;
    private boolean showVvvResultPanel = true;
    private String[] vvvSelectedMetrics = null;
    private String[] vvvSelectedVersions = null;
    private Long vvvHighlightedVersion = null;
    private int vvvChartType = VerboseFileView.TABLE_CHART;
    private boolean vvvInputTaggedOnly = false;


    public Locale getUserLocale() {
        return userLocale;
    }

    public void setUserLocale(Locale userLocale) {
        this.userLocale = userLocale;
    }

    public void setShowAllMetrics(boolean flag) {
        showAllMetrics = flag;
    }

    public boolean getShowAllMetrics() {
        return showAllMetrics;
    }

    public void setShowFileResultsOverview(boolean flag) {
        showFileResultsOverview = flag;
    }

    public boolean getShowFileResultsOverview() {
        return showFileResultsOverview;
    }

    public void setShowPVDevelopers(boolean showPVDevelopers) {
        this.showPVDevelopers = showPVDevelopers;
    }

    public boolean getShowPVDevelopers() {
        return showPVDevelopers;
    }

    public void setShowPVFileStat(boolean showPVFileStat) {
        this.showPVFileStat = showPVFileStat;
    }

    public boolean getShowPVFileStat() {
        return showPVFileStat;
    }

    public void setShowPVMetrics(boolean showPVMetrics) {
        this.showPVMetrics = showPVMetrics;
    }

    public boolean getShowPVMetrics() {
        return showPVMetrics;
    }

    public void setShowDevelopers(boolean showDevelopers) {
        this.showDevelopers = showDevelopers;
    }

    public boolean getShowDevelopers() {
        return showDevelopers;
    }

    public void setShowPVMetadata(boolean showPVMetadata) {
        this.showPVMetadata = showPVMetadata;
    }

    public boolean getShowPVMetadata() {
        return showPVMetadata;
    }

    public void setShowPVVersions(boolean showPVVersions) {
        this.showPVVersions = showPVVersions;
    }

    public boolean getShowPVVersions() {
        return showPVVersions;
    }

    public void setShowFVDirBrowser(boolean showFVDirBrowser) {
        this.showFVDirBrowser = showFVDirBrowser;
    }

    public boolean getShowFVDirBrowser() {
        return showFVDirBrowser;
    }

    public void setShowFVFolderList(boolean showFVFolderList) {
        this.showFVFolderList = showFVFolderList;
    }

    public boolean getShowFVFolderList() {
        return showFVFolderList;
    }

    public void setShowFVFileList(boolean showFVFileList) {
        this.showFVFileList = showFVFileList;
    }

    public boolean getShowFVFileList() {
        return showFVFileList;
    }

    // =======================================================================
    // VerboseFileView related
    // =======================================================================

    public boolean getShowVfvInfoPanel() {
        return showVfvInfoPanel;
    }

    public void setShowVfvInfoPanel(boolean show) {
        this.showVfvInfoPanel = show;
    }

    public boolean getShowVfvControlPanel() {
        return showVfvControlPanel;
    }

    public void setShowVfvControlPanel(boolean show) {
        this.showVfvControlPanel = show;
    }

    public boolean getShowVfvResultPanel() {
        return showVfvResultPanel;
    }

    public void setShowVfvResultPanel(boolean show) {
        this.showVfvResultPanel = show;
    }

    public String[] getVfvSelectedMetrics() {
        return vfvSelectedMetrics;
    }

    public void setVfvSelectedMetrics(String[] metrics) {
        this.vfvSelectedMetrics = metrics;
    }

    public String[] getVfvSelectedVersions() {
        return vfvSelectedVersions;
    }

    public void setVfvSelectedVersions(String[] versions) {
        this.vfvSelectedVersions = versions;
    }

    public int getVfvChartType() {
        return vfvChartType;
    }

    public void setVfvChartType(int type) {
        this.vfvChartType = type;
    }

    // =======================================================================
    // VersionVerboseView related
    // =======================================================================

    public boolean getShowVvvInfoPanel() {
        return showVvvInfoPanel;
    }

    public void setShowVvvInfoPanel(boolean show) {
        this.showVvvInfoPanel = show;
    }

    public boolean getShowVvvControlPanel() {
        return showVvvControlPanel;
    }

    public void setShowVvvControlPanel(boolean show) {
        this.showVvvControlPanel = show;
    }

    public boolean getShowVvvResultPanel() {
        return showVvvResultPanel;
    }

    public void setShowVvvResultPanel(boolean show) {
        this.showVvvResultPanel = show;
    }

    public String[] getVvvSelectedMetrics() {
        return vvvSelectedMetrics;
    }

    public void setVvvSelectedMetrics(String[] metrics) {
        this.vvvSelectedMetrics = metrics;
    }

    public String[] getVvvSelectedVersions() {
        return vvvSelectedVersions;
    }

    public void setVvvSelectedVersions(String[] versions) {
        this.vvvSelectedVersions = versions;
    }

    public Long getVvvHighlightedVersion() {
        return vvvHighlightedVersion;
    }

    public void setVvvHighlightedVersion(Long vvvHighlightedVersion) {
        this.vvvHighlightedVersion = vvvHighlightedVersion;
    }

    public int getVvvChartType() {
        return vvvChartType;
    }

    public void setVvvChartType(int type) {
        this.vvvChartType = type;
    }

    public boolean getVvvInputTaggedOnly() {
        return vvvInputTaggedOnly;
    }

    public void setVvvInputTaggedOnly(boolean vvvInputTaggedOnly) {
        this.vvvInputTaggedOnly = vvvInputTaggedOnly;
    }

    // =======================================================================
    // Shared methods
    // =======================================================================

    public File getTempFolder() {
        return tempFolder;
    }

    public void setTempFolder(File tempFolder) {
        this.tempFolder = tempFolder;
    }

}
