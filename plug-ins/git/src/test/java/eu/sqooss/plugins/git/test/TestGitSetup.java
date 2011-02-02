package eu.sqooss.plugins.git.test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.dircache.DirCacheCheckout;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.RefUpdate;
import org.eclipse.jgit.lib.TextProgressMonitor;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileBasedConfig;
import org.eclipse.jgit.storage.file.FileRepository;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.Transport;
import org.eclipse.jgit.transport.URIish;

import eu.sqooss.plugins.tds.git.GitAccessor;
import eu.sqooss.service.tds.AccessorException;

public class TestGitSetup {

    public static String projectName = "ruby-git";
    public static FileRepository local;
    public static SimpleDateFormat sdf;
    public static String url = "git://github.com/git/git.git";
    public static String localrepo = System.getProperty("user.dir") + "/test";
    
    public static GitAccessor git;
    
    public static void initTestRepo() throws IOException, URISyntaxException {
        File repo = new File(localrepo, Constants.DOT_GIT);
        local =  new FileRepository(repo);
        sdf = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", new Locale("en"));
        
        if (repo.exists())
            return;
            
        local.create();
        
        final FileBasedConfig localcfg = local.getConfig();
        localcfg.setBoolean("core", null, "bare", false);
        localcfg.save();
        
        RemoteConfig remoteConfig = new RemoteConfig(local.getConfig(), "master");
        remoteConfig.addURI(new URIish(url));
        
        final String dst = Constants.R_REMOTES + remoteConfig.getName();
        RefSpec wcrs = new RefSpec();
        wcrs = wcrs.setForceUpdate(true);
        wcrs = wcrs.setSourceDestination(Constants.R_HEADS + "*", Constants.R_REMOTES + "master" + "/*");
        remoteConfig.addFetchRefSpec(wcrs);
        
        remoteConfig.update(local.getConfig());
        local.getConfig().save();

        Transport t = Transport.open(local, "master");
        FetchResult fetchResult = t.fetch(new TextProgressMonitor(), null);
        t.close();
        Ref head = fetchResult.getAdvertisedRef("HEAD");
        
        final RevWalk rw = new RevWalk(local);
        final RevCommit commit;
        try {
            commit = rw.parseCommit(head.getObjectId());
        } finally {
            rw.release();
        }
        
        final RefUpdate u = local.updateRef(Constants.HEAD);
        u.setNewObjectId(commit);
        u.forceUpdate();

        DirCache dc = local.lockDirCache();
        DirCacheCheckout co = new DirCacheCheckout(local, dc, commit.getTree());
        co.checkout();
    }
    
    public static void getGitRepo() throws AccessorException, URISyntaxException {
        git = new GitAccessor();
        git.testInit(new URI("git-file://" + localrepo), projectName);
    }
}
