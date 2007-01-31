package eu.sqooss.vcs;

/**
 * @author circular
 *
 * Represents the various types of projects supported.
 */
public enum ProjectType {
		CVS,
		SVN,
		Url, //ftp or www location of the latest version of a project
		Local //a project that is synced via an external interface and we analyze locally
}
