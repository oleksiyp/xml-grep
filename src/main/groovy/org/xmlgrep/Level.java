package org.xmlgrep;

class Level {
    StringBuilder text = new StringBuilder();
    String []path;
    String recordPath;

    public Level(String []path) {
        this.path = path;
    }

    public Level(Level level, String name) {
        this.path = new String[level.path.length + 1];
        System.arraycopy(level.path, 0, this.path, 0, level.path.length);
        this.path[level.path.length] = name;
    }

    public String[] cut(String[] prefix) {
        if (path.length < prefix.length) {
            return null;
        }
        for (int i = 0; i < prefix.length; i++) {
            if (!prefix[i].equals(path[i])) {
                return null;
            }
        }
        String []result = new String[path.length - prefix.length];
        System.arraycopy(path, prefix.length, result, 0, result.length);
        return result;
    }

    public void setRecordPath(String recordPath) {
        this.recordPath = recordPath;
    }

    public String getRecordPath() {
        return recordPath;
    }
}