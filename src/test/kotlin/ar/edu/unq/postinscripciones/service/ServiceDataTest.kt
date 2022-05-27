package ar.edu.unq.postinscripciones.service

import ar.edu.unq.postinscripciones.model.Carrera
import ar.edu.unq.postinscripciones.model.comision.Dia
import ar.edu.unq.postinscripciones.model.cuatrimestre.Cuatrimestre
import ar.edu.unq.postinscripciones.model.cuatrimestre.Semestre
import ar.edu.unq.postinscripciones.model.exception.ExcepcionUNQUE
import ar.edu.unq.postinscripciones.service.dto.ComisionACrear
import ar.edu.unq.postinscripciones.service.dto.FormularioCrearAlumno
import ar.edu.unq.postinscripciones.service.dto.FormularioCuatrimestre
import ar.edu.unq.postinscripciones.service.dto.HorarioDTO
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired

@IntegrationTest
internal class ServiceDataTest {
    @Autowired
    private lateinit var cuatrimestreService: CuatrimestreService

    @Autowired
    private lateinit var comisionService: ComisionService

    @Autowired
    lateinit var materiaService: MateriaService

    @Autowired
    private lateinit var alumnoService: AlumnoService

    @Autowired
    private lateinit var dataService: DataService

    @Test
    fun `inicialmente no hay materias en el sistema`() {
        assertThat(materiaService.todas()).isEmpty()
    }

    @Test
    fun `se puede registrar una lista de alumnos`() {
        val planillaAlumnos: List<FormularioCrearAlumno> = generarAlumnos()

        alumnoService.registrarAlumnos(planillaAlumnos)

        val alumnosRegistrados = alumnoService.todos()
        assertThat(alumnosRegistrados.size).isEqualTo(planillaAlumnos.size)
        assertThat(alumnosRegistrados)
            .usingRecursiveComparison()
            .ignoringFields("formularios")
            .isEqualTo(planillaAlumnos)

    }

    @Test
    fun `al crear una lista de alumnos, si un alumno no se pudo registrar, lo retorna en una lista como datos conflictivo`() {
        val planillaAlumnos: List<FormularioCrearAlumno> = generarAlumnos()
        val otraPlanilla: MutableList<FormularioCrearAlumno> = generarAlumnos(100).toMutableList()
        otraPlanilla.add(planillaAlumnos.first())

        alumnoService.registrarAlumnos(planillaAlumnos)

        val cargaOtraPlanilla = alumnoService.registrarAlumnos(otraPlanilla)

        assertThat(cargaOtraPlanilla).hasSize(1)
    }

    @Test
    fun `al registrar un listado de alumnos, obtenemos los generaron conflictos por dni o legajo con los ya creados`() {
        val planillaAlumnos: List<FormularioCrearAlumno> = generarAlumnos()
        val otraPlanilla: List<FormularioCrearAlumno> = listOf(planillaAlumnos.first(), planillaAlumnos.last())
        val cargaDePrimeraPlanilla = alumnoService.registrarAlumnos(planillaAlumnos)

        val cargaDeSegundaPlanilla = alumnoService.registrarAlumnos(otraPlanilla)

        assertThat(cargaDePrimeraPlanilla).isEmpty()
        assertThat(cargaDeSegundaPlanilla).hasSize(2)
        assertThat(alumnoService.todos()).hasSize(planillaAlumnos.size)
    }

    @Test
    fun `si hay conflictos dentro de la lista a guardar se guarda al primero y al segundo se lo retorna como conflictivo`() {
        val planillaAlumnos: MutableList<FormularioCrearAlumno> = generarAlumnos().toMutableList()
        planillaAlumnos.add(planillaAlumnos.first())
        val cargaDePrimeraPlanilla = alumnoService.registrarAlumnos(planillaAlumnos)

        assertThat(cargaDePrimeraPlanilla).hasSize(1)
        assertThat(cargaDePrimeraPlanilla.first().formularioConflictivo).isEqualTo(planillaAlumnos.first())
        assertThat(alumnoService.todos()).hasSize(planillaAlumnos.size - 1)
    }

