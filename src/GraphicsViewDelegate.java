import java.util.Set;

public interface GraphicsViewDelegate {
    public Set<Drawable> getObjects();

    public void refresh();

    public void waitForNextStep();
}
