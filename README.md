patientia
=======

The ultimate laggy 4X game. The farther your units are from your cities, the slower they are to respond to your commands!

Here's some player vs. AI gameplay:
![player gameplay](https://i.imgur.com/Cd5lOWM.png)

And some AI vs. AI gameplay:

![engaging ai gameplay](https://i.imgur.com/ll8KlKs.png)

![engaging ai gameplay 2](https://i.imgur.com/R5f1y74.png)

![engaging ai gameplay 3](https://i.imgur.com/aSmp7YZ.png)


# Controls/gameplay
Left click to select a unit. Right click to order the unit to move/attack an in-game tile. Right click and drag to move the map around. Use scrolling to change zoom level of the map. 

N is a hotkey for next turn. If you hold down shift, you will be able to queue orders; if you aren't holding shift while you give an order, it will count as an override ("set") order, which deletes all prior orders. Shift queueing orders also works on button-style orders (ex. build mine). 

Soldiers consume 2 food and 1 wealth per turn. Cities generate 1 wealth per turn at the expense of 1 food (this changes to 0.5 wealth for 2 food when you enact population controls). Cities also have a base maintenance fee of 2 wealth. Mines produce 1 minerals per turn and farms produce 1 food per turn. If any resource goes negative then you receive a 50% combat penalty and a 25% wealth penalty. 

The amount of lag between you and a unit is equal to the floor of (the euclidean distance to the nearest city controlled by you divided by 5). If you have X turns of lag, it means that your commands issued now will be registered by the unit after X turns pass, and you will register the knowledge of the terrain and enemy units that your unit possessed X turns ago on every turn.

# Commentary
The AI I've written seems insanely strong at first glance. I've just played the first three matches, and I got absolutely trashed by the AI.

VERY unbalanced with respect to food right now. Food is basically useless, except for building new cities. The AI is basically swamped in food negatives despite building a ton of farms, so the first few cities that it builds with the 1k really count.

Some bugs remain, especially with map vision. These will be fixed.

Still need to add actual menu settings and such, right now everything's hardcoded.


### Direct download (jar file)
See releases section.

## Bugs
Please report all bugs. There are a ton of bugs, especially with map vision (selecting enemy units and such is kind of broken when they're dead or moved away, or similar).

## In progress
- Making the UI not a complete hack (if you have any advice, it would be very appreciated)
- Multiplayer
- Better AI
- More balance and more content (tech, etc.)

Suggestions are appreciated.
