package AtemIDE;

import java.io.InputStream;
import java.net.URL;

public abstract class ResourceLoader {
    public static InputStream getResourceAsStream(String res)
    {
        return ResourceLoader.class.getResourceAsStream(res);
    }

    public static URL getResource(String name)
    {
        return  ResourceLoader.class.getResource(name);
    }
}
