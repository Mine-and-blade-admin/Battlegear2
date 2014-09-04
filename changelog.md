== Mine & Blade: Battlegear 2 - Bullseye 1.0.5.7 ==
* Allowed more items in battle slots: compass, clock, and more...
* Expanded Quiver API for custom bows
* Made Piercing Arrow to pierce multiple layers of blocks
* Added support for custom saved file name in M.U.D
* Allowed making of custom weapons from other mods IMCMessage-s
* Added enchantment id conflict automated fixer (fixing previous known issue)
** beware if you add new mods, old world saves could break
* Added a sensibility parameter for /weaponwield
* Fixed block being "stolen"/shield usage between players in battlemode on server

== Mine & Blade: Battlegear 2 - Bullseye 1.0.5.6 ==
* Update for 1.7.10
* Fixed invalid attributes while using weapon in left hand
* Fixed arrows not obeying GameRule
* Added DynamicLight support for light sources in left hand
* Reduced M.U.D chatting (with verbose option to configure)
* Fixed flagpole crashing dedicated servers
* Fixed iron textures for some systems
* Remapped item registry names for consistency
** world saves should be convertible without loss
* Fixed pickable quiver arrows from infinity bow

**Known issue**
Enchanted books from vanilla creative tabs (weapons, misc) with the Battlegear enchantments don't apply correctly to items

== Mine & Blade: Battlegear 2 - Bullseye 1.0.5.5 ==
* Fixed bow FOV with "draw" enchantment
* Added player inventory expansion API
* Fixed M.U.D state not saved after changes in GUI
* Enhanced Quiver API to support arrows from plugins
* Fixed spears taking double damage
* Expanded heraldry API (not official yet)
* Removed warning with flagpoles
* Put FakePlayer out of inventory IllegalAccessException (PneumatiCraft compatibility)
* Catch a crash with offhand block placement
* Overriden hotbar keys in battlemode
** allow item switching without mouse scroll
* Added more wood types into flagpoles

== Mine & Blade: Battlegear 2 - Bullseye 1.0.5.4 ==
* Added picking up arrows into offhand quiver
* Fixed creative knight armor missing things
* Fixed mace effects
* Force-fixed MCPC+ inventory bug
* Fixed multiple null errors
* Attempt to be more compatible with modded bows
* Improved weapon selection in battle slots
* Added command for op to set weapons wielding

== Mine & Blade: Battlegear 2 - Bullseye 1.0.5.3 ==
* Fixed shield sounds
* Fixed player not dropping items on death
* Fixed mystery arrow crash 
* Added damage in knight armor (still incomplete)
* Fixed flagpole

== Mine & Blade: Battlegear 2 - Bullseye 1.0.5.2 ==
* Fixed keys toggle
* Fixed darkened slots in inventory
* Fixed delay issue for addon shields
* Fixed leech arrow healing player even if his victim is invulnerable
* Fixed incorrect version number (stop MUD cycle)

== Mine & Blade: Battlegear 2 - Bullseye 1.0.5.1 ==
* Fixed crash from using empty quiver
* Registered recipes into forge sorter
* Fixed pick block in survival
* Added ShieldBlockEvent (API upgrade)
* Fixed shields no catching arrows
* Fixed quiver arrow switch
* Enable possibility of mouse keybind for custom gameplay keys
**Please report any bug you find**

== Mine & Blade: Battlegear 2 - Bullseye 1.0.5.0 ==
* Fixed server crash with no method found exception
* Fixed in-game option Disabled Renderers not removing values
* Added Screen Customizer option in Config screen
* Added a Disable button in MUD screen, to disable updates check
* Added flag poles with rendering option and recipes
* Sneak peek:Added back knight armor (incomplete rendering)

== Mine & Blade: Battlegear 2 - Bullseye 1.0.4.9 ==
* Update to 1.7.2
* Added Config screen in mod list, with client-side options
* Added official markers for all parts of the API
* Progress toward the heraldry system

== Mine & Blade: Battlegear 2 - Bullseye 1.0.4.8 ==
* Added option for quiver rendering on skeletons
* Added descriptions for arrows
* Reduced hitting range for bare hands
* Fixed sheathed sword rendering on back
* Other internal changes for the API

