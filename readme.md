# Simple GUI Library

This is a simple GUI library for Bukkit plugins. It allows you to create GUIs with ease.

## Usage

To use this library, you need to add it as a dependency in your pom.xml file:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
	    <groupId>com.github.legofreak107</groupId>
	    <artifactId>GuiLibrary</artifactId>
	    <version>1.0.0</version>
	</dependency>
</dependencies>
```

Then, you need to initialize the library in your main class:

```java
public class MyPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        GuiLibrary.init(this);
    }

}
```

Now, you can create a new GUI by inheriting from the `GuiMenu` class:

```java
public class MyGui extends GuiMenu {

    // Make the layout of the GUI, these numbers are placeholders.
    private int layout[][] = {
            {1, 1, 1, 0, 0, 0, 3, 3, 3},
            {1, 1, 1, 0, 2, 0, 3, 3, 3},
            {1, 1, 1, 0, 0, 0, 3, 3, 3},
    };

    public MyGui() {
        HashMap<Integer, GuiItem> items = new HashMap<>();

        // Make the clickable item of the gui. The event -> here is the event that is called when the item is clicked.
        GuiItem acceptItem = new GuiItem(
                // We use the built in ItemStackBuilder to make the item.
                new ItemStackBuilder(Material.LIME_WOOL).setName("§aAccept").build(),
                event -> {
                    // Event is already filtered by the library, so we can just send a message to the player.
                    event.getWhoClicked().sendMessage("You clicked the accept button!");
                }
        );
        
        GuiItem declineItem = new GuiItem(
                new ItemStackBuilder(Material.RED_WOOL).setName("§cDecline").build(),
                event -> {
                    event.getWhoClicked().sendMessage("You clicked the decline button!");
                }
        );

        GuiItem infoItem = new GuiItem(
                new ItemStackBuilder(Material.PAPER).setName("§6Info").build(),
                // As you can see, we don't need to set a click handler for this item.
                null
        );

        
        // Put the items in the hashmap to mask the ids set in the layout.
        items.put(1, acceptItem);
        items.put(3, declineItem);
        items.put(2, infoItem);

        try {
            // Initialize the GUI with the layout, items and a title. the CloseEvent is called when the GUI is closed.
            init(layout, items, Component.text("My GUI"), closeEvent -> {
                closeEvent.getPlayer().sendMessage("You closed the GUI!");
            });
        } catch (InvalidGuiLayoutException e) {
            e.printStackTrace();
        }
    }
}
```

You can then open the GUI by calling the `open()` method:

```java
MyGui gui = new MyGui();
gui.open(player);
```
