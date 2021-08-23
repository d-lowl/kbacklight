package space.dlowl.kbacklight

import com.soywiz.korio.lang.parseInt
import kotlinx.cli.*
import space.dlowl.dmenu.DMenu

val service = BacklightService()


@ExperimentalCli
fun main(args: Array<String>) {
    val parser = ArgParser("kBacklight")

    class ListBacklights(val service: BacklightService): Subcommand("list", "List backlights") {
        override fun execute() {
            println(service.getNames().joinToString("\n"))
        }
    }

    class GetBacklights(val service: BacklightService): Subcommand("get", "Get backlight brighness") {
        val backlightName by argument(ArgType.String, "name", description = "Backlight name").optional()
        val isCurrent by option(ArgType.Boolean, "current", "c").default(false)
        val isMax by option(ArgType.Boolean, "max", "m").default(false)
        val isPercent by option(ArgType.Boolean, "percent", "p").default(false)

        fun print(backlight: Backlight, printName: Boolean = false) {
            var res: String = if (printName) "${backlight.name}: " else ""
            res += if (isCurrent) {
                "${backlight.current}"
            } else if (isMax) {
                "${backlight.maxValue}"
            } else if (isPercent) {
                "${backlight.percent}"
            } else {
                "${backlight.current}/${backlight.maxValue} (${backlight.percent}%)"
            }
            println(res)
        }

        override fun execute() {
            if (backlightName == null) {
                service.getBacklights().forEach {
                    print(it, printName = true)
                }
            } else {
                val backlight = service.get(backlightName!!)
                if (backlight == null) {
                    println("$backlightName not found")
                } else {
                    print(backlight, printName = false)
                }
            }
        }
    }

    class SetBacklight(val service: BacklightService): Subcommand("set", "Set backlight brightness") {
        val backlightName by argument(ArgType.String, "name", description = "Backlight name")
        val value by argument(ArgType.Int, "value")
        val isPercent by option(ArgType.Boolean, "percent", "p").default(false)
        val isRelative by option(ArgType.Boolean, "relative", "r").default(false)

        override fun execute() {
            if (service.get(backlightName) == null) {
                println("Backlight `$backlightName` not found")
                return
            }
            when {
                isPercent && isRelative -> service.get(backlightName)!!.addPercent(value)
                isPercent && !isRelative -> service.get(backlightName)!!.setPercent(value)
                !isPercent && isRelative -> service.get(backlightName)!!.addCurrent(value)
                !isPercent && !isRelative -> service.get(backlightName)!!.setCurrent(value)
            }
        }
    }

    class DMenuBacklight(val service: BacklightService): Subcommand("dmenu", "Backlight brightness dmenu") {
        var backlightName by option(ArgType.String, "name")

        override fun execute() {
            if (backlightName == null) {
                backlightName = DMenu.show("Backlight name", service.getNames())
            }
            if (service.get(backlightName!!) == null) {
                println("Backlight `$backlightName` not found")
                return
            }

            val command = DMenu.show("Command [+-]?\\d+%?")
            val isRelative = command.startsWith("+") || command.startsWith("-")
            val isPercent = command.endsWith("%")
            val value = command.trim('+', '%').parseInt()

            when {
                isPercent && isRelative -> service.get(backlightName!!)!!.addPercent(value)
                isPercent && !isRelative -> service.get(backlightName!!)!!.setPercent(value)
                !isPercent && isRelative -> service.get(backlightName!!)!!.addCurrent(value)
                !isPercent && !isRelative -> service.get(backlightName!!)!!.setCurrent(value)
            }
        }

    }

    parser.subcommands(
        ListBacklights(service),
        GetBacklights(service),
        SetBacklight(service),
        DMenuBacklight(service),
    )

    parser.parse(args)
}