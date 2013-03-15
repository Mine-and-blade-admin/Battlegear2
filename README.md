Mine and Blade : Battlegear - 2
===============================

Source is updated for Minecraft 1.5

This is the second iteration of Mine & Blade: Battlegear, the popular dual-wielding and combat mod for Minecraft.
The mod will have a heavy dependency on the use of ASM (coremod) to edit the bytecode of the minecraft base classess. Currently this feature is not avaliable.

What does and doesn't work
--------------------------
**What works**
* Dual wielding of weapons and fists
* Switching between normal and battlemode with the 'R' Key
* Attacking entities with both the offhand and mainhand weapons
* Rendering of dual wielding in 1st person view
* 3rd person renderering
* Initial work on the equip screen
* Inital work on an API to allow other modders to define dual wielding weapons
* Swinging of offhand items of other players
* Rendering of offhand weapons on other players

**Note from nerd-boy:** I am not 100% happy with the way we are syncing items. It works now by sending a packet with all the "battle items" to each player that is viewing a player every 5 ticks. I am not sure if this will be too much or not. Although right now I cannot think of another way to do it that doesn't require more bytecode manipulation. I think we already have enough AccessTransformers planed to keep us busy without adding more that we may in fact not need.

**What does not work**
* ASM capabilities
* Shift clicking on the inventory screen (It also needs to be tested on both ssp & smp)
* Tools breaking blocks when held in the left hand (probably will not make it into the game)
* Rendering of sheathed weapons

Instalation
-----------
The files contained within the repository must be placed inside a minecraft forge-universal src installation.
Due to potential legal issues of re-distributing Majong .java files, all base class edits are distributed as .java.patch files.

The full battlegear source can be obtained by running

``./battlegear-install.sh``
on a unix enviroment or
``./battlegear-install.cmd``
on a windows environment

The patch source can be re-obtained by issuing

`` ./battlegear-getsrc.sh``
on a unix enviroment or
``./battlegear-getsrc.cmd``
on a windows environment

Remember to set the exeuction bit to true on the unix enviroment

When the project is in a more mature state I will also include another script (likely an ant script) to retrieve the fully compiled and packaged code

