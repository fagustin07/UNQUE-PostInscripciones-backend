package ar.edu.unq.postinscripciones.model

import ar.edu.unq.postinscripciones.helpers.ChequeadorDeMateriasDisponibles
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ChequeadorRequisitosTest {

    lateinit var chequeadorDeMateriasDisponibles: ChequeadorDeMateriasDisponibles

    @BeforeEach
    fun setup() {
        chequeadorDeMateriasDisponibles = ChequeadorDeMateriasDisponibles()
    }

    @Test
    fun `se puede saber si un alumno de la tpi  puede cursar una materia por requisitos de un ciclo`() {
        val alumno = Alumno(carrera = Carrera.P)

        val materia = Materia(codigo = "12", nombre = "materia1", tpi2015 = CicloTPI.CI, creditos = 30)
        val materiaCursada = MateriaCursada(materia, estado = EstadoMateria.APROBADO)

        val requisitoCiclo =
            RequisitoCiclo(Carrera.P, cicloTPI = CicloTPI.CI, cicloLI = CicloLI.NO_PERTENECE, creditos = 20)
        val materiaDeseada = Materia(
            codigo = "123",
            nombre = "materia2",
            requisitosCiclo = mutableSetOf(requisitoCiclo),
            tpi2015 = CicloTPI.CO,
            li = CicloLI.NO_PERTENECE
        )

        alumno.actualizarHistoriaAcademica(listOf(materiaCursada))

        val materiasDisponibles =
            chequeadorDeMateriasDisponibles.materiasQuePuedeCursar(alumno, listOf(materia, materiaDeseada))

        assertThat(materiasDisponibles).containsExactly(materiaDeseada)
    }

    @Test
    fun `se puede saber si un alumno puede cursar una materia por requisitos de materia`() {
        val alumno = Alumno(carrera = Carrera.P)

        val materia = Materia(codigo = "12", nombre = "materia1", tpi2015 = CicloTPI.CI, creditos = 30)
        val materiaCursada = MateriaCursada(materia, estado = EstadoMateria.APROBADO)

        val materiaDeseada = Materia(
            codigo = "123",
            nombre = "materia2",
            correlativas = mutableListOf(materia),
            tpi2015 = CicloTPI.CO,
            li = CicloLI.NO_PERTENECE
        )

        alumno.actualizarHistoriaAcademica(listOf(materiaCursada))

        val materiasDisponibles =
            chequeadorDeMateriasDisponibles.materiasQuePuedeCursar(alumno, listOf(materia, materiaDeseada))

        assertThat(materiasDisponibles).containsExactly(materiaDeseada)
    }

    @Test
    fun `se puede saber si un alumno puede cursar una materia por requisitos de varios ciclos`() {
        val alumno = Alumno(carrera = Carrera.P)

        val materia = Materia(codigo = "12", nombre = "materia1", tpi2015 = CicloTPI.CA, creditos = 104)
        val materiaCursada = MateriaCursada(materia, estado = EstadoMateria.APROBADO)

        val materia2 = Materia(codigo = "13", nombre = "materia2", tpi2015 = CicloTPI.CO, creditos = 100)
        val materiaCursada2 = MateriaCursada(materia2, estado = EstadoMateria.APROBADO)

        val materia3 = Materia(codigo = "14", nombre = "materia3", tpi2015 = CicloTPI.CC, creditos = 4)
        val materiaCursada3 = MateriaCursada(materia3, estado = EstadoMateria.APROBADO)

        val materia4 = Materia(codigo = "15", nombre = "materia4", tpi2015 = CicloTPI.CC, creditos = 4)
        val materiaCursada4 = MateriaCursada(materia4, estado = EstadoMateria.APROBADO)

        alumno.actualizarHistoriaAcademica(listOf(materiaCursada, materiaCursada2, materiaCursada3, materiaCursada4))

        val requisitoCicloCA =
            RequisitoCiclo(Carrera.P, cicloTPI = CicloTPI.CA, cicloLI = CicloLI.NO_PERTENECE, creditos = 104)
        val requisitoCicloCO =
            RequisitoCiclo(Carrera.P, cicloTPI = CicloTPI.CO, cicloLI = CicloLI.NO_PERTENECE, creditos = 100)
        val requisitoCicloCC =
            RequisitoCiclo(Carrera.P, cicloTPI = CicloTPI.CC, cicloLI = CicloLI.NO_PERTENECE, creditos = 8)

        val tip =
            Materia(
                codigo = "tip",
                nombre = "TIP",
                requisitosCiclo = mutableSetOf(requisitoCicloCA, requisitoCicloCO, requisitoCicloCC),
                tpi2015 = CicloTPI.OR,
                li = CicloLI.NO_PERTENECE
            )

        val materiasOfertadas = listOf(materia, materia2, materia3, materia4, tip)

        val materiasDisponibles = chequeadorDeMateriasDisponibles.materiasQuePuedeCursar(alumno, materiasOfertadas)

        assertThat(materiasDisponibles).containsExactly(tip)
    }

    @Test
    fun `un alumno puede cursar una materia complementaria para su carrera ignorando las correlatividades de la misma`() {
        val alumnoTPI = Alumno(carrera = Carrera.P)
        val alumnoLI = Alumno(carrera = Carrera.W)

        val materia = Materia(codigo = "12", nombre = "materia1", tpi2015 = CicloTPI.CO, li = CicloLI.NBW, creditos = 100)
        val materiaCursada = MateriaCursada(materia, estado = EstadoMateria.APROBADO)

        val materia2 = Materia(codigo = "13", nombre = "materia2", li = CicloLI.CA)
        val materiaCursada2 = MateriaCursada(materia2, estado = EstadoMateria.AUSENTE)

        alumnoTPI.actualizarHistoriaAcademica(listOf(materiaCursada, materiaCursada2))
        alumnoLI.actualizarHistoriaAcademica(listOf(materiaCursada, materiaCursada2))

        val requisitoCicloLI = RequisitoCiclo(Carrera.W, cicloLI = CicloLI.NBW)
        val requisitoCicloTPI =
            RequisitoCiclo(Carrera.P, cicloTPI = CicloTPI.CO, cicloLI = CicloLI.NO_PERTENECE, creditos = 100)

        val sistDistr =
            Materia(
                codigo = "distr",
                nombre = "sd",
                correlativas = mutableListOf(materia2),
                requisitosCiclo = mutableSetOf(requisitoCicloLI, requisitoCicloTPI),
                tpi2015 = CicloTPI.CC,
                li = CicloLI.CA
            )

        val materiasOfertadas = listOf(materia, materia2, sistDistr)

        val materiasDisponiblesAlumnoTPI =
            chequeadorDeMateriasDisponibles.materiasQuePuedeCursar(alumnoTPI, materiasOfertadas)
        val materiasDisponiblesAlumnoLI =
            chequeadorDeMateriasDisponibles.materiasQuePuedeCursar(alumnoLI, materiasOfertadas)

        assertThat(alumnoTPI.aproboTodas(listOf(materia2))).isFalse
        assertThat(materiasDisponiblesAlumnoTPI).containsExactly(materia2, sistDistr)
        assertThat(materiasDisponiblesAlumnoLI).doesNotContain(sistDistr)
    }

    @Test
    fun `un alumno de TPI2015 no podra cursar materia por no cumplir correlativas pero uno de TPI2010 va a poder`() {
        val alumnoTPI2010 = Alumno(carrera = Carrera.PW, cursaTPI2010 = true)
        val alumnoTPI2015 = Alumno(carrera = Carrera.P, cursaTPI2010 = false)

        val materia = Materia(
            codigo = "12",
            nombre = "materia1",
            tpi2015 = CicloTPI.CO,
            tpi2010 = CicloTPI.CO,
            li = CicloLI.NBW,
            creditos = 75
        )
        val materiaCursada = MateriaCursada(materia, estado = EstadoMateria.APROBADO)

        val materia2 = Materia(codigo = "13", nombre = "materia2", li = CicloLI.CA)
        val materiaCursada2 = MateriaCursada(materia2, estado = EstadoMateria.AUSENTE)

        alumnoTPI2010.actualizarHistoriaAcademica(listOf(materiaCursada, materiaCursada2))
        alumnoTPI2015.actualizarHistoriaAcademica(listOf(materiaCursada, materiaCursada2))

        val requisitoCicloTPI2010 = RequisitoCiclo(
            Carrera.P,
            cicloTPI = CicloTPI.CO,
            cicloLI = CicloLI.NO_PERTENECE,
            creditos = 71,
            esTPI2010 = true
        )
        val requisitoCicloTPI2015 = RequisitoCiclo(
            Carrera.P,
            cicloTPI = CicloTPI.CO,
            cicloLI = CicloLI.NO_PERTENECE,
            creditos = 70,
            esTPI2010 = false
        )

        val sistDistr =
            Materia(
                codigo = "distr",
                nombre = "sd",
                correlativas = mutableListOf(materia2),
                requisitosCiclo = mutableSetOf(requisitoCicloTPI2015, requisitoCicloTPI2010),
                tpi2015 = CicloTPI.CA,
                tpi2010 = CicloTPI.CA,
                li = CicloLI.CA
            )

        val materiasOfertadas = listOf(materia, materia2, sistDistr)

        val materiasDisponiblesAlumnoTPI2010 = chequeadorDeMateriasDisponibles.materiasQuePuedeCursar(alumnoTPI2010, materiasOfertadas)
        val materiasDisponiblesAlumnoTPI2015 = chequeadorDeMateriasDisponibles.materiasQuePuedeCursar(alumnoTPI2015, materiasOfertadas)

        assertThat(materiasDisponiblesAlumnoTPI2010).containsExactly(materia2, sistDistr)
        assertThat(materiasDisponiblesAlumnoTPI2015).containsExactly(materia2)
    }
}