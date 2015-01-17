/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2008 - 2010 - Organization for Free and Open Source Software,  
 *                Athens, Greece.
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

import java.lang.StringBuffer;
import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * A simple, file extension based, file type matcher class. It uses a set of
 * statically loaded look-up tables and string comparisons to do its job.
 */
public final class FileTypeMatcher {

	private static FileTypeMatcher instance;
	private static HashMap<String, FileType> lookupTable = new HashMap<String, FileType>();

	private static Pattern doc;
	private static Pattern locale;
	
	private FileTypeMatcher() {
		StringBuffer pattern = new StringBuffer();
		pattern.append(".*\\.(");
		for(String doc : docMimes) {
			pattern.append(doc).append("|");
		}
		pattern.deleteCharAt(pattern.length() - 1);
		pattern.append(")$");
		
		doc = Pattern.compile(pattern.toString());
		locale = Pattern.compile(locales);
	}

	public static FileTypeMatcher getInstance() {
		if (instance == null) {
			instance = new FileTypeMatcher();
		}
		return instance;
	}

	/**
	 * File types enumeration. These are the distinguished types of files that
	 * Alitheia works with. The list is clearly not exhaustive nor complete;
	 * some file types could easily be put in one or the other category (e.g.
	 * XML docbook) and some obvious file types are missing.
	 */
	public enum FileType {
		/**
		 * Source code files
		 */
		SRC,
		/**
		 * Binary files
		 */
		BIN,
		/**
		 * Documentation files
		 */
		DOC,
		/**
		 * XML file formats
		 */
		XML,
		/**
		 * Raw text files. Includes all non-binary files.
		 */
		TXT,
		/**
		 * Translation files
		 */
		TRANS
	}

	private static String[] srcMimes = { ".C", ".CBL", ".COB", ".F", ".S",
			".ad", ".ada", ".adb", ".ads", ".am", ".asm", ".asp", ".aspx",
			".atf", ".autoforms", ".awk", ".bas", ".bat", ".c", ".c++", ".cbl",
			".cc", ".ccg", ".cgi", ".cls", ".cob", ".cpp", ".cpy", ".cs",
			".csh", ".css", ".cxx", ".d", ".diff", ".dlg", ".dsp", ".dtd",
			".e", ".ec", ".ecp", ".el", ".exp", ".f", ".f77", ".fd", ".for",
			".gnorba", ".h", ".hg", ".hh", ".hpp", ".hs", ".hxx",
			".i", ".i3", ".idl", ".ids", ".inc", ".itk", ".java", ".jl", ".js",
			".jsp", ".l", ".lex", ".ll", ".lsp", ".m", ".m3", ".m4", ".ml",
			".ml3", ".p", ".pad", ".pas", ".patch", ".pc", ".pcc", ".perl",
			".php", ".php3", ".php4", ".pl", ".plot", ".plugin", ".pm", ".pod",
			".ppd", ".pri", ".pro", ".py", ".rb", ".s", ".schema", ".scm",
			".sed", ".sh", ".sql", ".tcl", ".tk", ".trm", ".upd", ".vb",
			".vbs", ".vim", ".xs", ".xsl", ".y", ".yy", ".scala"};

	private static String[] docMimes = { "readme.*", "changelog.*", "todo.*",
			"credits.*", "authors.*", "changes.*", "news.*", "install.*",
			"hacking.*", "copyright.*", "licen[sc]e.*", "copying.*",
			"manifest", "faq", "building", "howto", "design", "�les",
			"subdirs", "maintainers", "developers", "contributors", "thanks",
			"testing", "build", "comments?", "bugs", "buglist", "problems",
			"debug", "hacks", "hacking", "versions?", "mappings", "tips",
			"ideas?", "spec", "compiling", "notes", "missing", "done", "omf",
			"lsm", "directory", "dox", "html", "txt", "lyx", "tex",
			"tex", "sgml", "docbook", "xhtml", "phtml", "shtml", "htm",
			"rdf", "phtm", "ref", "css", "dsl", "ent", "xml", "xsl",
			".gnuplot", 
			"entities", "man", "manpages", "man\\.[0-9]+", "docs$" };

	private static String[] xmlFormats = { ".xml", ".svn", ".argo", ".graffle",
			".vcproj", ".csproj", ".rdf", ".wsdl", ".pom", ".omf" };

	private static String[] binMimes = { ".pdf", ".png", ".jpg", ".tiff",
			".dvi", ".gz", ".zip", ".gif", ".exe", ".jar", ".doc", ".png",
			".o", ".class", ".pyc", ".bmp", ".ico", ".bz2", ".jpeg", ".war",
			".tif", ".ppt", ".xls", ".mp3", ".wmf", ".gif", ".dll", ".so" };

	private static String[] transMimes = { ".po" };

