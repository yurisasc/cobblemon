# Changelog

## 1.1.0 - The Swim and Sleep Update
#### "Ideally not at the same time."


### Additions
- Added a new GUI for viewing party information, rearranging moves, and evolving Pokémon. It looks unreasonably good.
- Starter Pokémon will sleep on top of you if sent out when you get on a bed.
- Added sleeping animations (used in battle and in the wild) for starters, Weedle family, and the Caterpie family.
- Added Alolan Rattata and Alolan Raticate, Sableye, Deerling and Sawsbuck, and Pyukumukurutudulu or whatever it's called.
- Added swimming animations and swimming AI.
- Aquatic Pokémon like Magikarp desperately move to water if they're on land.
- Added status condition indicators in the party overlay.
- Added spawn data for all final and special evolutions previously lacking spawn data.
- Added shiny textures for many Pokémon (thank you MageFX!): Aerodactyl, Articuno, Zapdos, Moltres, Chansey, the Dratini family, Electabuzz, Goldeen and Seaking, Hitmonchan and Hitmonlee, Jynx, Kabuto and Kabutops, Magmar, Lickitung, Mr. Mime, Omanyte and Omastar, Rhyhorn and Rhydon, Koffing and Weezing, Porygon, Scyther, Seadra, Staryu and Starmie, and Tangela. Phew!
- Added new advancements.

### Changes
- Significantly improved AI pathing for larger entities
- Changed the starter menu and summary menu keybinds to `M` by default
- `/givepokemon random`, `/spawnpokemon random`, and `/spawnallpokemon` will now only choose implemented Pokémon
- Pokémon that are sent out slowly raise friendship. Before this it was faster and even worked when not sent out which was very unbalanced.

### Fixes
- Fixed resource pack support for Pokémon models and textures.
- Fixed shinies and form variations not displaying in the party or PC.
- Fixed servers getting stuck on shutdown due to non-closed showdown server connections.
- Fixed a niche situation where players could challenge themselves. It's a little inspirational if you think about it.
- Fixed Pokémon natures not saving such that every time you restarted they had a totally different nature.
- Fixed some underground Pokémon spawning above ground instead. I'm told other weird spawns were probably fixed at the same time.
- Fixed being unable to customize keybinds on Forge.
- Fixed Summary keybinding being labeled as PokéNav.
- Fixed Pokémon sometimes running in place. It's still possible, but much less likely.
- Fixed messages appearing in the console whenever a healer is used.
- Fixed mod incompatibility with many Architectury API mods, including *Biome Makeover* and *Earth2Java*.
- Fixed a mod incompatibility with Minecraft Transit Railway Fabric and probably a bunch of other Fabric mods.
- Fixed messages appearing in the console whenever a healer is used.
- Fixed Apricorns spawning without leaves.
- Fixed spawning in several biome tag categories.
- **Model Fixes**
    - Fixed Paras and Nidoqueen looking very weird.
    - Fixed Hitmonchan asserting dominance with a T-pose as well as Grimer and Muk pointing their hands to the sky.
    - Fixed specific Pokémon suddenly pausing their animation after staying still for a long time.
    - Fixed Mankey's feet being buried in the ground.
    - Updated the Substitute model, and updated its shiny texture to be better for the red-green colorblind.
    - Improved Horsea, Seadra, and Kingdra animations.
- **Battle Fixes**
    - Added Magnitude battle messages.
    - Moves that are disabled or out of PP now show transparently in the battle GUI instead of being hidden completely.
    - The battle message box now displays even when the battle GUI is minimised.
    - Moved the `R` prompt in battle to be higher on the screen, so that it's not as obstructive.
    - Statuses like sleep and frozen no longer last forever if it was carried over from a previous battle.

### Localization
- Added species, ability, and move translations for ko_ko, jp_jp, fr_fr, es_es, it_it, and zh_cn. 
## 1.0.0 (2022-11-12)
- Initial release.