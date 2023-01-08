package com.cobblemon.mod.common.config

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class LastChangedVersion(val version: String)