== Mine & Blade: Battlegear 2 - Bullseye 1.0.4.7 ==
* Fixed daggers out-of-slot bug
* Fixed creative pick block
* Fixed blocks wrongly consumed in offhand
* Added more support for TinkersConstruct tabs
* Internal changes for the API
** Incompatibilities may appear
**Known Issues**  
* Inventory issues with MCPC+, this mod is NOT recommended to use on MCPC+ servers

== Mine & Blade: Battlegear 2 - Bullseye 1.0.4.6 ==
* Fixed piercing arrow
* Organized config file, added more comments
* Added default enchantment rendering on spear
* Improved durability for mace and waraxe
* Added chinese and french locals for MUD
* Transition towards 1.7 secured
* Perfected block placing with offhand
* Added leech arrow: heals with the enemy life
* Improved ender arrow: gives block if player sneak

== Mine & Blade: Battlegear 2 - Bullseye 1.0.4.5 ==
* Allowed quiver into offhand slots to use with bow
* Added more rendering events into the API
* Default quiver now allows all registered arrows
* Added another effect into the mystery arrow
* Added config options to disable items completely
* Moved gui buttons slightly to the right

== Mine & Blade: Battlegear 2 - Bullseye 1.0.4.4 ==
* Added 3 arrows with recipes: Piercing, Poison, Mystery
* Added uncrafting recipes for all arrows
* Added options to disable items special rendering
* Added options to move shield stamina bar and quiver hotbar
* Added bow enchantments: loot, charging
* Fixed ender arrow teleport going through bedrock
* Fixed picking arrow on the ground returning wrong arrow

== Mine & Blade: Battlegear 2 - Bullseye 1.0.4.3 ==
* Added Quiver with 4 slots to put arrows into
** Slots are selected with the special action key
* Added 3 arrows with recipes: Explosive, Ender, Fire
* Changed the way the bow is rendered, to show selected arrow
* Quiver is rendered on player's back when a bow is equiped
* Gave skeletons a chance to use new arrow types
** See config options to modify this
* Added an API for modders to use or make quivers
** and change the way the bow fire arrows

== Mine & Blade: Battlegear 2 - Warcry 1.0.4.2 ==
* Fixed bug of MCPC in enchantment
* Fixed packet without data crash
* Added better config options for enchantment ids

== Mine & Blade: Battlegear 2 - Warcry 1.0.4.1 ==
* Added block placing by offhand
* Added WeaponRegistry and FMLInterModComms access for other mods weapons
* Fixed conflict with MCPC in NetServerHandler transformer
* TinkersConstruct should have caught our API changes
**meaning it should be compatible again in their latest version

== Mine & Blade: Battlegear 2 - Warcry 1.0.4.0 ==
* Fixed shields sound
* Organized sheathed item rendering into the API
* Made shields use the "useItem" keybind
* Fixed server crash with TConstruct
* Fixed transformers to keep fields and methods order
* Added EntityArrow transformer

== Mine & Blade: Battlegear 2 - Warcry 1.0.3.9 ==
* Caught config crash with M.U.D
* Made /mud a client side command
* Added config option to disable GUI buttons
* Added custom enchantments for Shields

== Mine & Blade: Battlegear 2 - Warcry 1.0.3.8 ==
* Fixed config file being a folder on first launch
* Added back keys for inventory and heraldry, disabled by default
**(See in config file the Enable GUI Keys setting)
* Added support for Tinker's Construct inventory tabs
* Code improvements for packets

== Mine & Blade: Battlegear 2 - Warcry 1.0.3.7 ==
* Moved switch buttons to avoid Tinker's Construct conflict

== Mine & Blade: Battlegear 2 - Warcry 1.0.3.6 ==  
* Finished coremod fixes
* Replaced inventory and heraldry keys with switch buttons in inventory gui

== Mine & Blade: Battlegear 2 - Warcry 1.0.3.5 ==  
* Simplified EntityOtherplayerMP patch, and other coremod fixes
* Added buttons to switch between gui

