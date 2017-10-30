package gusevdm.nlp;

public class NGramma {

    private final String[] content;

    /***/
    public NGramma(String... content) {

        if (content == null || content.length < 1) { // fail fast
            throw new IllegalStateException("Can't create empty NGramm!");
        }

        this.content = new String[content.length];
        System.arraycopy(content, 0, this.content, 0, content.length);
    }


}
