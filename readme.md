# Simple GUI Library

This is a simple GUI library for Bukkit plugins. It allows you to create GUIs with ease.

## Usage

To use this library, you need to add it as a dependency in your pom.xml file:

```xml<dependency>    <groupId>me.legofreak107</groupId>    <artifactId>GuiLibrary</artifactId>    <version>1.0.0</version></dependency>```Then, you need to initialize the library in your main class:

```javapublic class MyPlugin extends JavaPlugin {    @Override    public void onEnable() {        GuiLibrary.init(this);    }}```Now, you can create a new GUI by inheriting from the `GuiMenu` class:

```javapublic class MyGui extends GuiMenu {    private int layout[][] = {            {1, 1, 1, 0, 0, 0, 3, 3, 3},            {1, 1, 1, 0, 2, 0, 3, 3, 3},            {1, 1, 1, 0, 0, 0, 3, 3, 3},    };    public MyGui() {        HashMap<Integer, GuiItem> items = new HashMap<>();        GuiItem acceptItem = new GuiItem(                new ItemStackBuilder(Material.LIME_WOOL).setName("§aAccept").build(),                event -> {                    event.getWhoClicked().sendMessage("You clicked the accept button!");                }        );        GuiItem declineItem = new GuiItem(                new ItemStackBuilder(Material.RED_WOOL).setName("§cDecline").build(),                event -> {                    event.getWhoClicked().sendMessage("You clicked the decline button!");                }        );        GuiItem infoItem = new GuiItem(                new ItemStackBuilder(Material.PAPER).setName("§6Info").build(),                null        );        items.put(1, acceptItem);        items.put(3, declineItem);        items.put(2, infoItem);        try {            init(layout, items, Component.text("My GUI"), closeEvent -> {                closeEvent.getPlayer().sendMessage("You closed the GUI!");            });        } catch (InvalidGuiLayoutException e) {            e.printStackTrace();        }    }}```You can then open the GUI by calling the `open()` method:

```javaMyGui gui = new MyGui();gui.open(player);```
