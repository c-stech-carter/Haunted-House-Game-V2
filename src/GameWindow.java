import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.*;

public class GameWindow extends Application {
    //For simplicity in its uses in multiple areas this static variable is public
    public static Scene scene;

    private Map<String, Room> rooms;
    private Room currentRoom;
    private ImageView backgroundView;
    private TextArea descriptionArea;
    private Menu exitsMenu;
    private FlowPane inventoryPane;
    private List<String> inventory = new ArrayList<>();
    private Set<String> foundItems = new HashSet<>(); // Tracks items already found
    private Map<String, String> itemDiscoveryDescriptions = new HashMap<>();
    private MenuBar menuBar;
    private ContextMenu contextMenu;


    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: dimgray;");

        StackPane gameArea = new StackPane();
        gameArea.setPrefSize(1300, 743);

        backgroundView = new ImageView();
        backgroundView.setPreserveRatio(true);
        backgroundView.setFitWidth(1300);
        backgroundView.setFitHeight(743);
        gameArea.getChildren().add(backgroundView);
        gameArea.setAlignment(Pos.CENTER);
        root.setCenter(gameArea);

        menuBar = new MenuBar();
        Menu actionsMenu = new Menu("Actions");
        MenuItem searchItem = new MenuItem("Search for Items");
        searchItem.setOnAction(e -> handleSearchAction());
        MenuItem lookItem = new MenuItem("Look Around");
        lookItem.setOnAction(e -> handleLookAction());
        actionsMenu.getItems().addAll(searchItem, lookItem);

        exitsMenu = new Menu("Exits");
        menuBar.getMenus().addAll(actionsMenu, exitsMenu);
        root.setTop(menuBar);

        contextMenu = new ContextMenu();
        MenuItem searchContextItem = new MenuItem("Search for Items");
        searchContextItem.setOnAction(e -> handleSearchAction());
        MenuItem lookContextItem = new MenuItem("Look Around");
        lookContextItem.setOnAction(e -> handleLookAction());

        contextMenu.getItems().addAll(searchContextItem, lookContextItem);

        descriptionArea = new TextArea();
        descriptionArea.setWrapText(true);
        descriptionArea.setEditable(false);
        descriptionArea.setPrefHeight(150);
        descriptionArea.setPrefWidth(600);
        descriptionArea.setStyle("-fx-control-inner-background: black; -fx-text-fill: yellow;");

        ScrollPane textScrollPane = new ScrollPane(descriptionArea);
        textScrollPane.setFitToWidth(true);
        textScrollPane.setFitToHeight(false);

        inventoryPane = new FlowPane();
        inventoryPane.setPadding(new Insets(10));
        inventoryPane.setHgap(10);
        inventoryPane.setVgap(10);
        inventoryPane.setStyle("-fx-background-color: #444444; -fx-border-color: black; -fx-border-width: 2;");
        inventoryPane.setPrefHeight(150);
        inventoryPane.setPrefWidth(200);

        ScrollPane inventoryScrollPane = new ScrollPane(inventoryPane);
        inventoryScrollPane.setFitToWidth(true);
        inventoryScrollPane.setFitToHeight(false);

