/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package alloshowdesktop;

/**
 *
 * @author http://www.java-tips.org/java-se-tips/javax.swing/how-to-create-a-download-manager-in-java.html
 */
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

// This class manages the download table's data.
class DownloadsTableModel extends AbstractTableModel
        implements Observer {

    // These are the names for the table's columns.
    private static final String[] columnNames = {"Show", "Season", "Episode",
        "Size", "Progress", "Speed", "Status"};

    // These are the classes for each column's values.
    private static final Class[] columnClasses = {String.class, Integer.class,
        Integer.class, Integer.class, JProgressBar.class, String.class, String.class};

    // The table's list of downloads.
    private ArrayList downloadList = new ArrayList();

    // Add a new download to the table.
    public void addDownload(Download download) {

        // Register to be notified when the download changes.
        download.addObserver(this);

        downloadList.add(download);

        // Fire table row insertion notification to table.
        fireTableRowsInserted(getRowCount() - 1, getRowCount() - 1);
    }

    // Get a download for the specified row.
    public Download getDownload(int row) {
        return (Download) downloadList.get(row);
    }

    // Remove a download from the list.
    public void clearDownload(int row) {
        downloadList.remove(row);

        // Fire table row deletion notification to table.
        fireTableRowsDeleted(row, row);
    }

    // Get table's column count.
    public int getColumnCount() {
        return columnNames.length;
    }

    // Get a column's name.
    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    // Get a column's class.
    @Override
    public Class getColumnClass(int col) {
        return columnClasses[col];
    }

    // Get table's row count.
    public int getRowCount() {
        return downloadList.size();
    }

    // Get value for a specific row and column combination.
    public Object getValueAt(int row, int col) {
        Download download = (Download) downloadList.get(row);
        switch (col) {
            case 0: // Show
                return download.getShow();
            case 1: // Season
                return download.getSeason();
            case 2: // Episode
                return download.getEpisode();
            case 3: // Size
                int size = download.getSize();
                return (size == -1) ? "" : Integer.toString(size/1048576);//b to Mb
            case 4: // Progress
                return new Float(download.getProgress());
            case 5: // Speed
                if (((System.currentTimeMillis() - download.getTime()) / 1000.0) >= 2){
                    download.setRate((int) (((download.getDownloaded()-download.getLastDownloaded()) / 1000.0)
                            / ((System.currentTimeMillis() - download.getTime()) / 1000.0)));
                    download.setTime(System.currentTimeMillis());
                    download.setLastDownloaded(download.getDownloaded());
                }
                return Integer.toString(download.getRate())+" Kb/s";
            case 6: // Status
                return Download.STATUSES[download.getStatus()];
        }
        return "";
    }

  /* Update is called when a Download notifies its
     observers of any changes */
    public void update(Observable o, Object arg) {
        int index = downloadList.indexOf(o);

        // Fire table row update notification to table.
        fireTableRowsUpdated(index, index);
    }
}