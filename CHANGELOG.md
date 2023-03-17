# Changelog

## [1.3.0](#1-3-0)
### Additions
- Added new models and animations for Poké Balls and reworked their mechanics to feel much smoother instead of being pure frustration.
- Added party scrolling via holding R and using the mouse wheel so you don't need to take your hand off your mouse.
- Added a cap of Pokémon spawns in an area because waiting a while made things insane. This is controlled by a new `pokemonPerChunk` config option.
- Added models and animations for heaps of Pokémon (101): Riolu, Lucario, Chimchar, Monferno, Infernape, Turtwig, Grotle, Torterra, Popplio, Brionne, Primarina, Treeko, Grovyle, Sceptile, Snivy, Servine, Serperior, Tepig, Pignite, Emboar, Oshawott, Dewott, Samurott, Grookey, Thwackey, Rillaboom, Scorbunny, Raboot, Cinderace, Sobble, Drizzile, Inteleon, Fennekin, Braixen, Delphox, Froakie, Frogadier, Greninja, Chespin, Quilladin, Chesnaught, Miltank, Torkoal, Kricketot, Kricketune, Heatmor, Durant, Wooloo, Dubwool, Pumpkaboo, Gourgeist, Sigilyph, Cryogonal, Whismur, Loudred, Exploud, Misdreavus, Mismagius, Tatsugiri, Eiscue, Luvdisc, Stantler, Wyrdeer, Gible, Gabite, Garchomp, Sneasel, Weavile, Elgyem, Beheeyem, Baltoy, Claydol, Nacli, Naclstack, Alcremie, Milcery, Dhelmise, Morelull, Shiinotic, Xerneas, Klink, Klang, Klinklang, Joltik, Galvantula, Honedge, Duoblade, Aegislash, Spiritomb, Mawile, Carvanha, Sharpedo, Seedot, Nuzleaf, Shiftry, Lotad, Lombre, Ludicolo, Pineco, Forretress, and Spinda.
- Added generation 3, 4, 5, 6, 7, and 8 Starter Pokémon to the starter select screen.
- Added particle effect support for model animations
- Added particle effect and animation for Gastly.
- Added sleep and faint animations to many Pokémon.
- Added item holding for Pokémon. Any Minecraft item can be given to a Pokémon by holding shift and right-clicking them. Traditional Pokémon held items will have their expected battle effects.
- Added heaps of held items with crafting recipes: Assault Vest, Big Root, Black Belt, Black Sludge, Charcoal, Choice Band, Choice Scarf, Choice Specs, Dragon Fang, Exp. Share, Focus Band, Hard Stone, Heavy-Duty Boots, Leftovers, Light Clay, Lucky Egg, Magnet, Miracle Seed, Muscle Band, Mystic Water, Never-Melt Ice, Poison Barb, Quick Claw, Rocky Helmet, Safety Goggles, Sharp Beak, Silk Scarf, Silver Powder, Soft Sand, Spell Tag, Twisted Spoon, and Wise Glasses.
- Added heaps of evolution items with crafting recipes: Milcery's sweets items, Chipped Pot, Cracked Pot, Deep Sea Scale, Deep Sea Tooth, Dragon Scale, Galarica Cuff, Galarica Wreath, Peat Block, Prism Scale, Razor Claw, Razor Fang, Reaper Cloth, Sachet, Sweet Apple, Tart Apple, and Whipped Dream.
- Existing evolution items all now either have a crafting recipe or drop from Pokémon.
- Added the Item [tags](https://minecraft.fandom.com/wiki/Tag) `cobblemon:held/experience_share` and `cobblemon:held/lucky_egg` allowing you to mark any items you desire to have the effects implied in the tag name.
- Added an interface that appears when interacting with your Pokémon while sneaking. The interface allows for interactive options such as shouldering and exchanging held items.
- Added blinking animations to many Pokémon.
- Added animated texture support.
- Added translucent option for aspect layers.
- Added glowing textures to many Pokémon and it looks amazing.
- Added the Saturation shoulder effect.
- Added the Haste shoulder effect, initially for Joltik.
- Added the Water Breathing shoulder effect, initially for Wooper.
- Added the Speed shoulder effect, initially for Pichu and Pikachu.
- [Dispensers](https://minecraft.fandom.com/wiki/Dispenser) can now use shears to automatically harvest fully grown Apricorns.
- Added milking to Miltank.
- Added shearing to Wooloo and Dubwool.
- Added data for generation 9 Pokémon species, moves, and ability data. They're all still Substitute models, but their moves and abilities work.
- Added support for custom Pokémon to implement 'quirks' such as blinks.
- Added sound effect for harvesting Apricorns.
- Added icon to summary and PC interfaces to indicated if a Pokémon is shiny.
- Added the ``/spawnpokemonat <pos> <properties>`` command, the ``pos`` argument uses the same syntax as the Minecraft [summon](https://minecraft.fandom.com/wiki/Commands/summon) command.
- Added the `/giveallpokemon` command which is definitely safe and not insane.
- Added compatibility with Carry On by preventing the mod being able to interact with Cobblemon entities, the mod caused too many gameplay detrimental features to stay enabled.
- Added healing to your party when you sleep in a bed.
- Added the 'ability' Pokémon Property so commands can specify the ability.
- Added block tag support to the 'neededBaseBlocks' and 'neededNearbyBlocks' spawn condition.
- Added a config option for disallowing players from damaging Pokémon by hand.
- Apricorn seeds can now be used with the [Composter](https://minecraft.fandom.com/wiki/Composter), these have the layer increase chance of 65% like Apricorns and various Minecraft crops.
- Added support for Pokémon species data appending making it so datapack developers no longer need to overwrite files.
- Added an implementation of every [catch rate](https://bulbapedia.bulbagarden.net/wiki/Catch_rate) from generation 1 to 9, these can be used by changing the ``captureCalculator`` config value:
  - ``generation_1`` Sets the calculator to the generation 1 implementation.
  - ``generation_2`` Sets the calculator to the generation 2 implementation.
  - ``generation_2_fixed`` Sets the calculator to the generation 2 implementation with the status multiplier bug fixed.
  - ``generation_3_4`` Sets the calculator to the generation 3 and 4 implementation.
  - ``generation_5`` Sets the calculator to the generation 5 implementation.
  - ``generation_6`` Sets the calculator to the generation 6 implementation.
  - ``generation_7`` Sets the calculator to the generation 7 implementation.
  - ``generation_8`` Sets the calculator to the generation 8 implementation.
  - ``generation_9`` Sets the calculator to the generation 9 implementation.
  - ``cobblemon`` Sets the calculator to the custom Cobblemon implementation. This is the default value.
  - ``debug`` Sets the calculator to the debug/cheat implementation, every attempt will be a successful critical capture.
 
### Changes
- Pokémon now save to the world by default, meaning the same Pokémon will remain in the world and not disappear after you log out and log back in. They still despawn over time though.
- Significantly sped up the Poké Ball shake animation so it takes less time to try to catch Pokémon.
- Update the PC and Healing Machine models and bounding boxes.
- The Healing Machine and PC now emit light when fully charged or when turned on respectively.
- The PC block screen will now turn on when being used.
- The Healing Machine will now visually display its charge level using 6 stages.
- The Healing Machine will now emit a redstone signal with the strength of 1 for every 10% charge it has when attached to a [Redstone Comparator](https://minecraft.fandom.com/wiki/Redstone_Comparator).
- Made it so that particles are not shown whenever you have a shoulder Pokémon that gives potion effects.
- Changed hitbox and size definitions for Decidueye, Blastoise, and Magmortar
- Apricorns can now be harvested with Axes, the speed will scale with enchantments and tool material, only dropping the Apricorn if fully grown, these will still destroy the Apricorn so the manual harvest still is recommended unless you're just keen on destroying trees.
- Apricorns are now a part of the vanilla tag ``minecraft:blocks/mineable/axe``.
- Apricorns are now compatible with any mod that breaks a whole tree at once.
- Apricorns no longer have a config value for the seed drop chance these are now a part of their loot table which can be found in ``cobblemon/loot_tables/blocks/<color>_apricorn.json``.
- Advancements were redone to be slightly more interesting, with improved names, descriptions, and sorting.
- Updated models and textures for Tentacool line, Gengar, Slowpoke line, Tyrogue line, Doduo line, Dratini, Dragonair, Quagsire, and Piplup line. There were probably others, the team lost track.
- Improved sending out Pokémon at the start of battle so that they are positioned in a less annoying way.
- Name Tags will no longer be used on Pokémon and Poke Ball entities, this prevents the item from being wasted.
- Lowered spawn rate of Tauros.
- Sableye now spawns near gem ores as well as amethyst.
- Added evolution stones and items to item tags

### Fixes
- Fixed catch rates being entirely too difficult.
- Fixed various strange battle issues such as Flying types being vulnerable to Ground type moves and status effects hitting despite vulnerabilities.
- Fixed shiny Gyarados not using the red Gyarados texture.
- Improved the framing of all in-game Pokémon in the party and PC GUIs so they aren't halfway out of the screen or something else crazy.
- Fixed incompatibility with Kotlin for Forge (by depending on Kotlin for Forge ourselves)
- Fixed Gengar, Goodra, and many other Pokémon showing the types of an alternate form despite those not being modelled yet.
- Fixed datapack Pokémon not being able to battle.
- Fixed Pokémon always being created with a moveset as if they're level 1 instead of their current level.
- Fixed an issue of Pokémon not rendering in GUIs on some Mac displays.
- Fixed a soft-duplicate that could occur when a party Pokémon is pushed through a Nether Portal or left in a boat.
- Fixed Pokémon that faint from poison appearing to be on full health and suckering you into false hope.
- Fixed incorrect spawns of Tentacool, Tentacruel, Dragonite, Politoed, Tangrowth, Lickilicky, Electivire, and Magmortar.
- Fixed crashes involving opening the Pokémon summary GUI with an empty party.
- Fixed lower brightness settings causing Pokémon to appear much too dark in menus such as the party and PC.
- Fixed Showdown sometimes failing to start, causing crashes.
- Fixed Showdown initialization happening several times when you login, more depending on how many times you have relogged this session.
- Fixed Showdown failing to update on first attempt. We totally weren't accidentally deleting our target directory or anything, nope.
- Fixed HP recovery related battle actions not animating for the client nor updating the in-game Pokémon HP.
- Fixed moves that force a switch such as Teleport and U-Turn soft locking battles.
- Fixed missing battle text for Bide, Speed Boost, Belly Drum, Anger Point, and Haze.
- Fixed battle messages for many field effects starting, ending, and actions caused by them such as blocking certain moves.
- Improved battle messages for effects that prevent a move from being executed such as a Taunt, requiring a recharge, flinched, etc.
- Fixed move names not being translated in battle messages.
- Fixed stat change messages for boosts over 3 stages.
- Fixed experience calculation not being completely accurate.
- Fixed positioning of Poké Balls when rendered in Healing Machines.
- Fixed a desync issue on servers where all Pokémon seemed like they were special forms when they weren't.
- Fixed an incompatibility with [Exordium](https://www.curseforge.com/minecraft/mc-mods/exordium).
- Fixed datapack Pokémon language key generation. A Pokémon under the namespace ``example`` named ``Pogemon`` will now correctly look for the lang key ``example.species.pogemon.name``.
- Fixed client not receiving messages for the different "stages" for the move Bide.
- Fixed the Medium Slow and Medium Fast experience group IDs, they're now ```medium_slow``` and ``medium_fast``. Any custom datapack Pokémon will require an update.
- Fixed Pokémon friendship being capped to the maximum level config value instead of the friendship one when loading Pokémon data.
- Fixed Poké Balls taking forever to capture Pokémon if you are underwater or up in the air where it takes a long time to hit the ground.
- Fixed Pokémon being unable to spawn on blocks such as snow layers.
- Fixed Pokémon spawning inside of trees.
- Fixed Pokémon experience not loading after a restart and instead going back to the minimal amount for the current level.
- Fixed being able to use ``/healpokemon`` in battle.
- Fixed being able to select fainted party members in the switch menu causing the battle to lock.
- Fixed ``/spawnpokemon`` command not supporting any command source other than players.
- Fixed issues with Charizard's sleep pose.
- Fixed players being able to use multiple healer machines at once.
- Fixed Pokémon layers not rendering when a Pokémon is on your shoulder.
- Fixed Caterpie and Weedle not moving or looking at players. That was meant to be Metapod and Kakuna; how embarrassing.
- Fixed Pokémon not carrying over the correct equivalent original ability when evolving from stages that only had one ability.
- Fixed Deerling and Sawsbuck not spawning with the correct season.
- Fixed issue of not being able to drag the scroll bar in summary and battle interfaces.
- Fixed optional aspects not saving and loading properly.
- Fixed layering logic so multiple texture layers can exist on a Pokémon (probably).
- Fixed not all Poké Balls being associated with the `cobblemon:pokeballs` item tag.
- Fixed the `/pokemoneditother` command not working.
- Fixed ambient sound file path for Porygon2.
- Fixed forms not being able to unset the secondary type of a Pokémon in the stat JSON.

### Developer
- Reworked CatchRateModifier, as such, existing implementations need to be updated.
- Fixed minimumDistanceBetweenEntities option being half of what it's set as.
- Fixed the contents of CobblemonEvents, CobblemonBlocks etc having getters instead of just being public static properties.
- Added ApricornHarvestEvent.
- Added a new item for representing Pokémon within native UI menus or item frames which display as the Pokémon's model. It's called a PokemonItem, with static functions to build one.

### Localization
- Added complete translations for French, German, Simplified Mandarin, Brazilian Portuguese, and Pirate English.
- Added partial translations for Traditional Mandarin, Italian, and Spanish. We'd love more help with this!
- Thank you to all of the fantastic volunteer translators for taking the time to help with this! 

## [1.2.0 - The Customization Update (January 1st, 2023)](#1-2-0)
### Additions
- Added models for Natu and Xatu, Murkrow and Honchkrow, Wailmer and Wailord.
- Added new PC interface and it is beautiful.
- Reworked the battle system so that battles load faster, cause fewer bugs, and can run on shared server hosts. This is a very big change that also reduced the size of the mod by 50mb!
- Added full resource pack / data pack customization of models, textures, animations, spawning, and spawn file presets to make custom Pokémon species and variations very easy to create. You can find the guide for creating custom Pokémon on [our website](https://cobblemon.com/guides/custompokemon.html)!
- Added water surface spawning for Pokémon like Lapras.
- Added emissive texture support to Pokémon render layers.
- Added compatibility for Mod Menu ([CurseForge](https://www.curseforge.com/minecraft/mc-mods/modmenu), [Modrinth](https://modrinth.com/mod/modmenu)).
- Added blank ambient Pokémon cries so cries can be added via resource packs.
- Added new sounds for GUIs and item uses.
- Added `nature` and `pokeball` options to commands such as `/spawnpokemon` and `/givepokemon`.

### Changes
- Reinforced party and PC saving to make data corruption from crashes less bad.
- Added a config option for whether the starter config will be exported, making it more maintainable as we add starter Pokémon.
- Battles now start with the options menu open for convenience.
- Doubled the default charge rate of healers. You'd need to reset your config under `./config/cobblemon/main.json` to see this change!
- Changed the default Apricorn seed chance config value from 6% to 10%.
- The mod now correctly reports our dependency on Architectury API so people don't get super confused when things don't work.
- Pokémon now look at their opponents during battle.
- Updated Sableye's animations to be super freaky.
- Changed the healer advancements to make the healing machine's mechanics more obvious.

### Fixes
- Fixed an incompatibility with [Porting Lib](https://github.com/Fabricators-of-Create/Porting-Lib) used by Fabric ports of mods such as Create or Twilight Forest.
- Fixed HP and max HP values in the battle GUI not being correct.
- Fixed some animations on snake-type Pokémon being super slow.
- Fixed a typo in the English name for Calcium. Calcuim.
- Fixed Pokémon gradually becoming rarer around you if you move long distances.
- Fixed a shoulder mount crash on Fabric.
- Fixed a rare issue where chunks would take a really long time to generate.
- Fixed a singleplayer bug where battles wouldn't work after leaving then rejoining a world.
- Fixed stat calculations for everything except HP. HP was fine though :).
- Fixed a randomized Pokémon spawning in mountains that corrupted the data of whatever party or PC it got into. Yikes.
- Fixed a rare crash involving literally random number generation. A random crash involving random numbers.
- Fixed all regular Mewtwo having the stats and types of Mega Mewtwo X - same deal with many other Pokémon. 
- Fixed the framing of many Pokémon in GUIs.
- Fixed texture glitches with Rattata and Nidoqueen (again!).
- Fixed dropped item forms of all Pokéballs and the Healing Machine, and slightly adjusted all other display settings
- Fixed issues with forms not showing the correct evolution in the evolution menu.
- Fixed some alternate forms not having the alternate stats and types.
- Fixed moves that only work in double battles not being selectable at all (such as Helping Hand and Aromatic Mist).
- Fixed abilities not remaining legal in some forms.
- Fixed Poké Ball capture effects not triggering after a successful capture, such as the Heal Ball's healing effect.
- Fixed multiple-hit moves sending gibberish into the battle chat.
- Fixed Pyukumuku not being appropriately scaled.
- Fixed shiny and other variations of Pokémon not showing in the battle GUI.
- Fixed Eevee being poorly positioned and un-animated on shoulders.
- Fixed a Pokémon's hitbox not updating when it evolves while sent out.
- Fixed a Pokémon's PP going from zero to above the maximum when entering another battle.

## [1.1.1 (November 27th, 2022)](#1-1-1)
### Fixes
- Fixed a critical issue with servers where Pokémon data didn't properly synchronize and so you couldn't see any.

## [1.1.0 - The Swim and Sleep Update (November 27th, 2022)](#1-1-0)
#### "Ideally not at the same time."
### Additions
- Added a new GUI for viewing party information, rearranging moves, and evolving Pokémon. It looks too good.
- Starter Pokémon will sleep on top of you if sent out when you get on a bed.
- Added sleeping animations for starters, the Weedle family, and the Caterpie family. More to come.
- Added Alolan Rattata and Alolan Raticate, Sableye, Deerling and Sawsbuck, and Pyukumukurutudulu or whatever it's called.
- Added swimming AI so Pokémon don't sink to the bottom in water.
- Aquatic Pokémon like Magikarp desperately move to water if they're on land.
- Added status condition indicators in the party overlay.
- Added HP labels to the battle interface so that you can see how much health you actually have.
- Added spawn data for all final and special evolutions previously lacking spawn data.
- Added shiny textures for many Pokémon (thank you MageFX!): Aerodactyl, Articuno, Zapdos, Moltres, Chansey, the Dratini family, Electabuzz, Goldeen and Seaking, Hitmonchan and Hitmonlee, Jynx, Kabuto and Kabutops, Magmar, Lickitung, Mr. Mime, Omanyte and Omastar, Rhyhorn and Rhydon, Koffing and Weezing, Porygon, Scyther, Seadra, Staryu and Starmie, and Tangela. Phew!
- Added a couple of new advancements.
- Added new items: Calcium, Carbos, HP Up, Iron, Protein, and Zinc. Currently only obtainable in Creative Mode (It's a surprise tool that will help us later).

### Changes
- Significantly improved AI pathing for larger entities so they won't keep trying to move to places they cannot possibly fit.
- Changed the starter menu and summary menu keybinds to `M` by default.
- Pokémon that are sent out slowly raise friendship. Before this it was faster and even worked when not sent out (wildly unbalanced).
- Updated Link Cable and Protector item sprites to be prettier.
- Slightly polished the Poké Ball opening and Poké Ball item use sounds.
- `/givepokemon random`, `/spawnpokemon random`, and `/spawnallpokemon` will now only choose implemented Pokémon.
- The battle message box now displays even when the battle GUI is minimised so that you can still see what's going on.
- Moved the `R` prompt in battle to be higher on the screen so that it's not as distracting.

### Fixes
- Fixed shinies and form variations not displaying in the party or PC.
- Fixed servers getting stuck on shutdown due to non-closed showdown server connections.
- Fixed a niche situation where players could challenge themselves. It's a little inspirational if you think about it.
- Fixed Pokémon natures not saving such that every time you restarted they had a totally different nature.
- Fixed some underground Pokémon spawning above ground instead. I'm told other weird spawns were probably fixed at the same time.
- Fixed Pokémon sometimes running in place. "It's still possible, but much less likely" - Yeah, ok devs.
- Fixed mod incompatibility with many Architectury API mods, including *Biome Makeover* and *Earth2Java*.
- Fixed a mod incompatibility with Minecraft Transit Railway Fabric and probably a bunch of other Fabric mods.
- Fixed being unable to customize keybinds on Forge.
- Fixed Summary keybinding being labeled as PokéNav. That comes later.
- Fixed apricorns spawning without leaves which meant sometimes apricorns were growing on the side of dirt and stone which doesn't make much sense to me.
- Fixed messages appearing in the console whenever a healer is used.
- Fixed spawning in several biome tag categories.
- Fixed resource pack support for Pokémon models and textures.
- **Model Fixes**
    - Fixed Paras and Nidoqueen looking very weird.
    - Fixed Hitmonchan asserting dominance with a T-pose as well as Grimer and Muk pointing their hands to the sky.
    - Fixed specific Pokémon suddenly pausing their animation after staying still for a long time.
    - Fixed Mankey's feet being buried in the ground.
    - Updated the Substitute model and updated its shiny texture to be better for the red-green colorblind.
    - Improved Horsea, Seadra, and Kingdra animations, especially on land.
- **Battle Fixes**
    - Fixed an issue with battles where Pokémon had a different max HP causing some desync issues with the health bars.
    - Fixed Magnitude battle messages.
    - Moves that are disabled or out of PP now show transparently in the battle GUI instead of being hidden completely.
    - Statuses like sleep and frozen no longer last forever if it was carried over from a previous battle.

### Localization
- Added species, ability, and move translations for `ko_ko`, `jp_jp`, `fr_fr`, `es_es`, `it_it`, and `zh_cn`.

## [1.0.0 (November 12th, 2022)](#1-0-0)
- Initial release.
