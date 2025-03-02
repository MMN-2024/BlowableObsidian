# BlowableObsidians

A Minecraft plugin that makes obsidian and other blocks destructible by explosions. Instead of instantly breaking, blocks take damage from explosions and eventually break after multiple hits.

## Features

- Make obsidian, enchantment tables, anvils, and other blocks destructible by explosions
- Configurable block health and explosion damage
- Block health regeneration over time
- Check block health with a configurable item
- World-specific settings
- Protection for bedrock at specific Y levels
- Liquid explosion protection (configurable damage multiplier in water/lava)

## Configuration

The plugin is highly configurable. See `config.yml` for all options:

- World settings (enable/disable per world)
- Explosion settings (radius and damage per explosion type)
- Block health values
- Regeneration time
- Messages

## Commands

- `/blowable reload` - Reload the plugin configuration (requires permission: blowable.reload)

## Installation

1. Download the plugin JAR file
2. Place it in your server's plugins folder
3. Start or restart your server
4. Configure the plugin in the `plugins/BlowableObsidians/config.yml` file

## Support

For support, please contact the plugin developer.