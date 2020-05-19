package zoo;

import java.io.*;
import java.util.*;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.utils.ZKPaths;


public class Curator {
    private static final String PATH = "/z";
    private static final String ROOT = "/";
    private static final String EXEC = "src/main/resources/script.bat";
    private static final Set<String> nodes = new HashSet<>();
    private static Process child = null;
    private static boolean DEBUG = true;

    public static void main(String[] args) throws Exception {
        String zkConnString = "0.0.0.0:2181";
        CuratorFramework client = null;
        TreeCache cache = null;
        try {
            client = CuratorFrameworkFactory.newClient(zkConnString,
                    new ExponentialBackoffRetry(1000, 3));
            client.start();

            cache = new TreeCache(client, ROOT);
            cache.start();
            processCommands(client, cache);
        } finally {
            CloseableUtils.closeQuietly(cache);
            CloseableUtils.closeQuietly(client);
        }
    }

    private static void addListener(final TreeCache cache) {
        TreeCacheListener listener = (client, event) -> {

            String znodePath = event.getData().getPath();
            String znodeName = ZKPaths.getNodeFromPath(znodePath);

            if (znodePath.startsWith(PATH + "/") || znodePath.equals(PATH))
                switch (event.getType()) {
                    case NODE_ADDED: {
                        System.out.println("zNode added: "
                                + znodeName);
                        nodes.add(znodePath);

                        printTree();

                        if (DEBUG && znodePath.equals(PATH))
                            startChild(EXEC);
                        break;
                    }
                    case NODE_UPDATED: {
                        System.out.println("zNode changed: "
                                + znodeName);

                        if (DEBUG && znodePath.equals(PATH) && !nodes.contains(znodePath))
                            startChild(EXEC);

                        nodes.add(znodePath);
                        break;
                    }
                    case NODE_REMOVED: {

                        System.out
                                .println("zNode removed: "
                                        + znodeName);

                        if (DEBUG && znodePath.equals(PATH))
                            killChild();

                        printTree();

                        nodes.remove(znodePath);
                        break;
                    }
                    default:
                        System.out
                                .println("Other event: " + event.getType().name());
                }
        };

        cache.getListenable().addListener(listener);
    }

    private static void processCommands(CuratorFramework client, TreeCache cache)
            throws Exception {
        addListener(cache);
        BufferedReader in = new BufferedReader(new InputStreamReader(
                System.in));
        boolean done = false;
        while (!done) {
            System.out.print("> ");
            String line = in.readLine();
            if (line == null) {
                break;
            }
            String command = line.trim();
            String[] parts = command.split("\\s");
            if (parts.length == 0) {
                continue;
            }
            String operation = parts[0];
            if (operation.equals("list")) {
                printTree();
            }
            Thread.sleep(1000);
        }
    }

    static public void killChild() {
        if (child != null) {
            System.out.println("Killing process");
            child.destroy();
            try {
                child.waitFor();
            } catch (InterruptedException e) {
            }
        }
        child = null;
    }

    static class StreamWriter extends Thread {
        OutputStream os;

        InputStream is;

        StreamWriter(InputStream is, OutputStream os) {
            this.is = is;
            this.os = os;
            start();
        }

        public void run() {
            byte b[] = new byte[80];
            int rc;
            try {
                while ((rc = is.read(b)) > 0) {
                    os.write(b, 0, rc);
                }
            } catch (IOException e) {
            }

        }
    }

    static public void startChild(String exec) {
        try {
            System.out.println("Starting child");
            child = Runtime.getRuntime().exec(exec);
            new Curator.StreamWriter(child.getInputStream(), System.out);
            new Curator.StreamWriter(child.getErrorStream(), System.err);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static public void printTree() {
        nodes.forEach(System.out::println);
    }
}