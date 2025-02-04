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
        itemDiscoveryDescriptions.put("Rusty Key",
                "You search the area and notice a small key under the rug, you pick it up.");
        itemDiscoveryDescriptions.put("Green Crystal",
                "\nYou also notice a small green crystal.  It looks important so you take it with you. As " +
                        "you touch it distant memories of happiness touch your mind, but then are eclipsed " +
                        "by a sense of dread.");
        itemDiscoveryDescriptions.put("Red Crystal",
                "\tAs you search the kitchen, you are suddenly drawn to the sink.   A red glow can be " +
                        "seen beneath its murky and foul water.  You apprehensively submerse your hand into the " +
                        "ooze which burns your skin slightly.   You grasp the source of the glow and pull it out; a " +
                        "dark red crystal.");
        itemDiscoveryDescriptions.put("Infernal Metal",
                "\tAs you search around the wine cellar, you notice a warmth coming from one of the racks, along " +
                        "with an acrid sulphurous smell.  An ingot of dark metal with smoldering veins of embers " +
                        "has been hidden behind a bottle.   As you grab it your surroundings become blurred " +
                        "and your vision darkens.  Within moments an almost audible scream fills your mind, " +
                        "although it doesn't seem human.  And before you can panic, your mind fixates unwillingly " +
                        "on a space of the floor, as if it whatever invisible presence here wants you to see " +
                        "what had happened there.  The moment then subsides, and you place the cursed ingot " +
                        "into your inventory.");
        itemDiscoveryDescriptions.put("Glass Eye",
                "\tYou search diligently in the small bathroom, looking in the medicine cabinet and corners of the " +
                        "room for something the feels like it could be important.   All this time you avoid the bath tub " +
                        "full of black liquid, but a part of you knows that in its depths is what you are looking for.  " +
                        "Finally after some mental preparation you dip your hand into the tub.  The liquid is " +
                        "frightfully cold and you soon feel numb.   Just when you think you can't endure any more " +
                        "of the freezing temperature your hand grasps a small sphere.   You quickly pull it out to " +
                        "see a small glass eye with a green iris, which you place with the things your carrying.");
        itemDiscoveryDescriptions.put("Silver Key",
                "\tAs you search the room you hear a faint knocking coming from the inside of a large armoire " +
                        "in the corner of the room.   Reluctantly you open it, finding it empty besides some dusty, " +
                        "poorly-folded bedlinens, and the knocking stops.  Suddenly, behind you, a loud sound " +
                        "startles you and when you look for the source of the noise you see a small silver key " +
                        "in the middle of the room.   It trembles slightly as if it just been dropped to the floor.");
        itemDiscoveryDescriptions.put("Gold Coin",
                "\tWhen you search this room you are immediately drawn to the ornate-looking closed chest resting " +
                        "in the middle of the floor.   It opens easily enough and you see an single gold coin inside " +
                        "it.   When you grab the coin it feels as if it weighs sixty pounds and you struggle to lift " +
                        "it, and then suddenly the heaviness is gone and you fall backward.  The coin seems as light " +
                        "as any normal coin now, and you put it with your things.");
        itemDiscoveryDescriptions.put("Gold Key",
                "\tYou carefully search the library looking for something that could be important.   You notice a book " +
                        "on one of the shelves that seems ancient.   When you take a closer look, the book has no title " +
                        "but simply a human skull embossed into the leather.   You notice a red ribbon which seems to " +
                        "to be a bookmark, but when you turn to its page you notice it is attached to a flat gold key.  " +
                        "Only then do you notice in the book the diagrams of some barbaric ritual written in language you can't " +
                        "understand.   The words seem to scream at you and you quickly shut the book.   You then place " +
                        "the key with the items you are carrying.");
    }

    //The rooms of the mansion are defined in this method
    private void initializeRooms() {
        rooms = new HashMap<>();

        //A template room can be seen at the bottom of this list for reference

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
                new String[]{"Front Hall"}, false,
                new String[]{"Rusty Key", "Green Crystal"}, new String[]{});

        Room frontHall = new Room("Front Hall",
                "\tYou step into the grand front hall of the mansion. Dust swirls in the dim light...  " +
                        "something is very wrong here, but you can't quite tell what exactly.  The room is vacant, " +
                        "but it feels like you're being watched.",
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
                new String[]{"Dining Room", "Parlor", "Upstairs Loft"}, true,
                new String[]{}, new String[]{"Rusty Key"});

        Room parlor = new Room("Parlor",
                "Placeholder",
                "Placeholder",
                "file:src/resources/image/Parlor.png",
                new String[]{"Front Hall", "Piano"}, true,
                new String[]{}, new String[]{"Gold Key"});

        Room diningRoom = new Room("Dining Room",
                "Placeholder",
                "Placeholder",
                "file:src/resources/image/DiningRoom.png",
                new String[]{"Front Hall", "Kitchen"}, false,
                new String[]{}, new String[]{});

        Room kitchen = new Room("Kitchen",
                "Placeholder",
                "Placeholder",
                "file:src/resources/image/Kitchen.png",
                new String[]{"Dining Room", "Basement"}, false,
                new String[]{"Red Crystal"}, new String[]{});

        Room basement = new Room("Basement",
                "Placeholder",
                "Placeholder",
                "file:src/resources/image/Basement.png",
                new String[]{"Kitchen", "Wine Cellar"}, false,
                new String[]{}, new String[]{});

        Room wineCellar = new Room("Wine Cellar",
                "Placeholder",
                "Placeholder",
                "file:src/resources/image/WineCellar.png",
                new String[]{"Basement"}, true,
                new String[]{"Infernal Metal"}, new String[]{"Silver Key"});

        Room upstairs = new Room("Upstairs Loft",
                "Placeholder",
                "Placeholder",
                "file:src/resources/image/Upstairs.png",
                new String[]{"Front Hall", "Master Bedroom", "Guest Bedroom", "Servants Quarters"}, false,
                new String[]{}, new String[]{});

        Room masterBedroom = new Room("Master Bedroom",
                "Placeholder",
                "Placeholder",
                "file:src/resources/image/MasterBedroom.png",
                new String[]{"Library", "Upstairs Loft", "Small Washroom"}, true,
                new String[]{}, new String[]{"Gold Coin"});

        Room libraryRoom = new Room("Library",
                "Placeholder",
                "Placeholder",
                "file:src/resources/image/LibraryRoom.png",
                new String[]{"Master Bedroom"}, false,
                new String[]{"Gold Key"}, new String[]{});

        Room washroom = new Room("Small Washroom",
                "Placeholder",
                "Placeholder",
                "file:src/resources/image/Washroom.png",
                new String[]{"Master Bedroom"}, false,
                new String[]{"Glass Eye"}, new String[]{});

        Room guestBedroom = new Room("Guest Bedroom",
                "Placeholder",
                "Placeholder",
                "file:src/resources/image/GuestBedroom.png",
                new String[]{"Closet Stairway", "Upstairs Loft"}, false,
                new String[]{}, new String[]{});

        Room stairway = new Room("Closet Stairway",
                "Placeholder",
                "Placeholder",
                "file:src/resources/image/Stairway.png",
                new String[]{"Attic", "Guest Bedroom"}, false,
                new String[]{}, new String[]{});

        Room attic = new Room("Attic",
                "Placeholder",
                "Placeholder",
                "file:src/resources/image/Attic.png",
                new String[]{"Closet Stairway"}, false,
                new String[]{"Gold Coin"}, new String[]{});

        Room servantRoom = new Room("Servants Quarters",
                "Placeholder",
                "Placeholder",
                "file:src/resources/image/ServantBedroom.png",
                new String[]{"Upstairs Loft"}, false,
                new String[]{"Silver Key"}, new String[]{});


        //This is the final area of the game, after the player has collected all the necessary items
        Room piano = new Room("Piano",
                "Placeholder",
                "Placeholder",
                "file:src/resources/image/Hallway.png",
                new String[]{"Small Door"}, true,
                new String[]{}, new String[]{"Red Crystal", "Green Crystal", "Infernal Metal", "Glass Eye"});


        //Blank room for following a pattern to add more rooms:
        Room template = new Room("Placeholder",
                "Placeholder",
                "Placeholder",
                "file:src/resources/image/temp.png",
                //String Array for Exits, boolean for toggling a room as locked
                new String[]{}, false,
                //String Array for discoverable items, and String Array for items required to open the room, if locked
                new String[]{}, new String[]{});


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

                //Custom unlock messages are here
                if (roomName.equalsIgnoreCase("Front Hall")) {
                    descriptionArea.setText("\tThe rusty key you found under the mat fits into the keyhole " +
                            "of the foreboding front door of the house. The door unlocks and you step inside. " +
                            "To your astonishment, the key disintegrates, as if made from ash, and " +
                            "completely disappears. " +
                            "\n\n(Click to continue...)");
                } else if (roomName.equalsIgnoreCase("Master Bedroom")) {
                    descriptionArea.setText("You drop the key into the strange locking mechanism of the master " +
                            "bedroom door.   You hear a click and the door is now open.   There seems to be no " +
                            "way to get the coin back." +
                            "\n\n(Click to continue...)");
                } else if (roomName.equalsIgnoreCase("Wine Cellar")) {
                    descriptionArea.setText("You unlock the door to the wine cellar with the silver key.  As " +
                            "soon as the door is open the key shatters, nearly injuring you.   The pieces scatter " +
                            "as if there were a great wind blowing them away." +
                            "\n\n(Click to continue...)");
                } else if (roomName.equalsIgnoreCase("Parlor")) {
                    descriptionArea.setText("You unlock the door to the parlor with the gold key. " +
                            "A bright flash blinds you for a moment and the beautiful key is gone in an instant." +
                            "\n\n(Click to continue...)");
                } else {  //Generic unlock message
                    descriptionArea.setText("\nYou used " + String.join(", ", requiredItems) + " to unlock the " + roomName + "!" +
                            "(\nClick to continue..)");
                }

                scene.setOnMouseClicked(e -> {
                    scene.setOnMouseClicked(null);
                    updateRoomAfterUnlock(room);
                    menuBar.setDisable(false);
                });

                return;
            } else {
                //Locked rooms have custom lock descriptions here:
                if (roomName.equalsIgnoreCase("Front Hall")) {
                    descriptionArea.setText("To your frustration, the front door of the mansion is locked, perhaps " +
                            "the key could be close-by?");
                    return;
                } else if (roomName.equalsIgnoreCase("Master Bedroom")) {
                    descriptionArea.setText("A strange lock keeps you from entering the master bedroom, it looks like " +
                            "a mechanism that is meant receive a coin, as if one had to pay to unlock the door.");
                    return;
                } else if (roomName.equalsIgnoreCase("Wine Cellar")) {
                    descriptionArea.setText("The door to the wine cellar is locked.   The door has scratches on it, as " +
                            "if someone had tried to claw their way in.   Perhaps the key is elsewhere in the house.");
                    return;
                } else if (roomName.equalsIgnoreCase("Parlor")) {
                    descriptionArea.setText("The door to the parlor is locked.   As you approach the door you can " +
                            "almost hear music playing.   Something draws you to this room, something important is behind " +
                            "this door.");
                    return;
                } else {
                    //Generic locked message
                    descriptionArea.setText("The door to " + roomName + " is locked. You need: " + requiredItems);
                    return;
                }
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
                        description = "The small green crystal glows faintly in your hand. It hums with a strange energy, " +
                                "as if reacting to your presence.  Touching it brings feelings of happiness mixed " +
                                "with despair.  It has been cut quite distinctly and almost looks like it would be " +
                                "very valuable in some other place besides this nightmare.";
                    } else if (item.equals("Rusty Key")) {
                        description = "A small, rusty key. It looks fragile. It must be for the front door.";
                    } else if (item.equals("Red Crystal")) {
                        description = "The red crystal causes feelings of anger when you touch it.   As if you'd been " +
                                "dealt a great injustice.   It is smooth and beautiful, however, and its color reminds " +
                                "you of blood.";
                    } else if (item.equals("Infernal Metal")) {
                        description = "The infernal metal ingot is cold to the touch on the black parts of the metal, and " +
                                "is piping hot on the veins that glow like embers.   Screams seem to fill your mind when " +
                                "you grasp it, like the wailing of the damned.";
                    } else if (item.equals("Glass Eye")) {
                        description = "The glass eye gives you a sense of unease when you inspect it.  When meeting its " +
                                "gaze it distinctly feels as if something is looking back at you, as if it were a real " +
                                "eye.  Its green iris seems sickly and diseased.";
                    } else if (item.equals("Gold Coin")) {
                        description = "The gold coin shines as if it were new.  A kingly face is on one side, and a small " +
                                "inscription on the other reads:  'Pay your respects to the master of the house.'";
                    } else if (item.equals("Silver Key")) {
                        description = "The silver key is tarnished but still looks valuable.  It has a bunch " +
                                "of grapes finely engraved into its handle.";
                    } else if (item.equals("Gold Key")) {
                        description = "The gold key is flat and brilliantly reflective, it looks like it could bring " +
                                "quite a sum of money were it to be sold.  One one side of its handle you see a musical note " +
                                "and a label that reads 'Parlor.'   On the other side it reads: 'Escape.'";
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
