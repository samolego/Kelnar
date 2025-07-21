package io.github.samolego.kelnar

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform