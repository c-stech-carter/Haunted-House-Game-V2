import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Room {
    private String name;
    private String initialDescription;
    private String lookDescription;
    private String imagePath;
    private String[] exits;
    private boolean isLocked;
    private List<String> items;
    private List<String> requiredItems;

    public Room(String name, String initialDescription, String lookDescription, String imagePath,
                String[] exits, boolean isLocked, String[] items, String[] requiredItems) {
        this.name = name;
        this.initialDescription = initialDescription;
        this.lookDescription = lookDescription;
        this.imagePath = imagePath;
        this.exits = exits;
        this.isLocked = isLocked;
        this.items = new ArrayList<>(Arrays.asList(items));
        this.requiredItems = new ArrayList<>(Arrays.asList(requiredItems));
    }

    public String getName() {
        return name;
    }

    public String getInitialDescription() {
        return initialDescription;
    }

    public String getLookDescription() {
        return lookDescription;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String[] getExits() {
        return exits;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void unlock() {
        isLocked = false;
    }

    public List<String> getItems() {
        return items;
    }

    public void removeItem(String item) {
        items.remove(item);
    }

    public List<String> getRequiredItems() {
        return requiredItems;
    }
}
