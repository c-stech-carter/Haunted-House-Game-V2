import javafx.animation.PauseTransition;
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

    private Map<String, Room> rooms;
    private Room currentRoom;
    private ImageView backgroundView;
    private TextArea descriptionArea;
    private Menu exitsMenu;
    private FlowPane inventoryPane;
    private List<String> inventory = new ArrayList<>();
    private Set<String> foundItems = new HashSet<>(); // Tracks items already found
    private Map<String, String> itemDiscoveryDescriptions = new HashMap<>();

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

        MenuBar menuBar = new MenuBar();
        Menu actionsMenu = new Menu("Actions");
        MenuItem searchItem = new MenuItem("Search for Items");
        searchItem.setOnAction(e -> handleSearchAction());
        MenuItem lookItem = new MenuItem("Look Around");
        lookItem.setOnAction(e -> handleLookAction());
        actionsMenu.getItems().addAll(searchItem, lookItem);

        exitsMenu = new Menu("Exits");
        menuBar.getMenus().addAll(actionsMenu, exitsMenu);
        root.setTop(menuBar);

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

        Scene scene = new Scene(root, 1320, 950, Color.DIMGRAY);
        primaryStage.setTitle("Haunted House Game");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private void initializeItemDiscoveryDescriptions() {
        itemDiscoveryDescriptions.put("Rusty Key", "You search the area and notice a small key under the rug, you pick it up.");
        itemDiscoveryDescriptions.put("Green Crystal", "You also notice a small green crystal.  It looks important so you take it with you.");
        itemDiscoveryDescriptions.put("Mysterious Note", "As you rummage through the papers on the table, you find a torn piece of paper with strange symbols.");
    }

    //The rooms of the mansion are defined in this method
    private void initializeRooms() {
        rooms = new HashMap<>();

        Room frontYard = new Room("Front Yard", "The last thing you remember is an intense pain to " +
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

        Room frontHall = new Room("Front Hall", "\tYou step into the grand front hall of the mansion. Dust swirls in the dim light..." +
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

        Room parlor = new Room("Placeholder", "Placeholder",
                "Placeholder",
                "file:src/resources/image/Parlor.png",
                new String[]{"Front Hall", "Piano"}, false, new String[]{}, new String[]{});

        Room diningRoom = new Room("Placeholder", "Placeholder",
                "Placeholder",
                "file:src/resources/image/DiningRoom.png",
                new String[]{"Front Hall", "Kitchen"}, false, new String[]{}, new String[]{});

        Room kitchen = new Room("Kitchen", "Placeholder",
                "Placeholder",
                "file:src/resources/image/Kitchen.png",
                new String[]{"Dining Room", "Basement"}, false, new String[]{}, new String[]{});

        Room basement = new Room("Basement", "Placeholder",
                "Placeholder",
                "file:src/resources/image/Basement.png",
                new String[]{"Kitchen", "Wine Cellar"}, false, new String[]{}, new String[]{});

        Room wineCellar = new Room("Wine Cellar", "Placeholder",
                "Placeholder",
                "file:src/resources/image/WineCellar.png",
                new String[]{"Basement"}, false, new String[]{}, new String[]{});

        Room upstairs = new Room("Upstairs Loft", "Placeholder",
                "Placeholder",
                "file:src/resources/image/Upstairs.png",
                new String[]{"Front Hall", "Master Bedroom", "Guest Bedroom", "Servants Bedroom"}, false, new String[]{}, new String[]{});

        //Blank room for following a pattern to add more rooms:
        Room template = new Room("Placeholder", "Placeholder",
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
                descriptionArea.appendText("\nYou used " + String.join(", ", requiredItems) + " to unlock " + roomName + "!");
                // Delay the actual room update to allow time for message display
                PauseTransition pause = new PauseTransition(Duration.seconds(2));
                pause.setOnFinished(e -> updateRoomAfterUnlock(room));
                pause.play();
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
        currentRoom = room;
        backgroundView.setImage(new Image(room.getImagePath()));
        descriptionArea.setText(room.getInitialDescription());

        exitsMenu.getItems().clear();
        for (String exit : room.getExits()) {
            MenuItem exitItem = new MenuItem(exit);
            exitItem.setOnAction(e -> updateRoom(exit));
            exitsMenu.getItems().add(exitItem);
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
            descriptionArea.setText("You search the room but find nothing of interest.");
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
            inventoryPane.getChildren().add(itemIcon);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
