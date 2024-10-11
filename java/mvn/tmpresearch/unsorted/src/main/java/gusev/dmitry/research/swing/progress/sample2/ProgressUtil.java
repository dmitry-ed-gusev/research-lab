package gusev.dmitry.research.swing.progress.sample2;

/**
 * @author Gusev Dmitry (GusevD)
 * @version 1.0 (DATE: 17.10.12)
 */

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * MySwing: Advanced Swing Utilites
 * Copyright (C) 2005  Santhosh Kumar T
 * <p/>
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
public class ProgressUtil{
    static class MonitorListener implements ChangeListener, ActionListener {
        ProgressMonitor monitor;
        Window owner;
        Timer timer;

        public MonitorListener(Window owner, ProgressMonitor monitor){
            this.owner = owner;
            this.monitor = monitor;
        }

        public void stateChanged(ChangeEvent ce){
            ProgressMonitor monitor = (ProgressMonitor)ce.getSource();
            if(monitor.getCurrent()!=monitor.getTotal()){
                if(timer==null){
                    timer = new Timer(monitor.getMilliSecondsToWait(), this);
                    timer.setRepeats(false);
                    timer.start();
                }
            }else{
                if(timer!=null && timer.isRunning())
                    timer.stop();
                monitor.removeChangeListener(this);
            }
        }

        public void actionPerformed(ActionEvent e){
            monitor.removeChangeListener(this);
            ProgressDialog dlg = owner instanceof Frame
                    ? new ProgressDialog((Frame)owner, monitor)
                    : new ProgressDialog((Dialog)owner, monitor);
            dlg.pack();
            dlg.setLocationRelativeTo(null);
            dlg.setVisible(true);
        }
    }

    public static ProgressMonitor createModalProgressMonitor(Component owner, int total, boolean indeterminate, int milliSecondsToWait){
        ProgressMonitor monitor = new ProgressMonitor(total, indeterminate, milliSecondsToWait);
        Window window = owner instanceof Window
                ? (Window)owner
                : SwingUtilities.getWindowAncestor(owner);
        monitor.addChangeListener(new MonitorListener(window, monitor));
        return monitor;
    }
}
