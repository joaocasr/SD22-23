package sd20200129;

import java.util.List;

interface Ficheiros {
    void using(String path);
    void notUsing(String path, boolean modified);
    List<String> startBackup();
    void endBackup();
}