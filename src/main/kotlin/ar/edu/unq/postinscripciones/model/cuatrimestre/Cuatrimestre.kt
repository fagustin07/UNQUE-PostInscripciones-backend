package ar.edu.unq.postinscripciones.model.cuatrimestre

import ar.edu.unq.postinscripciones.model.exception.ErrorDeNegocio
import java.io.Serializable
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Year
import javax.persistence.*

@Entity
@Table(uniqueConstraints = [UniqueConstraint(name = "unique_anio_semestre", columnNames = ["anio", "semestre"])])
class Cuatrimestre(
    @Column(nullable = false)
    val anio: Int = 2020,
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val semestre: Semestre = Semestre.S1,
    @Column(nullable = false)
    var inicioInscripciones: LocalDateTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(0,0)),
    @Column(nullable = false)
    var finInscripciones: LocalDateTime = inicioInscripciones.plusDays(14).plusHours(12)
) : Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    companion object {
        fun actual() = Cuatrimestre(Year.now().value, Semestre.actual())

        fun actualConFechas(inicio: LocalDateTime?, fin: LocalDateTime?): Cuatrimestre {
            if (inicio != null && fin != null) {
                this.checkFechas(inicio, fin)
                return Cuatrimestre(Year.now().value, Semestre.actual(), inicio, fin)
            }

            val cuatrimestreComparable = this.actual()

            if (inicio != null) {
                checkFechas(inicio, cuatrimestreComparable.finInscripciones)
                return Cuatrimestre(
                    Year.now().value,
                    Semestre.actual(),
                    inicio,
                    cuatrimestreComparable.finInscripciones
                )
            }

            return if (fin != null) {
                checkFechas(cuatrimestreComparable.inicioInscripciones, fin)
                Cuatrimestre(
                    Year.now().value,
                    Semestre.actual(),
                    cuatrimestreComparable.inicioInscripciones,
                    fin
                )
            } else {
                cuatrimestreComparable
            }
        }

        private fun checkFechas(inicio: LocalDateTime, fin: LocalDateTime) {
            if (inicio > fin) throw  ErrorDeNegocio(mensajeInicioMayorAFin())
        }

        private fun mensajeInicioMayorAFin() = "La fecha de inicio no puede ser mayor que la de fin"
    }

    fun esElCuatrimestre(anio: Cuatrimestre) = this.esElCuatrimestre(anio.anio, anio.semestre)

    fun esElCuatrimestre(anio: Int, semestre: Semestre) =
        this.anio == anio && this.semestre == semestre

    fun actualizarFechas(inicioInscripciones: LocalDateTime?, finInscripciones: LocalDateTime?) {
        inicioInscripciones?.let {
            this.chequearSiPuedeCambiarFechaInicio(it, finInscripciones)
            this.inicioInscripciones = it
        }
        finInscripciones?.let {
            this.chequearSiPuedeCambiarFechaFin(it)
            this.finInscripciones = it
        }
    }

    private fun chequearSiPuedeCambiarFechaInicio(
        fechaInicioDeseada: LocalDateTime,
        posibleFechaFin: LocalDateTime?
    ) {
        if ((posibleFechaFin != null && fechaInicioDeseada > posibleFechaFin) ||
            (posibleFechaFin == null && fechaInicioDeseada > this.finInscripciones)
        ) throw ErrorDeNegocio(mensajeInicioMayorAFin())
    }

    private fun chequearSiPuedeCambiarFechaFin(fechaFinDeseada: LocalDateTime) {
        if (fechaFinDeseada < this.inicioInscripciones) {
            throw ErrorDeNegocio("La fecha de fin no puede ser menor a la de inicio")
        }
    }
}