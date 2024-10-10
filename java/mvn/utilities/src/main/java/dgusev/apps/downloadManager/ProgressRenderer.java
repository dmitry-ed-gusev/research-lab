package dgusev.apps.downloadManager;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * Class for visualizing JProgressBar in table cell.
 * @author Gusev Dmitry (Dmitry)
 * @version 1.0 (DATE: 28.09.2014)
*/

public class ProgressRenderer extends JProgressBar implements TableCellRenderer {

    public ProgressRenderer(int min, int max) {
        super(min, max);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {
        // determine done percentage for JProgressBar
        this.setValue((int) ((Float) value).floatValue());
        return this;
    }
}