        HBox bottomBox = new HBox(10, textScrollPane, inventoryScrollPane);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(10));
        root.setBottom(bottomBox);

        initializeRooms();
        initializeItemDiscoveryDescriptions();
        updateRoom("Front Yard");

        scene = new Scene(root, 1320, 950, Color.DIMGRAY);
        scene.setOnContextMenuRequested(e -> {
            contextMenu.show(scene.getWindow(), e.getScreenX(), e.getScreenY());
        });
        primaryStage.setTitle("Haunted House Game");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }


    //When inventory items are found, the description for their discovery is defined here
    private void initializeItemDiscoveryDescriptions() {
        itemDiscoveryDescriptions.put("Rusty Key", "You search the area and notice a small key under the rug, you pick it up.");
        itemDiscoveryDescriptions.put("Green Crystal", "You also notice a small green crystal.  It looks important so you take it with you.");
        itemDiscoveryDescriptions.put("Mysterious Note", "As you rummage through the papers on the table, you find a torn piece of paper with strange symbols.");
    }

    //The rooms of the mansion are defined in this method
    private void initializeRooms() {
        rooms = new HashMap<>();

        //The front yard, the first area in the game
        Room frontYard = new Room("Front Yard",
                "\tThe last thing you remember is an intense pain to " +
                        "the back of your head, and after that, darkness for a time, where you were barely aware of yourself.   " +
                        "Beyond that, there are no memories, but echoes still dance in your mind, unable to be fully retrieved.    " +
                        "Now as your vision returns you find yourself standing in the yard of a very large foreboding house.   " +
                        "A very strange realization is forced on your mind, that this place *isn't real*...   and yet, you can feel " +
                        "the cold biting at your skin.  Looking at the home, it seems many lights are on, but no movement can " +
                        "be seen through any of them.   Whether or not this place is situated in reality, something tells you are " +
                        "trapped here by a real force, and the only way to wake from this dream is to enter the house. The way out " +
                        "of this realm is hidden inside, somewhere.  ",
                "\tThe path up to the house is well-trodden, and you see many footprints leading " +
                        "up to the porch and the front door.   Could it be that many a person has stood where you do now?   " +
                        "All of the footprints start around where you are standing, and there are " +
                        "none leading away from the house.   Something tells you much worse things " +
                        "could be waiting if you were to walk into the woods.",
                "file:src/resources/image/FrontYard.png",
                new String[]{"Front Hall"}, false, new String[]{"Rusty Key", "Green Crystal"}, new String[]{});

        Room frontHall = new Room("Front Hall",
                "\tYou step into the grand front hall of the mansion. Dust swirls in the dim light..." +
                        "  something is very wrong here, but you can't quite tell what exactly.",
                "\tYou stand in the Front Hall of the house.  Calling out does nothing and no human " +
                        "presence  arrives to the room. The door you entered this place with, has somehow locked " +
                        "itself, and no force you can muster can open it. Many lights are on but there is silence, " +
                        "a suffocating silence that you can almost feel in your throat.  The room itself is " +
                        "quite beautiful,  a grand staircase goes to the upper floor, a chandelier hangs from " +
                        "the ceiling.  A door on the left goes to a parlor, and a door behind you goes to what " +
                        "seems to be a dining room.   Two paintings hang here on the walls, one of a sad looking " +
                        "young woman, and the other of a woman who seems to be in severe distress.  " +
                        "Her hair is shorn, and her clothing is gray.",
                "file:src/resources/image/FrontHall.png",
                new String[]{"Dining Room", "Parlor", "Upstairs Loft"}, true, new String[]{}, new String[]{"Rusty Key"});

        Room parlor = new Room("Parlor",
                "Placeholder",
                "Placeholder",
                "file:src/resources/image/Parlor.png",
                new String[]{"Front Hall", "Piano"}, false, new String[]{}, new String[]{});

        Room diningRoom = new Room("Dining Room",
                "Placeholder",
                "Placeholder",
                "file:src/resources/image/DiningRoom.png",
                new String[]{"Front Hall", "Kitchen"}, false, new String[]{}, new String[]{});

        Room kitchen = new Room("Kitchen",
                "Placeholder",
                "Placeholder",
                "file:src/resources/image/Kitchen.png",
                new String[]{"Dining Room", "Basement"}, false, new String[]{}, new String[]{});

        Room basement = new Room("Basement",
                "Placeholder",
                "Placeholder",
                "file:src/resources/image/Basement.png",
                new String[]{"Kitchen", "Wine Cellar"}, false, new String[]{}, new String[]{});

        Room wineCellar = new Room("Wine Cellar",
                "Placeholder",
                "Placeholder",
                "file:src/resources/image/WineCellar.png",
                new String[]{"Basement"}, false, new String[]{}, new String[]{});

        Room upstairs = new Room("Upstairs Loft",
                "Placeholder",
                "Placeholder",
                "file:src/resources/image/Upstairs.png",
                new String[]{"Front Hall", "Master Bedroom", "Guest Bedroom", "Servants Quarters"}, false, new String[]{}, new String[]{});

        //Blank room for following a pattern to add more rooms:
        Room masterBedroom = new Room("Master Bedroom",
                "Placeholder",
                "Placeholder",
                "file:src/resources/image/MasterBedroom.png",
                new String[]{"Library", "Upstairs Loft", "Small Washroom"}, false, new String[]{}, new String[]{});

        Room libraryRoom = new Room("Library",
                "Placeholder",
                "Placeholder",
                "file:src/resources/image/LibraryRoom.png",
                new String[]{"Master Bedroom"}, false, new String[]{}, new String[]{});

        Room washroom = new Room("Small Washroom",
                "Placeholder",
                "Placeholder",
                "file:src/resources/image/Washroom.png",
                new String[]{"Master Bedroom"}, false, new String[]{}, new String[]{});

        Room guestBedroom = new Room("Guest Bedroom",
                "Placeholder",
                "Placeholder",
                "file:src/resources/image/GuestBedroom.png",
                new String[]{"Closet Stairway", "Upstairs Loft"}, false, new String[]{}, new String[]{});

        Room stairway = new Room("Closet Stairway",
                "Placeholder",
                "Placeholder",
                "file:src/resources/image/Stairway.png",
                new String[]{"Attic", "Guest Bedroom"}, false, new String[]{}, new String[]{});

        Room attic = new Room("Attic",
                "Placeholder",
                "Placeholder",
                "file:src/resources/image/Attic.png",
                new String[]{"Closet Stairway"}, false, new String[]{}, new String[]{});

        Room servantRoom = new Room("Servants Quarters",
                "Placeholder",
                "Placeholder",
                "file:src/resources/image/ServantBedroom.png",
                new String[]{"Upstairs Loft"}, false, new String[]{}, new String[]{});


        //This is the final area of the game, after the player has collected all the necessary items
        Room piano = new Room("Piano",
                "Placeholder",
                "Placeholder",
                "file:src/resources/image/Hallway.png",
                new String[]{"Small Door"}, false, new String[]{}, new String[]{});


        //Blank room for following a pattern to add more rooms:
        Room template = new Room("Placeholder",
                "Placeholder",
                "Placeholder",
                "file:src/resources/image/temp.png",
                new String[]{}, false, new String[]{}, new String[]{});


        //Add defined rooms to HashMap rooms
        rooms.put("Front Yard", frontYard);
        rooms.put("Front Hall", frontHall);
        rooms.put("Parlor", parlor);
        rooms.put("Dining Room", diningRoom);
        rooms.put("Kitchen", kitchen);
        rooms.put("Basement", basement);
        rooms.put("Wine Cellar", wineCellar);
        rooms.put("Upstairs Loft", upstairs);
        rooms.put("Master Bedroom", masterBedroom);
        rooms.put("Library", libraryRoom);
        rooms.put("Small Washroom", washroom);
        rooms.put("Guest Bedroom", guestBedroom);
        rooms.put("Closet Stairway", stairway);
        rooms.put("Attic", attic);
        rooms.put("Servants Quarters", servantRoom);
        rooms.put("Piano", piano);
    }

    private void updateRoom(String roomName) {
        Room room = rooms.get(roomName);
        if (room == null) return;

        if (room.isLocked()) {
            List<String> requiredItems = room.getRequiredItems();
            if (inventory.containsAll(requiredItems)) {
                inventory.removeAll(requiredItems);
                updateInventoryUI();
                room.unlock();

                // Temporarily disable menus
                menuBar.setDisable(true);

                contextMenu.getItems().clear();

                if (roomName.equalsIgnoreCase("Front Hall")) {
                    descriptionArea.setText("\tThe rusty key you found under the mat fits into the keyhole" +
                            " of the foreboding front door of the house. The door unlocks and you step inside. " +
                            "To your astonishment, the key disintegrates, as if made from ash, and " +
                            "completely disappears. (Click to continue...)");
                } else {
                    descriptionArea.setText("\nYou used " + String.join(", ", requiredItems) + " to unlock the " + roomName + "!");
                }

                scene.setOnMouseClicked(e -> {
                    scene.setOnMouseClicked(null);
                    updateRoomAfterUnlock(room);
                    menuBar.setDisable(false);
                });

                return;
            } else {
                descriptionArea.setText("The door to " + roomName + " is locked. You need: " + requiredItems);
                return;
            }
        }


        updateRoomAfterUnlock(room);


    }


    // Separate method to update the room after unlocking
    private void updateRoomAfterUnlock(Room room) {
        //Clear context menu during transition
        contextMenu.getItems().clear();

        // Update room details after fade-out
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), backgroundView);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        fadeOut.setOnFinished(e -> {
            currentRoom = room;
            backgroundView.setImage(new Image(room.getImagePath()));
            descriptionArea.setText(room.getInitialDescription());

            exitsMenu.getItems().clear();
            for (String exit : room.getExits()) {
                MenuItem exitItem = new MenuItem(exit);
                exitItem.setOnAction(ev -> updateRoom(exit));
                exitsMenu.getItems().add(exitItem);
            }

            updateContextMenuExits(room);
            // Create a fade-in transition
            FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), backgroundView);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.setOnFinished(ev -> {

                MenuItem searchItem = new MenuItem("Search for Items");
                searchItem.setOnAction(event -> handleSearchAction());

                MenuItem lookItem = new MenuItem("Look Around");
                lookItem.setOnAction(event -> handleLookAction());

                contextMenu.getItems().add(searchItem);
                contextMenu.getItems().add(lookItem);

                //Ensure exits are properly restored to the context menu
                updateContextMenuExits(room);
            });
            fadeIn.play();
        });
        fadeOut.play();
    }

    private void updateContextMenuExits(Room room) {
        contextMenu.getItems().removeIf(item -> item.getText().startsWith("Go to ")); // Remove old exits

        for (String exit : room.getExits()) {
            MenuItem exitItem = new MenuItem("Go to " + exit);
            exitItem.setOnAction(e -> updateRoom(exit));
            contextMenu.getItems().add(exitItem);
        }
    }


    private void handleLookAction() {
        if (currentRoom != null) {
            descriptionArea.setText(currentRoom.getLookDescription());
        }
    }

    private void handleSearchAction() {
        if (currentRoom == null) return;

        List<String> items = new ArrayList<>(currentRoom.getItems());
        if (items.isEmpty()) {
            descriptionArea.setText("You search the area but find nothing of interest.");
            return;
        }

        StringBuilder foundItemsDescription = new StringBuilder();
        for (String item : items) {
            if (!foundItems.contains(item)) { // Only show discovery text once
                foundItems.add(item);
                String discoveryMessage = itemDiscoveryDescriptions.getOrDefault(item, "You found " + item + "!");
                foundItemsDescription.append(discoveryMessage).append("\n");
            }
            inventory.add(item);
        }

        currentRoom.getItems().clear();
        descriptionArea.setText(foundItemsDescription.toString().trim());
        updateInventoryUI();
    }

    private void updateInventoryUI() {
        inventoryPane.getChildren().clear();
        for (String item : inventory) {
            ImageView itemIcon = new ImageView(new Image("file:src/resources/image/icons/" + item + ".png"));
            itemIcon.setFitWidth(32);
            itemIcon.setFitHeight(32);
            Tooltip.install(itemIcon, new Tooltip(item));
            // Add left-click event handler to show item-specific descriptions
            itemIcon.setOnMouseClicked(event -> {
                if (event.getButton().name().equals("PRIMARY")) { // Left-click

                    String description; // Variable to hold the description

                    // Check which item was clicked and provide a unique description
                    if (item.equals("Green Crystal")) {
                        description = "The small green crystal glows faintly in your hand. It hums with a strange energy, as if reacting to your presence.";
                    } else if (item.equals("Rusty Key")) {
                        description = "A small, rusty key. It looks fragile, but it might still be useful for unlocking something.";
                    } else if (item.equals("Mysterious Note")) {
                        description = "A torn piece of paper covered in strange symbols. You can't make sense of it... yet.";
                    } else {
                        // Default fallback description if no specific one is provided
                        description = "A mysterious item. You wonder what it could be used for.";
                    }

                    // Update the description area with the item-specific description
                    descriptionArea.setText(description);
                }
            });
            inventoryPane.getChildren().add(itemIcon);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
