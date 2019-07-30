package dgusev.apps.downloadManager;

import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Observable;

/**
 * @author Gusev Dmitry (Dmitry)
 * @version 1.0 (DATE: 28.09.2014)
 */
public class Download extends Observable implements Runnable {

    // max buffer size for download
    private static final int MAX_BUFFER_SIZE = 1024;

    // states
    public static final String STATUSES[] =
            {"Downloading", "Paused", "Complete", "Cancelled", "Error"};
    // states codes
    public static final int DOWNLOADING = 0;
    public static final int PAUSED     = 1;
    public static final int COMPLETE   = 2;
    public static final int CANCELLED  = 3;
    public static final int ERROR      = 4;

    private URL url;        // source address for downloading
    private int size;       // size of downloading data (bytes)
    private int downloaded; // already downloaded bytes
    private int status;     // current download state

    public Download(URL url) {
        this.url  = url;
        this.size = -1;
        this.downloaded = 0;
        this.status = DOWNLOADING;

        // immediate start download process after creating object
        this.download();
    }

    public URL getUrl() {
        return url;
    }

    public int getSize() {
        return size;
    }

    public int getStatus() {
        return status;
    }

    public float getProgress() {
        return ((float) this.downloaded / this.size) * 100;
    }

    /** Pause current downloading process. */
    public void pause() {
        this.status = PAUSED;
        this.stateChanged();
    }

    /** Resumes current downloading process. */
    public void resume() {
        this.status = DOWNLOADING;
        this.stateChanged();
        this.download();
    }

    /** Cancel current downloading process. */
    public void cancel() {
        this.status = CANCELLED;
        this.stateChanged();
    }

    /** Error during current download process. */
    public void error() {
        this.status = ERROR;
        this.stateChanged();
    }

    /** Start or resume current downloading process. */
    private void download() {
        Thread thread = new Thread(this);
        thread.start();
    }

    /** Get downloading file name from URL. */
    private String getFileName(URL url) {
        String fileName = url.getFile();
        return fileName.substring(fileName.lastIndexOf('/') + 1);
    }

    /** Download a file. */
    public void run() {
        RandomAccessFile file   = null;
        InputStream stream = null;

        try {
            // open connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // which part of file we have to download?
            connection.setRequestProperty("Range", "bytes=" + this.downloaded + "-");
            // connect to server
            connection.connect();

            // check answer code - it should be less than 200
            if (connection.getResponseCode() / 100 != 2) {
                this.error();
            }

            // check content length
            int contentLength = connection.getContentLength();
            if (contentLength < 1) {
                this.error();
            }

            // set download process size, if not specified
            if (this.size == -1) {
                this.size = contentLength;
                this.stateChanged();
            }

            // open file and search for file end
            file = new RandomAccessFile("c:/temp/" + this.getFileName(this.url), "rw");
            file.seek(this.downloaded);

            stream = connection.getInputStream();

            while (this.status == DOWNLOADING) {
                // set buffer size for download remainded part of file
                byte buffer[];
                if (this.size - this.downloaded > MAX_BUFFER_SIZE) {
                    buffer = new byte[MAX_BUFFER_SIZE];
                } else {
                    buffer = new byte[this.size - this.downloaded];
                }

                // reading from server into buffer
                int read = stream.read(buffer);
                if (read == -1) {
                    break;
                }
                // write content of buffer to file
                file.write(buffer, 0, read);
                this.downloaded += read;
                this.stateChanged();

            } // end of WHILE cycle

            // state is COMPLETE, if we reached this point (downloading is finished)
            if (this.status == DOWNLOADING) {
                this.status = COMPLETE;
                this.stateChanged();
            }
        } catch (Exception e) {
            this.error();
        } finally {
            if (file != null) { // close file
                try {
                    file.close();
                } catch (Exception e) {

                }
            }

            if (stream != null) { // close connection to server
                try {
                    stream.close();
                } catch (Exception e) {

                }
            }
        } // end of finally block

    } // end of run() method

    /** Inform observers about changing state. */
    private void stateChanged() {
        this.setChanged();
        this.notifyObservers();
    }

}