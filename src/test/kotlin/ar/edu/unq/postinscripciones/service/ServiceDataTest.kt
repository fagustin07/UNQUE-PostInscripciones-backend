package ar.edu.unq.postinscripciones.service

import ar.edu.unq.postinscripciones.model.Carrera
import ar.edu.unq.postinscripciones.model.comision.Modalidad
import ar.edu.unq.postinscripciones.model.cuatrimestre.Semestre
import ar.edu.unq.postinscripciones.service.dto.carga.datos.ComisionNueva
import ar.edu.unq.postinscripciones.service.dto.carga.datos.Locacion
import ar.edu.unq.postinscripciones.service.dto.formulario.FormularioCrearAlumno
import ar.edu.unq.postinscripciones.service.dto.formulario.FormularioCuatrimestre
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
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
            .ignoringFields("formularios", "contrasenia", "coeficiente")
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
        assertThat(cargaDePrimeraPlanilla.first().dni).isEqualTo(planillaAlumnos.first().dni)
        assertThat(alumnoService.todos()).hasSize(planillaAlumnos.size - 1)
    }

    @Test
    fun `se puede guardar una planilla de oferta de comisiones para un cuatrimestre`() {
        val formularioCuatrimestre = FormularioCuatrimestre(2022, Semestre.S1)
        val cuatri = cuatrimestreService.crear(formularioCuatrimestre)
        val bdd = materiaService.crear("Bases de Datos", "BD", mutableListOf(), Carrera.PW)

        comisionService.subirOferta(
            listOf(
                ComisionNueva(
                    bdd.codigo,
                    "Bases de datos",
                    1,
                    Modalidad.PRESENCIAL,
                    Locacion.Bernal,
                    listOf(),
                    12
                ),
                ComisionNueva(
                    bdd.codigo,
                    "Bases de datos",
                    2,
                    Modalidad.PRESENCIAL,
                    Locacion.Bernal,
                    listOf(),
                    14
                )
            ),
            cuatrimestre = cuatri
        )

        val ofertaDelCuatrimestre = comisionService.ofertaDelCuatrimestre(cuatrimestre = cuatri)

        assertThat(ofertaDelCuatrimestre).hasSize(2)
        assertThat(ofertaDelCuatrimestre).allMatch { it.materia == bdd.nombre }
    }

    @Test
    fun `se puede guardar una planilla y se obtienen las comisiones conflictivas por fila y mensaje`() {
        val formularioCuatrimestre = FormularioCuatrimestre(2022, Semestre.S1)
        val cuatri = cuatrimestreService.crear(formularioCuatrimestre)
        val bdd = materiaService.crear("Bases de Datos", "BD", mutableListOf(), Carrera.PW)
        val crearBdd = ComisionNueva(
            bdd.codigo,
            "Bases de datos",
            1,
            Modalidad.PRESENCIAL,
            Locacion.Bernal,
            listOf(),
            12
        )
        comisionService.subirOferta(
            listOf(
                crearBdd,
                ComisionNueva(
                    bdd.codigo,
                    "Bases de datos",
                    2,
                    Modalidad.PRESENCIAL,
                    Locacion.Bernal,
                    listOf(),
                    14
                )
            ),
            cuatrimestre = cuatri
        )

        val comisionesGuardadasConConflicto = comisionService
            .subirOferta(listOf(crearBdd), cuatrimestre = cuatri)

        assertThat(comisionesGuardadasConConflicto).hasSize(1)
        assertThat(comisionesGuardadasConConflicto.first().fila).isEqualTo(12)
        assertThat(comisionesGuardadasConConflicto.first().mensaje).isEqualTo("Ya existe la comision ${crearBdd.comision}, materia ${crearBdd.actividad}")
    }

    @Test
    fun `si no se aclara un cuatrimestre al subir la oferta academica, se crearan en el cuatrimestre actual`() {
        val bdd = materiaService.crear("Bases de Datos", "BD", mutableListOf(), Carrera.PW)

        comisionService.subirOferta(
            listOf(
                ComisionNueva(
                    bdd.codigo,
                    "Bases de datos",
                    1,
                    Modalidad.PRESENCIAL,
                    Locacion.Bernal,
                    listOf(),
                    12
                ),
            )
        )

        val ofertaDelCuatrimestre = comisionService.ofertaDelCuatrimestre()

        assertThat(ofertaDelCuatrimestre).hasSize(1)
        assertThat(ofertaDelCuatrimestre).allMatch { it.materia == bdd.nombre }
    }

    private fun generarAlumnos(prefijo: Int = 1): List<FormularioCrearAlumno> {
        val planilla = mutableListOf<FormularioCrearAlumno>()
        repeat(10) {
            planilla.add(
                FormularioCrearAlumno(
                        prefijo + planilla.size, "pepe", "soria", "correo" + planilla.size + "@ejemplo.com",
                        prefijo + planilla.size, Carrera.P, 0.0
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