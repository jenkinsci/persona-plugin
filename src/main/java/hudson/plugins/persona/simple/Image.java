package hudson.plugins.persona.simple;

/**
 * @author Kohsuke Kawaguchi
 */
public class Image {
    /**
     * 16x16 icon
     */
    public final String smallIconUrl;
    public final String backgroundImageUrl;

    public Image(String smallIconUrl, String backgroundImageUrl) {
        this.smallIconUrl = smallIconUrl;
        this.backgroundImageUrl = backgroundImageUrl;
    }
}