    @Test
    fun `se puede guardar una planilla de oferta de comisiones para un cuatrimestre`() {
        val formularioCuatrimestre = FormularioCuatrimestre(2022, Semestre.S1)
        val cuatri = cuatrimestreService.crear(formularioCuatrimestre)
        val bdd = materiaService.crear("Bases de Datos", "BD", mutableListOf(), Carrera.SIMULTANEIDAD)

        comisionService.actualizarOfertaAcademica(
            listOf(
                ComisionACrear(
                    1,
                    bdd.codigo,
                    listOf(HorarioDTO(Dia.LUNES, "18:00", "21:00")),
                    30,
                    8
                ),
                ComisionACrear(
                    2,
                    bdd.codigo,
                    listOf(HorarioDTO(Dia.MIERCOLES, "15:00", "18:00")),
                    30,
                    8
                )
            ),
            cuatrimestre = cuatri
        )

        val ofertaDelCuatrimestre = comisionService.ofertaDelCuatrimestre(cuatri)

        assertThat(ofertaDelCuatrimestre).hasSize(2)
        assertThat(ofertaDelCuatrimestre).allMatch { it.materia == bdd.nombre }
    }

    @Test
    fun `se puede guardar una planilla y se obtienen las comisiones conflictivas por cuatrimestre, materia y numero`() {
        val formularioCuatrimestre = FormularioCuatrimestre(2022, Semestre.S1)
        val cuatri = cuatrimestreService.crear(formularioCuatrimestre)
        val bdd = materiaService.crear("Bases de Datos", "BD", mutableListOf(), Carrera.SIMULTANEIDAD)
        val crearBdd = ComisionACrear(
            1,
            bdd.codigo,
            listOf(),
            30,
            8
        )
        comisionService.actualizarOfertaAcademica(
            listOf(
                crearBdd,
                ComisionACrear(
                    2,
                    bdd.codigo,
                    listOf(HorarioDTO(Dia.MIERCOLES, "15:00", "18:00")),
                    30,
                    8
                )
            ),
            cuatrimestre = cuatri
        )

        val comisionesGuardadasConConflicto = comisionService
            .actualizarOfertaAcademica(listOf(crearBdd), cuatrimestre = cuatri)

        assertThat(comisionesGuardadasConConflicto).hasSize(1)
        assertThat(comisionesGuardadasConConflicto.first().formularioConflictivo).isEqualTo(crearBdd)
    }

    @Test
    fun `si no se aclara un cuatrimestre al subir la oferta academica, se crearan en el cuatrimestre actual`() {
        val bdd = materiaService.crear("Bases de Datos", "BD", mutableListOf(), Carrera.SIMULTANEIDAD)

        comisionService.actualizarOfertaAcademica(
            listOf(
                ComisionACrear(
                    1,
                    bdd.codigo,
                    listOf(HorarioDTO(Dia.LUNES, "18:00", "21:00")),
                    30,
                    8
                ),
            )
        )

        val ofertaDelCuatrimestre = comisionService.ofertaDelCuatrimestre()

        assertThat(ofertaDelCuatrimestre).hasSize(1)
        assertThat(ofertaDelCuatrimestre).allMatch { it.materia == bdd.nombre }
    }

    @Test
    fun `si al pedir la oferta de un cuatrimestre no tiene al menos una oferta se levanta una excepcion`() {
        val excepcion = assertThrows<ExcepcionUNQUE> { comisionService.ofertaDelCuatrimestre() }
        val cuatrimestre = Cuatrimestre.actual()
        assertThat(excepcion.message)
            .isEqualTo("No hay oferta registrada para el cuatrimestre ${cuatrimestre.anio}, ${cuatrimestre.semestre}")
    }

    private fun generarAlumnos(prefijo: Int = 1): List<FormularioCrearAlumno> {
        val planilla = mutableListOf<FormularioCrearAlumno>()
        repeat(10) {
            planilla.add(
                FormularioCrearAlumno(
                    prefijo + planilla.size, "pepe", "soria", "correo" + planilla.size + "@ejemplo.com",
                    prefijo + planilla.size, "asdas", Carrera.TPI, listOf()
                )
            )
        }
        return planilla
    }

    @AfterEach
    fun tearDown() {
        dataService.clearDataSet()
    }
}