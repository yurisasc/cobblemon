# Changelog

## Unreleased

### Additions
- Added a new GUI for viewing party information, rearranging moves, and evolving Pokémon. It looks unreasonably good
- Added status condition indicators for party sidebar
- Added Alolan Rattata and Alolan Raticate. Now where would you find them...?
- Pokémon like Magikarp desperately move to water if they're on land

### Changes
- Significantly improved AI pathing for larger entities
- Water Pokémon properly swim around instead of sinking to the bottom
- Changed the starter menu and summary menu keybinds to `M` by default
- `/givepokemon random`, `/spawnpokemon random`, and `/spawnallpokemon` will now only choose implemented Pokémon

### Fixes
- Fixed resource pack support for Pokémon models and textures
- Fixed shinies and form variations not displaying in the party or PC
- Fixed servers getting stuck on shutdown due to non-closed showdown server connections
- Fixed a niche situation where players could challenge themselves. It's a little inspirational if you think about it
- Fixed Pokémon natures not saving such that every time you restarted they had a totally different nature
- Fixed some underground Pokémon spawning above ground instead. I'm told other weird spawns were probably fixed at the same time
- Fixed being unable to customize keybinds on Forge
- Fixed Summary keybinding being labeled as PokéNav
- Fixed Pokémon sometimes running in place. It's still possible, but much less likely
- Fixed messages appearing in the console whenever a healer is used
- **Model Fixes**
    - Fixed Paras and Nidoqueen looking very weird
    - Fixed Hitmonchan asserting dominance with a T-pose as well as Grimer and Muk pointing their hands to the sky
    - Fixed specific Pokémon suddenly pausing their animation after staying still for a long time
    - Fixed Mankey's feet being buried in the ground
    - Updated the Substitute model, and updated its shiny texture to be better for the red-green colourblind
    - Improved Horsea, Seadra, and Kingdra animations
- **Battle Fixes**
    - Added Magnitude battle messages
    - Moves that are disabled or out of PP now show transparently in the battle GUI instead of being hidden completely
    - The battle message box now displays even when the battle GUI is minimised
    - Moved the `R` prompt in battle to be higher on the screen, so that it's not as obstructive

## 1.0.0 (2022-11-12)
- Initial release