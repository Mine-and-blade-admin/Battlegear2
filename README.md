Battlegear2
===========

This is the second iteration of Mine & Blade: Battlegear, the popular dual-wielding and combat mod for Minecraft.

The mod will have a heavy dependency on the use of ASM (coremod) to edit the bytecode of the minecraft base classess. Currently this feature is not avaliable.

What does and doesn't work
--------------------------
**What works**
* Dual wielding of weapons and fists
* Switching between normal and battlemode with the 'R' Key
* Attacking entities with both the offhand and mainhand weapons
* Rendering of dual wielding in 1st person view

**What does not work**
* ASM capabilities
* 3rd person renderering
* The Battlegear inventory screen - currently 3 pairs of weapon slots appaer to the left of the normal inventory screen, this is only a temporary measure until the GUI is written
* Tools breaking blocks when held in the left hand (if possible)

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

When the project is in a more mature state I will also include another script (likely an ant script) to retrieve the fully compiled and packaged code

