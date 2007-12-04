/* This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 * 
 * Copyright 2007 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007 Georgios Gousios <gousiosg@gmail.com>
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

package eu.sqooss.impl.metrics.productivity;

public class FileTypeMatcher {

    public enum FileType {
        SRC, BIN, DOC, XML, TXT
    }

    private static String[] srcMimes = { ".c", ".java", ".h", ".py", "cpp",
            ".C", ".properties", ".po", ".sh", ".rb", ".el", ".m4", ".cs" };

    private static String[] docMimes = { ".txt", ".sgml", ".html", ".tex" };

    private static String[] xmlFormats = { ".xml", ".svn", ".argo", ".graffle",
            ".vcproj", ".csproj" };

    private static String[] binMimes = { ".pdf", ".png", ".jpg", ".tiff",
            ".dvi", ".gz", ".zip", ".properties", ".gif", ".exe", ".jar",
            ".doc", ".png", ".o", ".class", ".pyc" };

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

        return FileType.TXT;

    }

    public static FileType getFileTypeFromExt(String ext) {

        return null;

    }

}

//vi: ai nosi sw=4 ts=4 expandtab