	private static final String locales = "ar_SA|zh_CN|zh_TW|nl_NL|en_AU|en_CA|"
			+ "en_GB|en_US|fr_CA|fr_FR|de_DE|iw_IL|hi_IN|it_IT|ja_JP|ko_KR|"
			+ "pt_BR|es_ES|sv_SE|th_TH|th_TH_TH|sq_AL|ar_DZ|ar_BH|ar_EG|"
			+ "ar_IQ|ar_JO|ar_KW|ar_LB|ar_LY|ar_MA|ar_OM|ar_QA|ar_SD|ar_SY|"
			+ "ar_TN|ar_AE|ar_YE|be_BY|bg_BG|ca_ES|zh_HK|hr_HR|cs_CZ|da_DK|"
			+ "nl_BE|en_IN|en_IE|en_NZ|en_ZA|et_EE|fi_FI|fr_BE|fr_LU|fr_CH|"
			+ "de_AT|de_LU|de_CH|el_GR|hu_HU|is_IS|it_CH|lv_LV|lt_LT|mk_MK|"
			+ "no_NO|no_NO_NY|pl_PL|pt_PT|ro_RO|ru_RU|sr_YU|sh_YU|sk_SK|sl_SI|"
			+ "es_AR|es_BO|es_CL|es_CO|es_CR|es_DO|es_EC|es_SV|es_GT|es_HN|"
			+ "es_MX|es_NI|es_PA|es_PY|es_PE|es_PR|es_UY|es_VE|tr_TR|uk_UA";

	static {
		lookupTable = new HashMap<String, FileType>();

		for (String s : srcMimes)
			lookupTable.put(s, FileType.SRC);

		for (String s : docMimes)
			lookupTable.put(s, FileType.DOC);

		for (String s : xmlFormats)
			lookupTable.put(s, FileType.XML);

		for (String s : binMimes)
			lookupTable.put(s, FileType.BIN);

		for (String s : transMimes)
			lookupTable.put(s, FileType.TRANS);	
	}

	/**
	 * Return the file extension of path, or null if no file extension can be
	 * found. This works on whole paths, not just filenames. The last component
	 * of path is assumed to be a filename component (e.g. /foo/bar/baz.cpp
	 * shouldn't be a directory, and will return "cpp" as extension).
	 * 
	 * @param path
	 *            File path to extract the extension for
	 * @return Extension (without the trailing .) or null if none
	 */
	public static String getFileExtension(String path) {
		if (null == path) {
			return null;
		}
		// We use \Q and \E to quote the separator char, as it might
		// be a character that is interpreted by the RE engine. And
		// we can't just use \ to escape it, since it might be a
		// character where \x is interpreted as well.
		String pathSeparatorRE = "\\Q" + java.io.File.separatorChar + "\\E";
		String extensionSeparatorRE = "\\.";
		// Keep trailing components
		String[] components = path.split(pathSeparatorRE, -1);
		if (components.length == 0) {
			// So the path consisted only of a separator?
			return null;
		}
		if (components[components.length - 1].length() > 0) {
			components = components[components.length - 1].split(
					extensionSeparatorRE, -1);
			if (components.length == 0) {
				// No components?
				return null;
			}
			if (components[components.length - 1].length() > 0) {
				String extension = "." + components[components.length - 1];
				if (path.endsWith(extension)) {
					return extension;
				} else {
					// This shouldn't be possible
					return null;
				}
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * Get the (presumed) filetype of the path, based on file extensions and
	 * some heuristics. Operates on full pathnames (see getFileExtension()
	 * above).
	 * 
	 * @param path
	 *            Path to get file type from.
	 * @return FileType or TXT if none could be determined.
	 */
	public FileType getFileType(String path) {
		String ext = getFileExtension(path);
		FileType ft = getFileTypeFromExt(ext);
		if (null != ft) {
			return ft;
		}
		
		if (doc.matcher(path).find())
			return FileType.DOC;
		
		if (locale.matcher(path).matches()) {
			return FileType.TRANS;
		}

		return FileType.TXT;
	}

	/**
	 * Checks whether a file is of text type
	 * 
	 * @param path
	 *            The path to check
	 * @return True is the file is a text file as identified by the 
	 * extension
	 */
	public boolean isTextType(String path) {
		return !isBinaryType(path);
	}

	/**
	 * Checks whether a file is of binary type
	 * 
	 * @param path
	 *            The path to check
	 * @return True is the file is a binary file as identified by the 
	 * extension
	 */
	public boolean isBinaryType(String path) {
		if (getFileType(path).equals(FileType.BIN)) {
			return true;
		}
		return false;
	}
	
	/**
     * Checks whether a file is of source code type
     * 
     * @param path The path to check
     * @return True is the file is a source code file as identified by the 
     * extension
     */
	public boolean isSourceFile(String path) {
	    FileType ft = getFileType(path);
	    if (ft.equals(FileType.SRC)) {
	        return true;
	    }
	    return false;
	}

	/**
	 * Given a filename extension ext, check the known lists of file extensions
	 * for an exact match.
	 * 
	 * @param ext
	 *            File extension to check for
	 * @return A FileType or null if no match is found
	 */
	public static FileType getFileTypeFromExt(String ext) {
		FileType ft = null;
		ft = lookupTable.get(ext);
		return ft;
	}
}

// vi: ai nosi sw=4 ts=4 expandtab
