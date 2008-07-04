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

import java.util.Locale;

public class SelectedSettings {
    private Locale userLocale = Locale.US;

    private boolean showAllMetrics = false;
    private boolean showFileResultsOverview = false;
    private boolean showPVMetadata = true;
    private boolean showPVVersions = true;
    private boolean showPVDevelopers = true;
    private boolean showPVFileStat = true;
    private boolean showPVMetrics = true;
    private boolean showDevelopers = true;

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

}
