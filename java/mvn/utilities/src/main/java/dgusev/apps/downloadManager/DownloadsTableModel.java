package dgusev.apps.downloadManager;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * @author Gusev Dmitry (Dmitry)
 * @version 1.0 (DATE: 28.09.2014)
*/

public class DownloadsTableModel extends AbstractTableModel implements Observer {

    // table columns names
    private static final String[] columnNames = {"URL", "Size", "Progress", "Status"};
    // value classes for every column
    private static final Class[] columnClasses = {String.class, String.class, JProgressBar.class, String.class};
    // list of download processes
    private List<Download> downloadList = new ArrayList<>();

    /** Adding new download process. */
    public void addDownload(Download download) {
        download.addObserver(this); // register for getting state change events
        this.downloadList.add(download);
        // create notification for table about row insertion
        this.fireTableRowsInserted(this.getRowCount() - 1, this.getRowCount() - 1);
    }

    /** Getting download process for concrete row. */
    public Download getDownload(int row) {
        return downloadList.get(row);
    }

    /** Remove download process from list. */
    public void clearDownload(int row) {
        downloadList.remove(row);
        // create notification
        this.fireTableRowsDeleted(row, row);
    }

    /***/
    public int getColumnCount() {
        return columnNames.length;
    }

    /***/
    public String getColumnName(int col) {
        return columnNames[col];
    }

    /***/
    public Class getColumnClass(int col) {
        return columnClasses[col];
    }

    /***/
    public int getRowCount() {
        return downloadList.size();
    }

    /***/
    public Object getValueAt(int row, int col) {
        Download download = downloadList.get(row);
        switch (col) {
            case 0: // URL address
                return download.getUrl();
            case 1: // size
                int size = download.getSize();
                return (size == -1) ? "" : Integer.toString(size);
            case 2: // progress
                return new Float(download.getProgress());
            case 3: // state
                return Download.STATUSES[download.getStatus()];
        }
        return "";
    }

    /** Update, if download process notifies about changes. */
    public void update(Observable o, Object arg) {
        int index = downloadList.indexOf(o);
        this.fireTableRowsUpdated(index, index);
    }

}