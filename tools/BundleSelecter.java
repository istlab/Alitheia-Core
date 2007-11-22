import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class BundleSelecter {
    public static void main(String[] args) {
        String usage = "Usage: BundleSelecter <config.ini>";
        if (args.length < 1) {
            System.out.println(usage);
            return;
        }

        File f = new File(args[0]);
        if (!f.exists()) {
            System.out.println("No such config file.");
            System.out.println(usage);
            return;
        }

        try {
            Properties p = new Properties();
            p.load(new FileInputStream(f));

            String s = p.getProperty("osgi.bundles");
            if (s != null) {
                String[] bundles = s.split(", *");
                for (String b : bundles) {
                    if (b.indexOf('@') > 0) {
                        b = b.substring(0,b.indexOf('@'));
                    }
                    if (!b.startsWith("eu.sqooss")) {
                        if (!b.endsWith(".jar")) {
                            // Add a wildcard for version
                            b = b + "*.jar";
                        }
                        System.out.println(b);
                    }
                }
            } else {
                System.out.println("No osgi.bundles property.");
                System.out.println(usage);
                return;
            }
        } catch (Exception e) {
            System.out.println("Failed to read config file.");
            System.out.println(usage);
        }
    }
}

// vi: ai sw=4 ts=4 expandtab

