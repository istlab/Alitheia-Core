/* This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 * 
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007-2008 Georgios Gousios <gousiosg@gmail.com>
 * 
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

package eu.sqooss.service.fds;

public class FileTypeMatcher {

    private FileTypeMatcher instance;
    
    private FileTypeMatcher() { }
    
    public FileTypeMatcher getInstance() {
        if (instance == null) {
            instance = new FileTypeMatcher();
        }
        return instance;
    }
    
    public enum FileType {
        SRC, BIN, DOC, XML, TXT, TRANS
    }

    private static String[] srcMimes = { ".c", ".java", ".h", ".py", ".cpp",
            ".C", ".sh", ".rb", ".el", ".m4", ".cs", ".xsl", ".vb", ".patch", 
            "Makefile", ".hpp", ".pl", ".js", ".sql", ".css", ".jsp", ".bat", 
            ".php" };

    private static String[] docMimes = { ".txt", ".sgml", ".html", ".tex",
            ".htm", ".bib" };

    private static String[] xmlFormats = { ".xml", ".svn", ".argo", ".graffle",
            ".vcproj", ".csproj", ".rdf", ".wsdl", ".pom" };

    private static String[] binMimes = { ".pdf", ".png", ".jpg", ".tiff",
            ".dvi", ".gz", ".zip", ".gif", ".exe", ".jar", ".doc", ".png",
            ".o", ".class", ".pyc", ".bmp", ".ico", ".bz2", ".jpeg", ".war", 
            ".tif", ".ppt", ".xls", ".mp3", ".wmf", ".gif", ".dll", ".so" };

    private static String[] transMimes = { ".po" };

    private static final String locales = "ar_SA|zh_CN|zh_TW|nl_NL|en_AU|en_CA|" +
    		"en_GB|en_US|fr_CA|fr_FR|de_DE|iw_IL|hi_IN|it_IT|ja_JP|ko_KR|" +
    		"pt_BR|es_ES|sv_SE|th_TH|th_TH_TH|sq_AL|ar_DZ|ar_BH|ar_EG|" +
    		"ar_IQ|ar_JO|ar_KW|ar_LB|ar_LY|ar_MA|ar_OM|ar_QA|ar_SD|ar_SY|" +
    		"ar_TN|ar_AE|ar_YE|be_BY|bg_BG|ca_ES|zh_HK|hr_HR|cs_CZ|da_DK|" +
    		"nl_BE|en_IN|en_IE|en_NZ|en_ZA|et_EE|fi_FI|fr_BE|fr_LU|fr_CH|" +
    		"de_AT|de_LU|de_CH|el_GR|hu_HU|is_IS|it_CH|lv_LV|lt_LT|mk_MK|" +
    		"no_NO|no_NO_NY|pl_PL|pt_PT|ro_RO|ru_RU|sr_YU|sh_YU|sk_SK|sl_SI|" +
    		"es_AR|es_BO|es_CL|es_CO|es_CR|es_DO|es_EC|es_SV|es_GT|es_HN|" +
    		"es_MX|es_NI|es_PA|es_PY|es_PE|es_PR|es_UY|es_VE|tr_TR|uk_UA";

    public static FileType getFileType(String path) {
        for (String s : srcMimes)
            if (path.endsWith(s))
                return FileType.SRC;

        for (String s : docMimes)
            if (path.endsWith(s))
                return FileType.DOC;

        for (String s : xmlFormats)
            if (path.endsWith(s))
                return FileType.XML;

        for (String s : binMimes)
            if (path.endsWith(s))
                return FileType.BIN;

        for (String s : transMimes)
            if (path.endsWith(s))
                return FileType.TRANS;

        if (java.util.regex.Pattern.matches("(?i)^.*(" 
                + locales + ")\\.properties.*$", path)) {
            return FileType.TRANS;
        }

        return FileType.TXT;
    }

    public static FileType getFileTypeFromExt(String ext) {

        return null;

    }
}

// vi: ai nosi sw=4 ts=4 expandtab
