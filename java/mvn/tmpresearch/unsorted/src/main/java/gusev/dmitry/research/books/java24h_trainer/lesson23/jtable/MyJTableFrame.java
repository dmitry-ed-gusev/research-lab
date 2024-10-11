package gusev.dmitry.research.books.java24h_trainer.lesson23.jtable;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.ArrayList;

/**
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 05.10.12)
 */
public class MyJTableFrame extends JFrame implements TableModelListener {

    private MyJTableModel myTableModel;
    private JTable        myTable;

    MyJTableFrame (String title) {
        super(title);

        myTableModel = new MyJTableModel();
        myTable = new JTable(myTableModel);

        this.add(new JScrollPane(myTable));

        myTableModel.addTableModelListener(this);

        // custom cell renderer
        TableColumn column = myTable.getColumnModel().getColumn(3);
        // create cell renderer as an anonymous inner class and assign it to the column PRICE
        column.setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table,
                        value, isSelected, hasFocus, row, col);
                // right align the price value
                label.setHorizontalAlignment(JLabel.RIGHT);
                // display stocks that cost more than $100 in red
                if (((Float) value) > 100) {
                    label.setForeground(Color.RED);
                } else {
                    label.setForeground(Color.BLACK);
                }
                return label;
            } // end of getTableCellRendererComponent
        }); // end of DefaultTableCellRenderer and setCellRenderer

    }

    @Override
    public void tableChanged(TableModelEvent e) {
    }

    public static void main(String[] args) {
        MyJTableFrame myJTableFrame = new MyJTableFrame("Test JTable");
        myJTableFrame.pack();
        myJTableFrame.setVisible(true);
    }

    // inner class for data model
    class MyJTableModel extends AbstractTableModel{

        private ArrayList<Order> myData      = new ArrayList<Order>();
        private String[]         columnNames = {"Order ID", "Symbol", "Quantity", "Price"};

        MyJTableModel() {
            myData.add(new Order(1, "IBM",  100,  135.5f));
            myData.add(new Order(2, "AAPL", 300,  290.12f));
            myData.add(new Order(3, "MOT",  2000, 8.32f));
            myData.add(new Order(4, "ORCL", 500,  27.8f));
        }

        @Override
        public int getRowCount() {
            return myData.size();
        }

        @Override
        public int getColumnCount() {
            return 4;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            switch(columnIndex) {
                case 0:  return myData.get(rowIndex).getOrderId();
                case 1:  return myData.get(rowIndex).getStockSymbol();
                case 2:  return myData.get(rowIndex).getQuantity();
                case 3:  return myData.get(rowIndex).getPrice();
                default: return "";
            }
        }

        @Override
        public String getColumnName(int i) {
            return columnNames[i];
        }

    } // end of inner class MyJTableModel

}