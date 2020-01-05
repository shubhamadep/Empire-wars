Empire Wars
===========

Empire Wars is a 2D multi-player online battle arena video game. There are
two teams in the game: Red team and the Blue team, and the objective is to
acquire as many
ags as possible in theeld. The 
ags are located at dierent
positions in the gameeld. Each team should at least have two players. The
players start from the opposite sides of theeld, and they can move in all four
directions (Left, Right, Up and Down) and canre bullets at their opponents.
The gameeld has dierent terrains like plants, and bricks. The player has to
go around the walls and the plants and cannot walk through them. Initially,
all the
ags in theeld are inactive (grey). As the player reaches the 
ag, a
timer starts running. The player has to wait for 5 seconds for the
ag to change
color. By making the player wait, we solve the con
ict that might occur when
two players of dierent teamsght for the same 
ag. The
ags once changed to
a team's color can be acquired by the other team. The players canre bullets
at the opponent team players which in turn decreases the player's health. Each
player has three lives and the player is o the game when he loses all the three
lives. A countdown timer keeps running through the game, and the team with
the highest number of
ags when the time elapses is the winner. The game also
ends when all the players in a team lose all lives.

Mechanics and Interactions of the game:

Two teams of at least 2 characters, selected before the match,ght against each
other, with the goal to acquire as many
ags as possible before the others do the
same. Both teams have toght their way through enemy players, and creeps (a
unit controlled by the game's articial intelligence in a multi-player online battle
arena). The maps contain dierent types of terrain like grass and walls which
constrains the player's movement. Upon death, the player may re-spawn back
at their base after a short delay. There is also a tower (AI) for each team, that
shoots outre at the opponent team's players when they get near the 
ags that
are closer to the tower. When attacked by the opponent players, the players
end up in limbo and has to remain idle for a denite amount of time until he
is released. The maps include health pickups and extra life power ups which
appear at random times in random locations.

Visual Entities of the game:

1) Player: The player's are either colored red or blue depending upon the
team they belong to. A health bar along with the name of the player is
displayed above the player. Health is the amount of damage the player
can take before dying. Each player has their own amount of health. Each
section of the health bar above the character's head represent 15 health
points. The health point decreases when attacked by the opponent team's
crepes. Health can be increased by capturing the bananas that can be
found at random locations on the map. The player loses a life when the
health bar becomes empty.

2) The Flags : The flags are initially inactive (grey) when the game starts.
The flag changes to the color of the player's team when the player has
been in contact with the flag for 10 seconds. There is a timer running
on top of the flags that denote how much more longer the player has to
wait for the flag to change its color. The opponent player can shoot at
the players while they are waiting for the flag to change its color, which
will send them to the jail.

3) Bullets : When the player is shot by the enemy player and he dies, he
is sent to limbo and has to remain idle until the time transpires (say 20
seconds). When the players of the same team shoot at each other the
bullets are nullied. The player's bullets can kill the crepes.

4) Towers : The towers are located at both the ends of the maps. The
towers start shootingre at the opponent players when they arrive at a
certain distance from the tower. This makes it difficult for the players
to acquire the flags that are closer to the tower. Instead of shootingre
at the player's directly the tower shoots around the player. The player
has to go around there to survive. Colliding with the re decreases the
player's health points rapidly and the player has to move away from the
re as soon as possible. The towers cannot be destroyed.

5) The crepes : Crepes are the game units which are controlled by the AI.
There will be one crepe per team, and the crepe chooses one opponent
player at random and follows him. The crepes also have health bars above
them and and have only 6 health points. The crepes can be killed by the
players when shot 6 times. Once they are killed they re spawn from their
tower after some delay and continues to follow a player randomly.
 The Walls : There are also some stone blocks that appear in the game
eld and they cannot be destroyed with bullets. The characters have to
find a path around the walls to move past them. The bullets cannot pass
through the walls.

6) The plants : There are green patches of trees in the gameeld which
are similar to the walls.

Apart from these basic entities there are also several power ups involved in
the game. The power ups can appear in random positions of the gameeld and
disappear after 20 seconds. The power ups are:
 Heart : Gives an extra life.
 Banana : Improves the health by 10 points.
 Star : Powerful bullets that can kill the crepes in one hit.
 Cloak : Hides all the flags acquired by the player's team.
 Clock : Freezes all the opponent characters for 5 seconds.
 Shovel : Adds a steel wall around the flags acquired by the player for 20
seconds.
 Get out of Limbo : Acts as a trump card. When having this power
up the player can use it to get out of limbo before the 20 second duration
elapses.
To add more suspense, some power ups are revealed only after they are acquired.
The power ups may not always be beneficial to the player who acquired it. Some
times the power ups can be power downs and they might have a negative effect
on your team. For ex: the clock power up could freeze all the characters in the
player's team instead of freezing the opponent's team.

Technical Showpiece:

 Constrained Movement : The characters in the game cannot move
arbitrarily and they are constrained by the different terrains in the game
eld.
 State-Based Behavior : All the above mentioned power ups are responsible for the state based behaviour of the game.
 Reasonable Pathfinding : The crepes have to take the shortest path
to the player and attack them. Also the tower has to track the player's
position and shootre at them.
 Collision Detection : There are various types of terrains that has to
be handled when the player collides. On colliding with the
flag, the timer
on the top of the
flag has to be updated. Also the collision between the
players and their enemies has to be handled. Sometimes even the bullets
red by the players of the same team collide into one another, however
they will have no effects.
 Networking : Empire Wars will be a multi-player game. A game will be
hosted by one of the players and other players will join the hosted game.
The players will be assigned teams when they join a session.
