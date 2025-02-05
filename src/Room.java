import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * The {@code Room} class represents a room in the game.
 * Each room has a name, description, an image, possible exits,
 * and a list of items that can be found or required to unlock it.
 */
public class Room {
    private String name;
    private String initialDescription;
    private String lookDescription;
    private String imagePath;
    private String[] exits;
    private boolean isLocked;
    private List<String> items;
    private List<String> requiredItems;

    /**
     * Constructs a new {@code Room} object with the given attributes.
     *
     * @param name The name of the room.
     * @param initialDescription The description displayed when the player first enters the room.
     * @param lookDescription The description displayed when the player looks around.
     * @param imagePath The file path of the room's background image.
     * @param exits The names of the rooms that can be accessed from this room.
     * @param isLocked Whether the room is initially locked.
     * @param items The list of items that can be found in the room.
     * @param requiredItems The list of items required to unlock this room.
     */
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

    /**
     * Gets the name of the room.
     *
     * @return The room name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the description of the room when the player first enters it.
     *
     * @return The initial description of the room.
     */
    public String getInitialDescription() {
        return initialDescription;
    }

    /**
     * Gets the description of the room when the player looks around.
     *
     * @return The look description of the room.
     */
    public String getLookDescription() {
        return lookDescription;
    }

    /**
     * Gets the file path of the image representing the room.
     *
     * @return The image path of the room.
     */
    public String getImagePath() {
        return imagePath;
    }

    /**
     * Gets the names of the rooms that can be accessed from this room.
     *
     * @return An array of room names representing exits.
     */
    public String[] getExits() {
        return exits;
    }

    /**
     * Checks whether the room is locked.
     *
     * @return {@code true} if the room is locked, {@code false} otherwise.
     */
    public boolean isLocked() {
        return isLocked;
    }

    /**
     * Unlocks the room, allowing access.
     */
    public void unlock() {
        isLocked = false;
    }

    /**
     * Gets the list of items available in the room.
     *
     * @return A list of item names.
     */
    public List<String> getItems() {
        return items;
    }

    /**
     * Removes an item from the room's item list.
     *
     * @param item The name of the item to remove.
     */
    public void removeItem(String item) {
        items.remove(item);
    }

    /**
     * Gets the list of items required to unlock the room.
     *
     * @return A list of item names required to unlock the room.
     */
    public List<String> getRequiredItems() {
        return requiredItems;
    }
}
