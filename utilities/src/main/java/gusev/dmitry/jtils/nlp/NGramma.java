package gusev.dmitry.jtils.nlp;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Arrays;

/** NGramma class - tuple of words. */
public class NGramma {

    private final String[] content;

    /***/
    public NGramma(String... content) {

        if (content == null || content.length < 1) { // fail fast
            throw new IllegalStateException("Can't create empty NGramm!");
        }

        // init internal state by copying source array
        this.content = Arrays.copyOf(content, content.length);
    }

    /***/
    public String[] getContent() {
        return Arrays.copyOf(this.content, this.content.length);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NGramma nGramma = (NGramma) o;
        return Arrays.equals(this.content, nGramma.content);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.content);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("content", content)
                .toString();
    }

}
