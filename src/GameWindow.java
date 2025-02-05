/*
Author: Charles Carter
Date: 2/4/2025

Description:  This is the main program for an exploration video game.   The player explores
a haunted house trying to find an escape.
 */

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


/**
 * The {@code GameWindow} class represents the main game window and entry point for the Haunted House game.
 * It initializes the game UI, manages rooms, handles user interactions, and updates the game state.
 * This class extends {@link Application} to create a JavaFX-based user interface.
 */
public class GameWindow extends Application {
    //For simplicity in its uses in multiple areas this static variable is public
    public static Scene scene;

    //Data Fields used in the program
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

    /**
     * Initializes and starts the JavaFX application.
     * Sets up the UI elements, event handlers, and game logic.
     *
     * @param primaryStage The primary stage for this JavaFX application.
     */
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


    /**
     * Initializes the descriptions of items when they are discovered by the player.
     * These descriptions are displayed when the player finds specific items in the game.
     */
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

    /**
     * Initializes and sets up the rooms in the game.
     * Each room is defined with its name, description, image path, available exits,
     * and items that can be discovered or required to unlock access.
     */
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
                        "a suffocating silence that you can almost feel in your throat.  " +
                        "\n\tThe room itself is quite beautiful,  a grand staircase goes to the upper floor, a chandelier hangs from " +
                        "the ceiling.  A door on the left goes to a parlor, and a door behind you goes to what " +
                        "seems to be a dining room.   Two paintings hang here on the walls, one of a sad looking " +
                        "young woman, and the other of a woman who seems to be in severe distress.  " +
                        "Her hair is shorn, and her clothing is gray.",
                "file:src/resources/image/FrontHall.png",
                new String[]{"Dining Room", "Parlor", "Upstairs Loft"}, true,
                new String[]{}, new String[]{"Rusty Key"});

        Room parlor = new Room("Parlor",
                "\tYou enter the parlor, this room feels strangely calm, as if whatever evil presence " +
                        "in this house has less of an influence here.",
                "\tThe piano in this room draws all your focus as you look around this room.  A candle " +
                        "eerily burns on top of it, and from the light of the candle you can see some small pedestals " +
                        "that look as if they are to hold something.   On each base a small engraving depicts what should " +
                        "rest on them: the first, an eye, two others depict crystals, and the final shows a rectangular piece of metal. " +
                        "To the side of the piano a chair rests, and you have the distinct impression that something unseen " +
                        "is sitting in it." +
                        "\n\tBehind you a door leads back to the front hall, but part of you doesn't wish to leave this room " +
                        "again.",
                "file:src/resources/image/Parlor.png",
                new String[]{"Front Hall", "Piano"}, true,
                new String[]{}, new String[]{"Gold Key"});

        Room diningRoom = new Room("Dining Room",
                "\tYou enter the dining room.  There is a faint odor in this room that makes you slightly sick to " +
                        "your stomach.",
                "\tYou stand in a dining room, where it seems the spiders have created a tapestry of " +
                        "cobwebs.  Curiously, although the webs are plentiful, none of them seem to have any living " +
                        "residents, although there are plenty of dead ones.  Eerily, like all rooms in this place, " +
                        "the lights are on, and dinnerware is still present on the table, as if whomever last dined " +
                        "here had to leave in a hurry.   Some plates even have desiccated pieces of food on them.   " +
                        "Two clocks tick on the walls, displaying times that don't match, and both seem to be stuck, " +
                        "despite their sound.  One clock is set in a gold picture frame, the other in an ornate wooden housing.   " +
                        "The dining chairs have all been pushed back, away from the table.   " +
                        "\n\tBehind you a doorway leads back to the front hall, and another in the corner leads to a kitchen.  " +
                        "Strangely your own name comes to your mind, very distinctly, as if someone had spoken it in your ear, " +
                        "but without sound.",
                "file:src/resources/image/DiningRoom.png",
                new String[]{"Front Hall", "Kitchen"}, false,
                new String[]{}, new String[]{});

        Room kitchen = new Room("Kitchen",
                "\tAs you enter the kitchen a bright flash of light envelops the room, through the windows," +
                        "where out of the corner of your eye you catch a glimpse of a ray of lightning strike not " +
                        "very far from the house.   Strangely, no sound or familiar thunderclap accompanies it.  ",
                "\tThis room seems to be a little more modern than other parts of the house, with newer-looking " +
                        "appliances, albeit in very poor condition.   The refrigerator in particular, on closer " +
                        "inspection, has strange markings on it, that almost cause pain to look at.   A gaping hole" +
                        "has been punctured into its door.   " +
                        "\n\tBehind you, a doorway goes back to the Dining Room, " +
                        "and you notice a narrow door in the other corner of the room, with some tight stairs " +
                        "leading down into a basement",
                "file:src/resources/image/Kitchen.png",
                new String[]{"Dining Room", "Basement"}, false,
                new String[]{"Red Crystal"}, new String[]{});

        Room basement = new Room("Basement",
                "\tYou enter the small basement room.  It doesn't feel so dreary in here.",
                "\tThis room seems more cheerful than others, with colorful drawings pasted on the walls, " +
                        "and craft materials stored in various containers.   A small furnace with a low flame is burning. " +
                        "\n\tTaking a closer look at the drawings reveals the work of a child, and they are quite charming, " +
                        "except one on the right wall that reminds you of a skull or some grim ghostly face.  " +
                        "At the back of the room behind some shelves, you notice a very old looking door, " +
                        "which you think goes to a wine cellar.   In that dark part of the room it seems some effort was " +
                        "made to keep this particular door from easy view.",
                "file:src/resources/image/Basement.png",
                new String[]{"Kitchen", "Wine Cellar"}, false,
                new String[]{}, new String[]{});

        Room wineCellar = new Room("Wine Cellar",
                "\tYou enter the wine cellar.  Dust and cobwebs are everywhere and dirty your clothes " +
                        "no matter how hard you try to avoid it.",
                "\tLooking around the wine cellar, the first thing you notice is how much dust there is, " +
                        "more so than other areas of the house.   It feels as if you are the first to enter this room " +
                        "in a very, very long time.  As you look around you notice the casks, and many full bottles of " +
                        "wine, some of which seem from very old years.  The room itself seems frightening, dark shadows " +
                        "play in the light and give you a sense that something could strike out at you at any moment.",
                "file:src/resources/image/WineCellar.png",
                new String[]{"Basement"}, true,
                new String[]{"Infernal Metal"}, new String[]{"Silver Key"});

        Room upstairs = new Room("Upstairs Loft",
                "\tYou enter an upstairs loft.  The room is dark and poorly lit.",
                "\tYou survey the room, which features most noticeably, several paintings that seem to glare " +
                        "at you with empty eyes.  Two burning candles sit side-by-side on a table, and cobwebs and dust " +
                        "seem to cover almost every surface in the room.   Looking to the back of the room you see two " +
                        "hallways, one leads to a master bedroom, and the other leads to two other rooms, " +
                        "which look to be a guest bedroom and some servant's quarters.  Behind you stair lead back to " +
                        "the front hall.",
                "file:src/resources/image/Upstairs.png",
                new String[]{"Front Hall", "Master Bedroom", "Guest Bedroom", "Servants Quarters"}, false,
                new String[]{}, new String[]{});

        Room masterBedroom = new Room("Master Bedroom",
                "\tYou enter the master bedroom, moonlight shines through an open window. ",
                "\tYou look around the master bedroom and are surprised at its poor condition, even " +
                        "after seeing other areas of this house.  The bedsheets of the large bed are tattered and " +
                        "torn, and in the corner the fabric of a draped area looks to be torn to shreds.  " +
                        "The walls have great patches of decay all over them, and torn pieces of wallpaper hang " +
                        "from the ceiling.  \n\tWalking in the room is treacherous due to missing floorboards.  " +
                        "Disturbingly, two skulls casually rest on twin dressers with mirrors.   They look real, and " +
                        "placed with intent.   You have no desire to look closer to see if they are actually authentic.   " +
                        "\n\tBehind you, a door leads back to the upstairs loft, and to your right a doorway goes through " +
                        "to a den or study area.  Another door goes to a small bathroom.",
                "file:src/resources/image/MasterBedroom.png",
                new String[]{"Library", "Upstairs Loft", "Small Washroom"}, true,
                new String[]{}, new String[]{"Gold Coin"});

        Room libraryRoom = new Room("Library",
                "\tYou enter the study.  It is amply stocked with all kinds of literature.",
                "\tAs you look around the study, you notice how well-kept it looks.  Quite the difference " +
                        "from the bedroom.  Bookcases adorn the room and in the center is a desk, with an open journal " +
                        "resting on it, the pages are all blank, although you can see the imprints as if something had " +
                        "written on the pages, yet not transferred any ink.  A candle rests on the desk as if someone " +
                        "had been recently here.  Many other books are scattered and perusing them reveals them all to " +
                        "be in neither a language or alphabet you can recognize.  Some pictures on the wall seem to be " +
                        "vintage anatomy sketches of the human face and an old photograph of a cadaver likely from an " +
                        "archeological site.  A globe of the Earth sits in a spinning display but the country borders seem " +
                        "to be from a much older time period.",
                "file:src/resources/image/LibraryRoom.png",
                new String[]{"Master Bedroom"}, false,
                new String[]{"Gold Key"}, new String[]{});

        Room washroom = new Room("Small Washroom",
                "\tYou enter the small bathroom, the air feels heavy here.",
                "\tA presence can be felt in this room.  You can feel anger emanating from it.   It seems " +
                        "especially displeased at you being in this room in particular.  You notice a foul black liquid " +
                        "in a bathtub that has also spilled onto the floor.   The stench is nauseating and you are surprised " +
                        "you could not smell it from the bedroom.  You can hear faint whispering echoing off of the tile " +
                        "and you notice glyphs and drawings on the walls.   The whispers seem to ooze from them.  " +
                        "Patches of black mold have grown in many places on the wall.",
                "file:src/resources/image/Washroom.png",
                new String[]{"Master Bedroom"}, false,
                new String[]{"Glass Eye"}, new String[]{});

        Room guestBedroom = new Room("Guest Bedroom",
                "\tYou enter the guest bedroom, it is not very tidy and their are scuffs on the floor with " +
                        "less dust than other parts of the room.",
                "\tThe guest bedroom is dimly lit and in disarray.  It seems as if someone had rummaged through " +
                        "the chest of drawers hastily looking for something.  Some dried mud covers parts of the floor.  " +
                        "A small lone rocking chair sits in the middle of the room, and seems to sway when you are not " +
                        "directly looking at it.  Above the chair hangs a strange wire sculpture in the form of a wine-glass.  " +
                        "In the back of the room you see a door at the depths of a small closet, it looks like there is a " +
                        "stairway that goes up behind it.",
                "file:src/resources/image/GuestBedroom.png",
                new String[]{"Closet Stairway", "Upstairs Loft"}, false,
                new String[]{}, new String[]{});

        Room stairway = new Room("Closet Stairway",
                "\tYou enter the narrow stairway, the floorboards creak beneath you.",
                "\tYou survey the narrow stairway and you see that it leads to an attic.  At the top of the " +
                        "stairs there seems to be a figure standing there, but it disappears from view when you try to " +
                        "focus on it.  Very bright moonlight comes from the doorway.  The stairs themselves seem old " +
                        "and precarious, you make a mental note to be careful if you decide to climb them.",
                "file:src/resources/image/Stairway.png",
                new String[]{"Attic", "Guest Bedroom"}, false,
                new String[]{}, new String[]{});

        Room attic = new Room("Attic",
                "\tYou enter the attic.   The light from an odd window is almost blinding.",
                "\tThe ceiling is low in the attic and it is difficult to move around.  The most distinct " +
                        "part of this room is the window, which at first looked as if the moon impossibly filled it " +
                        "completely, but looking closer reveals it to be a very detailed frosted glass window with the " +
                        "surface of the moon on it.   It seems to glow with more light than physically possible for the " +
                        "real moon to provide behind it.  Several wooden chests and boxes contain old items like bottles " +
                        "and empty boxes of vintage detergent.   One chest in the center of the room is so heavy it can't " +
                        "be lifted at all, and it does not seem to be locked.",
                "file:src/resources/image/Attic.png",
                new String[]{"Closet Stairway"}, false,
                new String[]{"Gold Coin"}, new String[]{});

        Room servantRoom = new Room("Servants Quarters",
                "\tYou enter the servant's quarters.   It is eerily quiet.",
                "\tThe servant's quarters seem bare and poorly furnished.  Huge cobwebs cover some of the " +
                        "corners of the room.   A small lit lamp sits on a nightstand, and you wonder how long it has " +
                        "been turned on.  The room seems very drab compared to other places in the house, and not much " +
                        "catches your interest until you see some grotesque totems pinned to the wall in the corner of " +
                        "the room.   The longer you stare at them the more you feel like some dark voice is piercing " +
                        "your thoughts.  You decide not to look at the totems any further.",
                "file:src/resources/image/ServantBedroom.png",
                new String[]{"Upstairs Loft"}, false,
                new String[]{"Silver Key"}, new String[]{});


        //This is the final area of the game, after the player has collected all the necessary items
        Room piano = new Room("Piano",
                "\tAmazingly your surroundings change from standing before the piano to a large and " +
                        "grim hallway with many doors.  Something tells you that you are almost home.",
                "\tAs you look at the features of the hallway, you notice that all the doors on the " +
                        "sides seem to beckon you with scornful voices to open them.   Something tells you that " +
                        "these doors will only lead to new nightmares.   At the end of the hall a small and simple " +
                        "door quietly waits to be opened.   No feeling of dread emanates from it, but rather a calm " +
                        "and a peaceful sense that this door will help you escape.   You decide this is the only door " +
                        "worth opening.",
                "file:src/resources/image/Hallway.png",
                new String[]{"Small Door"}, true,
                new String[]{}, new String[]{"Red Crystal", "Green Crystal", "Infernal Metal", "Glass Eye"});


        //The ending screen of the game.
        Room smallDoor = new Room("Small Door",
                "You open the small door at the end of the strange hallway and find that your " +
                        "memories are returning, and whatever force that kept you in the nightmare has finally " +
                        "released you.   Whatever that realm was, perhaps you'll never know. " +
                        "\n\n(Congrats! You've reached the end of the game!)",
                "You open the small door at the end of the strange hallway and find that your " +
                        "memories are returning, and whatever force that kept you in the nightmare has finally " +
                        "released you.   Whatever that realm was, perhaps you'll never know. " +
                        "\n\n(Congrats! You've reached the end of the game!)",
                "file:src/resources/image/EndScreen.png",
                new String[]{}, false,
                new String[]{}, new String[]{});

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
        rooms.put("Small Door", smallDoor);
    }

    /**
     * Updates the game state when the player moves to a new room.
     * Handles room locking logic, room descriptions, and UI updates.
     *
     * @param roomName The name of the room to update to.
     */
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
                    descriptionArea.setText("\tYou drop the key into the strange locking mechanism of the master " +
                            "bedroom door.   You hear a click and the door is now open.   There seems to be no " +
                            "way to get the coin back." +
                            "\n\n(Click to continue...)");
                } else if (roomName.equalsIgnoreCase("Wine Cellar")) {
                    descriptionArea.setText("\tYou unlock the door to the wine cellar with the silver key.  As " +
                            "soon as the door is open the key shatters, nearly injuring you.   The pieces scatter " +
                            "as if there were a great wind blowing them away." +
                            "\n\n(Click to continue...)");
                } else if (roomName.equalsIgnoreCase("Parlor")) {
                    descriptionArea.setText("\tYou unlock the door to the parlor with the gold key. " +
                            "A bright flash blinds you for a moment and the beautiful key is gone in an instant." +
                            "\n\n(Click to continue...)");
                } else if (roomName.equalsIgnoreCase("Piano")) {
                    descriptionArea.setText("\tYou instinctively place the glass eye, the two crystals, and cursed ingot " +
                            "of metal on the places indicated on the piano.  They seem to catch fire in a cold blue flame.  " +
                            "Suddenly the piano springs to life, if played by invisible hands.  " +
                            "\n\tThe most intense yet " +
                            "beautiful song is played that makes your heart feel like it is about to leap from your " +
                            "throat in suspense.   Suddenly the walls of the room begin to melt away..." +
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
                    descriptionArea.setText("\tTo your frustration, the front door of the mansion is locked, perhaps " +
                            "the key could be close-by?");
                    return;
                } else if (roomName.equalsIgnoreCase("Master Bedroom")) {
                    descriptionArea.setText("\tA strange lock keeps you from entering the master bedroom, it looks like " +
                            "a mechanism that is meant to receive a coin, as if one had to pay to unlock the door.");
                    return;
                } else if (roomName.equalsIgnoreCase("Wine Cellar")) {
                    descriptionArea.setText("\tThe door to the wine cellar is locked.   The door has scratches on it, as " +
                            "if someone had tried to claw their way in.   Perhaps the key is elsewhere in the house.");
                    return;
                } else if (roomName.equalsIgnoreCase("Parlor")) {
                    descriptionArea.setText("\tThe door to the parlor is locked.   As you approach the door you can " +
                            "almost hear music playing.   Something draws you to this room, something important is behind " +
                            "this door.");
                    return;
                } else if (roomName.equalsIgnoreCase("Piano")) {
                    descriptionArea.setText("\tThe piano will not play when you strike the keys with your fingers.  A " +
                            "thought in the back of your head tells you that the pedestals on top of it are required " +
                            "for it to work.");
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


    /**
     * Handles the transition of unlocking a locked room and updating the game state accordingly.
     *
     * @param room The {@link Room} that has been unlocked.
     */
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
    /**
     * Updates the context menu with the available exits from the current room.
     *
     * @param room The current {@link Room} whose exits should be displayed in the context menu.
     */
    private void updateContextMenuExits(Room room) {
        contextMenu.getItems().removeIf(item -> item.getText().startsWith("Go to ")); // Remove old exits

        for (String exit : room.getExits()) {
            MenuItem exitItem = new MenuItem("Go to " + exit);
            exitItem.setOnAction(e -> updateRoom(exit));
            contextMenu.getItems().add(exitItem);
        }
    }

    /**
     * Displays the description of the current room when the player chooses to "Look Around".
     */
    private void handleLookAction() {
        if (currentRoom != null) {
            descriptionArea.setText(currentRoom.getLookDescription());
        }
    }

    /**
     * Handles the player's action of searching a room for items.
     * If items are found, they are added to the player's inventory.
     */
    private void handleSearchAction() {
        if (currentRoom == null) return;

        List<String> items = new ArrayList<>(currentRoom.getItems());
        if (items.isEmpty()) {
            descriptionArea.setText("\tYou search the room diligently but find nothing of importance seems " +
                    "to be here.");
            return;
            //I think in the future I might add custom messages here for rooms that do not contain items.
        }

        StringBuilder foundItemsDescription = new StringBuilder();
        for (String item : items) {
            if (!foundItems.contains(item)) { // Only show discovery text once
                foundItems.add(item);
                //Custom discovery messages or a default message are called here and assigned to a variable.
                String discoveryMessage = itemDiscoveryDescriptions.getOrDefault(item, "You found " + item + "!");
                foundItemsDescription.append(discoveryMessage).append("\n");
            }
            inventory.add(item);
        }

        currentRoom.getItems().clear();
        //Display the discovery message, (which can be a combination of more than one.)
        descriptionArea.setText(foundItemsDescription.toString().trim());
        updateInventoryUI();
    }

    /**
     * Updates the inventory UI to reflect the items the player has collected.
     * Displays item icons and allows players to inspect them.
     */
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
                        description = "\tThe small green crystal glows faintly in your hand. It hums with a strange energy, " +
                                "as if reacting to your presence.  Touching it brings feelings of happiness mixed " +
                                "with despair.  It has been cut quite distinctly and almost looks like it would be " +
                                "very valuable in some other place besides this nightmare.";
                    } else if (item.equals("Rusty Key")) {
                        description = "\tA small, rusty key. It looks fragile. It must be for the front door. ";
                    } else if (item.equals("Red Crystal")) {
                        description = "\tThe red crystal causes feelings of anger when you touch it.   As if you'd been " +
                                "dealt a great injustice.   It is smooth and beautiful, however, and its color reminds " +
                                "you of blood.";
                    } else if (item.equals("Infernal Metal")) {
                        description = "\tThe infernal metal ingot is cold to the touch on the black parts of the metal, and " +
                                "is piping hot on the veins that glow like embers.   Screams seem to fill your mind when " +
                                "you grasp it, like the wailing of the damned.";
                    } else if (item.equals("Glass Eye")) {
                        description = "\tThe glass eye gives you a sense of unease when you inspect it.  When meeting its " +
                                "gaze it distinctly feels as if something is looking back at you, as if it were a real " +
                                "eye.  Its green iris seems sickly and diseased.";
                    } else if (item.equals("Gold Coin")) {
                        description = "\tThe gold coin shines as if it were new.  A kingly face is on one side, and a small " +
                                "inscription on the other reads:  'Pay your respects to the master of the house.'";
                    } else if (item.equals("Silver Key")) {
                        description = "\tThe silver key is tarnished but still looks valuable.  It has a bunch " +
                                "of grapes finely engraved into its handle.";
                    } else if (item.equals("Gold Key")) {
                        description = "\tThe gold key is flat and brilliantly reflective, it looks like it could bring " +
                                "quite a sum of money were it to be sold.  One one side of its handle you see a musical note " +
                                "and a label that reads 'Parlor.'   On the other side it reads: 'Escape.' ";
                    } else {
                        // Default fallback description if no specific one is provided
                        description = "A mysterious item. You wonder what it could be used for. ";
                    }

                    // Update the description area with the item-specific description
                    descriptionArea.setText(description);
                }
            });
            inventoryPane.getChildren().add(itemIcon);
        }
    }

    /**
     * The main method to launch the JavaFX application.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