== Mine & Blade: Battlegear 2 - Warcry 1.0.3.4 ==  
**Bugs Fixed**
* Fixed EntityOtherplayerMP crash on server

== Mine & Blade: Battlegear 2 - Warcry 1.0.3.3 ==  
**Bugs Fixed**
* Fixed NetClientHandler crash on server

== Mine & Blade: Battlegear 2 - Warcry 1.0.3.2 ==  
**Bugs Fixed**
* Fixed BattlegearSyncPacket crash on server

== Mine & Blade: Battlegear 2 - Warcry 1.0.3.1 ==  
**Bugs Fixed**  
* Re-added MUD
* Added a config file for MUD
* Removed the still work in progress heraldry designer bound to the "p" key
** Note that the bound key will still appear in options but do nothing currently
**Known Issues**  
* Still some issues with mcpc+, it is recomended to not use this mod on mcpc+ servers

== Mine & Blade: Battlegear 2 - Warcry 1.0.3 ==  
**New Features**  
* Updated to minecraft 1.6.4
** Minecraft forge 9.10.1.850 or higher is nor a requirement
**Bugs Fixed**  
* Fixed a crash with minecraft forge 850 & higher
* Fixed a visual bug that would case the bow to reset it's animation every 3 seconds
**Known Issues**  
* Still some issues with mcpc+, it is recomended to not use this mod on mcpc+ servers

== Mine & Blade: Battlegear 2 - Warcry 1.0.2 ==  
**Bugs Fixed**  
* Fixed a crash with mcpc+
* Changed the iron shield recipe back (yet again)
** It should now be the same as described on the forum page
**Known Issues**  
* The shield block bar will be placed over any extra hearts given by various sources
* There is an issue with Forge versions greater than 9.10.1.849. Please use Forge 9.10.1.849 or lower (9.10.1.850 has the problematic code)
* There are still many issues with mcpc+

== Mine & Blade: Battlegear 2 - Warcry 1.0.1 ==  
**New Features**  
* A mod url button has been added to M.U.D.
**Bugs Fixed**  
* M.U.D. will check for updates every hour rather than every minute

== Mine & Blade: Battlegear 2 - Warcry 1.0.0 ==  
**New Features**  
* New update manager (M.U.D)
** Displays when a new update is uploaded
** Allows a user to download the new version from inside minecraft.
* Shields are now dyabe
** They follow the same dye method as Leather armour
** Dye can be removed using a bucket of water
**Bugs Fixed**
* Fixed an issue caused by hitting an enemy with an offhand dagger
* Fixed a rendering issue where the bow animation would activate while it is sheathed
**Known Issues**  
* M.U.D. checks for updates every minute

== Mine & Blade: Battlegear 2 - Warcry 0.2.3 ==  
**New Features**  
* Added various translations (there are not many, and many are not complete)
**Bugs Fixed**  
* Fixed Items not being dropped on player death
* Fixed rendering issue while wearing armour
* Fixed the mace stunning 100% of the time
* Increased the stun percentage of the mace (let me know if it is now OP)
* Reduced the time taken to sync items from 3 sec to 1 sec (let me know if this makes servers unstable)
* Fixed issues with minecraft forge "Latest Builds"
* Corrected the crafting recipe for the iron shield.

== Mine & Blade: Battlegear 2 - Warcry 0.2.2 ==  
**Bugs Fixed**  
* Fixed many issues related to SMP including (but not limited to)
** Crash when entering SMP
** Crashes when using items in battlemode
** Synchronization of "Battle Items"
** Shield bash not working correctly
** Rendering issues in that players arms would not be in the correct position
* Fixed the spear not applying extra damage when mounted
* Fixed an infinite loop caused by the spear
* Fixed an infinite loop caused by the dagger
**Known Issues**  
* The synchronization is a bit "dumb". It may take up to 3 seconds to correctly update.

== Mine & Blade: Battlegear 2 - Warcry 0.2.1 ==  
**New Features**  
* Updated to 1.6.2
**Known Issues**  
* Many issues related to SMP
