package gusev.dmitry.jtils.processing;

/**
 * Common interface for one data processing component, that may work in seperate thread.
 * Interface used for data processing unification.
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 31.07.13)
*/

public interface ProcessorInterface {

    public void process();

}