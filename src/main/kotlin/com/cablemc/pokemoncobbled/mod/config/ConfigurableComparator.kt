package com.cablemc.pokemoncobbled.mod.config

class ConfigurableComparator : Comparator<ConfigurableNode> {
    override fun compare(o1: ConfigurableNode?, o2: ConfigurableNode?): Int {
        if (o1 == null) {
            return -1
        }
        if (o2 == null) {
            return 1
        }
        /*val value = o1.category.compareTo(o2.category)
        if(value == 0)
            return 1
        return value*/
        return "${o1.configurable.category}${o1.configurable.name}".compareTo("${o2.configurable.category}${o2.configurable.name}")
    }
}