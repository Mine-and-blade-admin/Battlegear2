Mine and Blade : Battlegear - 2
===============================
	
Minecraft version: 1.6.2  
Forge Version: 1.6.2-9.10.1.871 (nightly at the time of writing)  

Note from nerd-boy: Although the source code version will work for this version of Forges, there is still currently an issue with the ASM. I will look into this ass soon as I have some free time, however I am super busy right now. For the moment all users should use the previous version of the mod with minecraft forge 9.10.1.849 or lower.  

Source is updated for Minecraft 1.6.2

This is the second iteration of Mine & Blade: Battlegear, the popular dual-wielding and combat mod for Minecraft.
The mod will have a heavy dependency on the use of ASM (coremod) to edit the bytecode of the minecraft base classess. Currently this feature is not avaliable.

What does and doesn't work
--------------------------
**What works**
* Dual wielding of weapons and fists
* Switching between normal and battlemode with the 'R' Key
* Attacking entities with both the offhand and mainhand weapons
* Rendering of dual wielding in 1st person view
* 3rd person renderering (with sheaths)
* Swinging of offhand items of other players
* Rendering of offhand weapons on other players
* Shift Clicking on the inventory screen
* The Following Weapons
    +Waraxe
    +Mace
    +Spear
    +Dagger
* Shields
    + Shield Blocking
    + Shield Bash
    + Arrows being stuck in Shields
* Quivers
    +Quivers are the "current project" They are not released to the public yet

**Note from nerd-boy:** I am not 100% happy with the way we are syncing items. It works now by sending a packet with all the "battle items" to each player that is viewing a player every 60 ticks. I am not sure if this will be too much or not. Although right now I cannot think of another way to do it that doesn't require more bytecode manipulation. I think we already have enough AccessTransformers planed to keep us busy without adding more that we may in fact not need.

**What does not work**
* Some of themore "advanced" quiver features
    +Rendering on the back
    +Support for multiple types of arrows in the same quiver



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


Compiling the Mod
-----------------
The mod can be compiled using the following ant command
``ant clean compile build``

This will generate a jar file in the battlegear dist folder.
This command should only be ran after the instalation command


Translations
------------
If you can help to update any of the translation files please fork & make a pull request.

We currently have translation for the following languages (please note that many of these will be out of date)
* English US (default)
* English UK (mainly just change armor -> armour)
* English Pirate
* French
* Polish
* Chinese 

Please feel free to add to this list or update any of the current language files




Some Notes for Texture Pack Makers
----------------------------------

Some of the item names will change, sorry they currently have not been finalised.

Some of the rendering capabilities of the mod are a little different, especially concerning the heraldry. The following are a few notes.
* The patterns should only have white. The white section will determine the secondary colour. The primary colour will be the alpha section. Semi transparent sections should also work for better blending.
* The icons should be greyscale and alpha values. They also require at least 1 pixal border around the whole image that should be kept black. This is the reason all of them are 18x18 pixals (16x16 plus a 1px border all around)

