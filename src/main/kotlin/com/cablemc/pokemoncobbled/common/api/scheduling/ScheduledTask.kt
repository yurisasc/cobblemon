package com.cablemc.pokemoncobbled.common.api.scheduling

import kotlin.math.max

/**
 * A task to be executed synchronously in beat with ticking. The precision of tasks is such that the delay
 * is never more than one tick from the scheduled time. Pinched from Cable Libs.
 *
 * @author landonjw, Hiroku
 */
class ScheduledTask(
    /** The action to run. */
    val action: (ScheduledTask) -> Unit,
    /** The identifier, optional. */
    val identifier: String? = null,
    /** How long until the task should execute. */
    delayTicks: Int,
    /** The ticks between each execution, if this is repeated. */
    val intervalTicks: Int = 0,
    /** The number of times this task should iterate. */
    val iterations: Int = 1
) {
    var currentIteration: Int = 0
        private set
    var ticksRemaining: Int = 0
        private set
    var expired = false
        private set
    var paused = false

    init {
        if (delayTicks > 0) {
            ticksRemaining = delayTicks
        }
    }

    override fun toString(): String {
        return identifier?.let { "Task-$it" } ?: super.toString()
    }

    fun tick() {
        if (!expired && !paused) {
            ticksRemaining = max(0, --ticksRemaining)
            if (ticksRemaining == 0) {
                action(this)
                currentIteration++
                if (intervalTicks > 0 && (currentIteration < iterations || iterations == -1)) {
                    ticksRemaining = intervalTicks
                } else {
                    expired = true
                }
            }
        }
    }

    fun expire() {
        this.expired = true
    }

    class Builder {
        private var action: ((ScheduledTask) -> Unit)? = null

        /** The number of ticks before the task will first be executed. */
        private var delayTicks: Int = 0

        /** The number of ticks before the task will be executed after it's already executed. */
        private var interval: Int = 0

        /** The number of times the task will run. -1 to run indefinitely. */
        private var iterations: Int = 1
        
        private var identifier: String? = null
        
        fun identifier(identifier: String): Builder {
            this.identifier = identifier
            return this
        }

        /**
         * Sets the consumer to be executed by the task.
         *
         * @param action the consumer to be executed by the task
         * @return builder with consumer set
         */
        fun execute(action: ((ScheduledTask) -> Unit)): Builder {
            this.action = action
            return this
        }

        /**
         * Sets the delay of the task.
         *
         * @param delayTicks the number of ticks before the task will first be executed
         * @return builder with delay set
         * @throws IllegalArgumentException if delay is below 0
         */
        fun delay(delayTicks: Int): Builder {
            require(delayTicks >= 0) { "Delay must not be below 0" }
            this.delayTicks = delayTicks
            return this
        }

        /**
         * Sets the interval between task executions.
         *
         * @param interval the number of ticks before the task will execute after already executing
         * @return builder with interval set
         * @throws IllegalArgumentException if interval is below 0
         */
        fun interval(interval: Int): Builder {
            require(interval >= 0) { "Interval must not be below 0" }
            this.interval = interval
            return this
        }

        /**
         * Sets the number of times the task will run.
         *
         * @param iterations the number of times the task will run, -1 treated as indefinitely
         * @return builder with number of iterations set
         * @throws IllegalArgumentException if iterations is below -1
         */
        fun iterations(iterations: Int): Builder {
            require(iterations >= -1) { "Iterations must not be below -1" }
            this.iterations = iterations
            return this
        }

        /**
         * Sets the task to run indefinitely.
         *
         * @return builder with infinite iterations set
         */
        fun infiniteIterations(): Builder {
            return iterations(-1)
        }

        /**
         * Builds the task using the properties of the task builder.
         * This will add the task to [TaskTickListener].
         *
         * @return a task with the task builder's properties
         * @throws IllegalStateException if consumer is not set
         */
        fun build(): ScheduledTask {
            checkNotNull(action) { "action must be set" }
            val task = ScheduledTask(
                action = action!!,
                identifier = identifier,
                delayTicks = delayTicks,
                intervalTicks = interval,
                iterations = iterations
            )
            ScheduledTaskTracker.addTask(task)
            return task
        }
    }
}