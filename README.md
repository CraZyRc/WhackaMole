# WhackaMole![Premium transparent](https://user-images.githubusercontent.com/79481250/203549518-343a2559-d9a3-4f76-a760-dab4db9805cb.png)

Whack-a-Mole is a mini game with a million customisable options.
The goal for the game is to whack some moles and by doing so gaining points which will be transferred to your currency account (whether this is Vault or a Scoreboard objective).
This plugin took a month to make so if you do enjoy the plugin, let me know!
This plugin took me a while to create so i'd greatly appreciate it if you were to support me by [donating](https://ko-fi.com/crazyrc).

## Bugs or Feature requests?
Please visit the [Issues](https://github.com/CraZyRc/WhackaMole/issues) tab to request new features or to report bugs.
Whenerver submitting a new request, please be as specific as possible. 
Have a bug? Then please submit your error log and describe how you got that bug/error to happen.
Want a new feature? Then please try and be specific in what you like, descrive how you see this feature happening.


## Features:
 * Editor for points gained per mole hit.
 * Possibility to switch between using Vault or a Scoreboard.
 * Enable/Disable Jackpot spawns.
 * Decide what the game field is made out of, what block should be used for the moles to spawn on?
 * decide what sound should be played if a mole is to be hit or missed.
 * Customisability for the hammer name and game message (Actionbar).
 * Mole spawning in 8 directions (North, North_east, East, South_east, South, South_west, West, North_west).
 * Choose yourself the speed of a mole, when it spawns, per how many hits the difficulty increases or how often a jackpot spawns (basically everything is   customizable.

## Installation:
To install the plugin, simply drop the latest version (compatible with your Minecraft version) into your plugins folder and start your server.
It is essential to set up the Economy method of the plugin. Otherwise, it won't work.

### Economy setup:
* Deciding whether to use a Scoreboard objective or Vault.
  * SCOREBOARD:
    If your server doesn't use an economy plugin like [EssentialsX](essentialsx.net) then you can use a Scoreboard value to keep track of people their currency.
    to do this, simply set Scoreboard as currency manager in the config file at:
    ```
    Economy: SCOREBOARD
    ```
    When this is done you need to specify which objective manages people their money. This is done like so:
    ```
    Scoreboard Objective: Example
    ```
    And that's it! The points people have won will automatically be added to the objective and be taken when purchasing a reset ticket.

  * VAULT:
    If your server does use an economy plugin (Check the [Vault](https://www.spigotmc.org/resources/vault.34315/) page to see which plugins are supported), then it is best to make use of our Vault compatibility.
    To do so set economy to Vault:
    ```
    Economy: VAULT
    ```
    If this is done you can ignore the objective value since this will be skipped.
When an economy option has been set up, you can start creating games and play around with them.

## Commands:
* /wam Create <Gamename>
  This creates the game (do make sure you stand on a configured grid though).
* /wam Settings <Setting>
  Ingame command to mess around with the game settings.
* /wam Buy
  Command used to buy a ticket (tickets can be used by right-clicking them on top of a game grid).
* /wam Remove
  Deletes a game... kinda self-explanatory
* /wam Reload
  reloads the (game) config. this is handy when you make changes in the files and don't want to do a restart.

This is all you need to know. Have fun playing!


