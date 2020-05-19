# Zookeeper watcher

Goal of this script is to supervise /z znode for any new child and log subtree on console if any insertion/deletion happened.
Also if root node is created run child script and stop/kill it if /z znode is deleted.
