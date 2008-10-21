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

import eu.sqooss.webui.settings.BaseDataSettings;

public class SelectedSettings {
    // Shared settings
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

    // FileDataView specific
    private Long fdvSelectedFileId = null;

    // VersionDataView specific
    private boolean vdvInputTaggedOnly = false;

    // TimelineView specific
    private Long tvDateFrom;
    private Long tvDateTill;

    public static final int FILE_DATA_SETTINGS      = 11;
    public static final int VERSION_DATA_SETTINGS   = 12;
    public static final int DEVELOPER_DATA_SETTINGS = 13;
    public static final int TIMELINE_DATA_SETTINGS  = 14;

    public BaseDataSettings fileDataView        = null;
    public BaseDataSettings versionDataView     = null;
    public BaseDataSettings developerDataView   = null;
    public BaseDataSettings timelineDataView    = null;

    public SelectedSettings() {
        super();
        developerDataView = new BaseDataSettings();
        versionDataView = new BaseDataSettings();
        fileDataView = new BaseDataSettings();
        timelineDataView = new BaseDataSettings();
    }

    public BaseDataSettings getDataSettings(int target) {
        switch (target) {
        case DEVELOPER_DATA_SETTINGS:
            return developerDataView;
        case VERSION_DATA_SETTINGS:
            return versionDataView;
        case FILE_DATA_SETTINGS:
            return fileDataView;
        case TIMELINE_DATA_SETTINGS:
            return timelineDataView;
        default:
            return null;
        }
    }

    public void flushDataSettings(int target) {
        switch (target) {
        case DEVELOPER_DATA_SETTINGS:
            developerDataView = new BaseDataSettings(); break;
        case VERSION_DATA_SETTINGS:
            versionDataView = new BaseDataSettings(); break;
        case FILE_DATA_SETTINGS:
            fileDataView = new BaseDataSettings(); break;
        case TIMELINE_DATA_SETTINGS:
            timelineDataView = new BaseDataSettings(); break;
        }
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
    // FileDataView related
    // =======================================================================

    public Long getFdvSelectedFileId() {
        return fdvSelectedFileId;
    }

    public void setFdvSelectedFileId(Long fdvSelectedFileId) {
        this.fdvSelectedFileId = fdvSelectedFileId;
    }

    // =======================================================================
    // VersionDataView related
    // =======================================================================

    public boolean getVdvInputTaggedOnly() {
        return vdvInputTaggedOnly;
    }

    public void setVdvInputTaggedOnly(boolean enable) {
        vdvInputTaggedOnly = enable;
    }

    // =======================================================================
    // TimelineView related
    // =======================================================================

    public Long getTvDateFrom() {
        return tvDateFrom;
    }

    public void setTvDateFrom(Long date) {
        tvDateFrom = date;
    }

    public Long getTvDateTill() {
        return tvDateTill;
    }

    public void setTvDateTill(Long date) {
        tvDateTill = date;
    }

    // =======================================================================
    // Shared methods
    // =======================================================================

    public Locale getUserLocale() {
        return userLocale;
    }

    public void setUserLocale(Locale userLocale) {
        this.userLocale = userLocale;
    }

    public File getTempFolder() {
        return tempFolder;
    }

    public void setTempFolder(File path) {
        tempFolder = path;
    }

}
