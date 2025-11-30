package com.example.appmovilsiivmex.domain.usecase


// Mapa A–Z -> 0..25
private val ALPHABET: Map<Char, Int> =
    ('A'..'Z').withIndex().associate { it.value to it.index }

// Convierte un bloque de letras (ej. "ABC") a un número en base 26
private fun lettersToNum(letters: String): Int {
    var v = 0
    for (ch in letters) {
        v = v * 26 + (ALPHABET[ch] ?: 0)
    }
    return v
}

// Normaliza la placa quitando todo lo que no sea A-Z o 0-9
private val normalizeRegex = Regex("[^A-Z0-9]")

private fun normalizePlate(raw: String): String {
    return normalizeRegex.replace(raw.uppercase(), "")
}

// Funciones para hacer comparaciones
private data class IntPair(val a: Int, val b: Int) : Comparable<IntPair> {
    override fun compareTo(other: IntPair): Int {
        return when {
            a != other.a -> a - other.a
            else -> b - other.b
        }
    }
}

private data class IntTriple(val a: Int, val b: Int, val c: Int) : Comparable<IntTriple> {
    override fun compareTo(other: IntTriple): Int {
        return when {
            a != other.a -> a - other.a
            b != other.b -> b - other.b
            else -> c - other.c
        }
    }
}


// Placas Ciudad de México 2002–2013: DDDLLL => 001-AAA … 999-ZZZ
private val cdmxDfRegex = Regex("""(\d{3})([A-Z]{3})""")
// Convencional: LDDLLL => A01-AAA … Z99-ZZZ
private val cdmxConvRegex = Regex("""([A-Z])(\d{2})([A-Z]{3})""")
// ECO: DDLNNN => 97E-001 … 49H-999
private val cdmxEcoRegex = Regex("""(\d{2})([A-Z])(\d{3})""")

private fun inRangeCdmx(norm: String): Boolean {
    val s = norm.uppercase()

    // Placas Ciudad de México 2002–2013: DDDLLL => 001-AAA … 999-ZZZ
    cdmxDfRegex.matchEntire(s)?.let { m ->
        val ddd = m.groupValues[1]
        val lll = m.groupValues[2]

        val key = IntPair(ddd.toInt(), lettersToNum(lll))
        val kmin = IntPair(1, lettersToNum("AAA"))
        val kmax = IntPair(999, lettersToNum("ZZZ"))

        return key in kmin..kmax
    }

    // Convencional: LDDLLL => A01-AAA … Z99-ZZZ
    cdmxConvRegex.matchEntire(s)?.let { m ->
        val l = m.groupValues[1][0]
        val dd = m.groupValues[2]
        val lll = m.groupValues[3]

        val key = IntTriple(ALPHABET[l] ?: 0, dd.toInt(), lettersToNum(lll))
        val kmin = IntTriple(ALPHABET['A'] ?: 0, 1,  lettersToNum("AAA"))
        val kmax = IntTriple(ALPHABET['Z'] ?: 0, 99, lettersToNum("ZZZ"))

        return key in kmin..kmax
    }

    // ECO: DDLNNN => 97E-001 … 49H-999
    cdmxEcoRegex.matchEntire(s)?.let { m ->
        val dd  = m.groupValues[1]
        val l   = m.groupValues[2][0]
        val nnn = m.groupValues[3]

        // (L, DD, NNN)
        val key  = IntTriple(ALPHABET[l] ?: 0, dd.toInt(), nnn.toInt())
        val kmin = IntTriple(ALPHABET['E'] ?: 0, 97, 1)
        val kmax = IntTriple(ALPHABET['H'] ?: 0, 49, 999)


        return key in kmin..kmax
    }

    return false
}


// Placas 2025: LLLNNNL   => LGA-001-A a PEZ-999-Z
private val edo2025Regex = Regex("""([A-Z]{3})(\d{3})([A-Z])""")
// Placas 2013–2022: LLLNNNN => LGA-0000 a PEZ-9999
private val edo2013Regex = Regex("""([A-Z]{3})(\d{4})""")
// ECO: DDLNNN   => 11J-001 a 62L-999
private val edoEcoRegex = Regex("""(\d{2})([A-Z])(\d{3})""")

private fun inRangeEdomex(norm: String): Boolean {
    val s = norm.uppercase()

    // Placas 2025: LLLNNNL   => LGA-001-A a PEZ-999-Z
    edo2025Regex.matchEntire(s)?.let { m ->
        val lll = m.groupValues[1]
        val nnn = m.groupValues[2]
        val l   = m.groupValues[3][0]

        val key  = IntTriple(lettersToNum(lll), nnn.toInt(), ALPHABET[l] ?: 0)
        val kmin = IntTriple(lettersToNum("LGA"), 1,   ALPHABET['A'] ?: 0)
        val kmax = IntTriple(lettersToNum("PEZ"), 999, ALPHABET['Z'] ?: 0)

        return key in kmin..kmax
    }

    // Placas 2013–2022: LLLNNNN => LGA-0000 a PEZ-9999
    edo2013Regex.matchEntire(s)?.let { m ->
        val lll  = m.groupValues[1]
        val nnnn = m.groupValues[2]

        val key  = IntPair(lettersToNum(lll), nnnn.toInt())
        val kmin = IntPair(lettersToNum("LGA"), 0)
        val kmax = IntPair(lettersToNum("PEZ"), 9999)

        return key in kmin..kmax
    }

    // ECO: DDLNNN   => 11J-001 a 62L-999
    edoEcoRegex.matchEntire(s)?.let { m ->
        val dd  = m.groupValues[1]
        val l   = m.groupValues[2][0]
        val nnn = m.groupValues[3]

        val key  = IntTriple(dd.toInt(), ALPHABET[l] ?: 0, nnn.toInt())
        val kmin = IntTriple(11, ALPHABET['J'] ?: 0, 1)
        val kmax = IntTriple(62, ALPHABET['L'] ?: 0, 999)

        return key in kmin..kmax
    }

    return false
}

enum class PlateRegion {
    CDMX,
    EDOMEX
}

data class PlateValidationResult(
    val isValid: Boolean,
    val region: PlateRegion? = null,
    val message: String? = null
)


class ValidatePlateUseCase {

    operator fun invoke(rawPlate: String): PlateValidationResult {
        val norm = normalizePlate(rawPlate)

        if (norm.isEmpty()) {
            return PlateValidationResult(
                isValid = false,
                message = "Ingresa una placa."
            )
        }

        val isCdmx = inRangeCdmx(norm)
        val isEdomex = inRangeEdomex(norm)

        return when {
            isCdmx -> PlateValidationResult(
                isValid = true,
                region = PlateRegion.CDMX,
                message = null
            )
            isEdomex -> PlateValidationResult(
                isValid = true,
                region = PlateRegion.EDOMEX,
                message = null
            )
            else -> PlateValidationResult(
                isValid = false,
                region = null,
                message = "La placa no corresponde a rangos válidos de CDMX o EDOMEX."
            )
        }
    }
}
