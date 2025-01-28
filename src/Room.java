/*
Author: Charles Carter
Date: 11/14/2024

This is the Room class for the Haunted House game. It represents a room in the mansion, including its name,
descriptions, image path, exits, locked state, and any items it contains.
*/

import java.util.List;

/**
 * The Room class encapsulates the details of a room in the game.
 */
public class Room {
    private String name;
    private String initialDescription;
    private String lookDescription;
    private String imagePath;
    private String[] exits;
    private boolean isLocked;
    private String[] items;

    /**
     * Constructs a new Room.
     *
     * @param name               The name of the room.
     * @param initialDescription The description displayed upon initial entry.
     * @param lookDescription    The description displayed when the "Look" action is performed.
     * @param imagePath          The file path to the room's image.
     * @param exits              An array of strings representing exits to other rooms.
     * @param isLocked           Whether the room is initially locked.
     * @param items              An array of items available in the room.
     */
    public Room(String name, String initialDescription, String lookDescription, String imagePath, String[] exits, boolean isLocked, String[] items) {
        this.name = name;
        this.initialDescription = initialDescription;
        this.lookDescription = lookDescription;
        this.imagePath = imagePath;
        this.exits = exits;
        this.isLocked = isLocked;
        this.items = items;
    }

    /**
     * Gets the name of the room.
     *
     * @return The name of the room.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the initial description of the room.
     *
     * @return The initial description of the room.
     */
    public String getInitialDescription() {
        return initialDescription;
    }

    /**
     * Gets the "Look" description of the room.
     *
     * @return The "Look" description of the room.
     */
    public String getLookDescription() {
        return lookDescription;
    }

    /**
     * Gets the file path to the room's image.
     *
     * @return The file path to the room's image.
     */
    public String getImagePath() {
        return imagePath;
    }

    /**
     * Gets the exits available from this room.
     *
     * @return An array of strings representing the exits.
     */
    public String[] getExits() {
        return exits;
    }

    /**
     * Checks if the room is locked.
     *
     * @return True if the room is locked, otherwise false.
     */
    public boolean isLocked() {
        return isLocked;
    }

    /**
     * Unlocks the room.
     */
    public void unlock() {
        isLocked = false;
    }

    /**
     * Gets the items available in the room.
     *
     * @return An array of items in the room.
     */
    public String[] getItems() {
        return items;
    }
}
