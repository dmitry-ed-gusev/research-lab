package dg.bigdata.hw1.words;

import org.apache.commons.lang.ArrayUtils;
import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;

/**
 * Implementation of ArrayWritable (array of objects references).
 * Created by gusevdm on 11/15/2016.
 */

public class TextArrayWritable extends ArrayWritable {

    public TextArrayWritable() {
        super(Text.class);
    }

    public TextArrayWritable join(TextArrayWritable array) {

        if (array == null) { // nothing to join, return the same array
            return this;
        }

        // create and return result
        TextArrayWritable result = new TextArrayWritable();
        result.set((Writable[]) ArrayUtils.addAll(this.get(), array.get()));
        return result;
    }

}
