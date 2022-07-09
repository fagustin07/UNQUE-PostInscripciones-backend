package ar.edu.unq.postinscripciones.service

import ar.edu.unq.postinscripciones.model.Carrera
import ar.edu.unq.postinscripciones.model.EstadoMateria
import ar.edu.unq.postinscripciones.model.comision.Modalidad
import ar.edu.unq.postinscripciones.model.cuatrimestre.Semestre
import ar.edu.unq.postinscripciones.service.dto.alumno.AlumnoMateriaCursada
import ar.edu.unq.postinscripciones.service.dto.formulario.FormularioComision
import ar.edu.unq.postinscripciones.service.dto.formulario.FormularioCrearAlumno
import ar.edu.unq.postinscripciones.service.dto.formulario.FormularioCuatrimestre
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDate
import java.time.Year

@IntegrationTest
class CargaHistoriaAcademicaTest {
    @Autowired
    private lateinit var alumnoService: AlumnoService

    @Autowired
    private lateinit var cuatrimestreService: CuatrimestreService

    @Autowired
    private lateinit var materiaService: MateriaService

    @Autowired
    private lateinit var comisionService: ComisionService

    @Test
    fun `se puede cargar historia academica a un alumno`() {
        val alumno = alumnoService.crear(
            FormularioCrearAlumno(
                12345678,
                "fede",
                "sanchez",
                "fede@test.com",
                12345,
                Carrera.P,
                3.22
            )
        )
        val materia = materiaService.crear("Funcional", "FUN-205", mutableListOf(), Carrera.PW)
        val otraMateria = materiaService.crear("BD", "BD-205", mutableListOf(), Carrera.PW)

        alumnoService.subirHistoriaAcademica(
            listOf(
                AlumnoMateriaCursada(
                    alumno.dni,
                    materia.codigo,
                    LocalDate.of(2021, 10, 12),
                    EstadoMateria.APROBADO,
                    2132
                ),
                AlumnoMateriaCursada(
                    alumno.dni,
                    otraMateria.codigo,
                    LocalDate.of(2021, 10, 12),
                    EstadoMateria.APROBADO,
                    2133
                )
            )
        )

        val cursadasAlumno = alumnoService.buscarAlumno(alumno.dni).historiaAcademica

        assertThat(cursadasAlumno).hasSize(2)
        assertThat(cursadasAlumno.first().materia.codigo).isEqualTo(materia.codigo)
        assertThat(cursadasAlumno.last().materia.codigo).isEqualTo(otraMateria.codigo)
    }


    @Test
    fun `si se carga un resultado de cursada ya cargado , se ignora`() {
        val alumno = alumnoService.crear(
            FormularioCrearAlumno(
                12345678,
                "fede",
                "sanchez",
                "fede@test.com",
                12345,
                Carrera.P,
                3.22
            )
        )
        val materia = materiaService.crear("Funcional", "FUN-205", mutableListOf(), Carrera.PW)

        alumnoService.subirHistoriaAcademica(
            listOf(
                AlumnoMateriaCursada(
                    alumno.dni,
                    materia.codigo,
                    LocalDate.of(2021, 10, 12),
                    EstadoMateria.AUSENTE,
                    2132
                ),
                AlumnoMateriaCursada(
                    alumno.dni,
                    materia.codigo,
                    LocalDate.of(2021, 10, 12),
                    EstadoMateria.AUSENTE,
                    2133
                )
            )
        )

        val cursadasAlumno = alumnoService.buscarAlumno(alumno.dni).historiaAcademica

        assertThat(cursadasAlumno).hasSize(1)
        assertThat(cursadasAlumno.first().materia.codigo).isEqualTo(materia.codigo)
    }

    @Test
    fun `se pueden cargar varios resultados de cursadas iguales mientras tengan distintas fechas`() {
        val alumno = alumnoService.crear(
            FormularioCrearAlumno(
                12345678,
                "fede",
                "sanchez",
                "fede@test.com",
                12345,
                Carrera.P,
                3.22
            )
        )
        val materia = materiaService.crear("Funcional", "FUN-205", mutableListOf(), Carrera.PW)

        alumnoService.subirHistoriaAcademica(
            listOf(
                AlumnoMateriaCursada(
                    alumno.dni,
                    materia.codigo,
                    LocalDate.of(2021, 10, 12),
                    EstadoMateria.AUSENTE,
                    2132
                ),
                AlumnoMateriaCursada(
                    alumno.dni,
                    materia.codigo,
                    LocalDate.of(2022, 12, 12),
                    EstadoMateria.AUSENTE,
                    2133
                )
            )
        )

        val cursadasAlumno = alumnoService.buscarAlumno(alumno.dni).historiaAcademica

        assertThat(cursadasAlumno).hasSize(2)
        assertThat(cursadasAlumno.all { it.materia.codigo == materia.codigo && it.estado == EstadoMateria.AUSENTE }).isTrue
    }

    @Test
    fun `un alumno no puede tener mas de una vez aprobada una materia, sin importar las fechas`() {
        val alumno = alumnoService.crear(
            FormularioCrearAlumno(
                12345678,
                "fede",
                "sanchez",
                "fede@test.com",
                12345,
                Carrera.P,
                3.22
            )
        )
        val materia = materiaService.crear("Funcional", "FUN-205", mutableListOf(), Carrera.PW)

        alumnoService.subirHistoriaAcademica(
            listOf(
                AlumnoMateriaCursada(
                    alumno.dni,
                    materia.codigo,
                    LocalDate.of(2021, 10, 12),
                    EstadoMateria.APROBADO,
                    2132
                ),
                AlumnoMateriaCursada(
                    alumno.dni,
                    materia.codigo,
                    LocalDate.of(2021, 12, 12),
                    EstadoMateria.APROBADO,
                    2133
                )
            )
        )

        val cursadasAlumno = alumnoService.buscarAlumno(alumno.dni).historiaAcademica

        assertThat(cursadasAlumno).hasSize(1)
        assertThat(cursadasAlumno.first().materia.codigo).isEqualTo(materia.codigo)
    }

    @Test
    fun `un alumno si se cargan distintos resultados para una fecha, solo queda la ultima cargada`() {
        val alumno = alumnoService.crear(
            FormularioCrearAlumno(
                12345678,
                "fede",
                "sanchez",
                "fede@test.com",
                12345,
                Carrera.P,
                3.22
            )
        )
        val materia = materiaService.crear("Funcional", "FUN-205", mutableListOf(), Carrera.PW)

        alumnoService.subirHistoriaAcademica(
            listOf(
                AlumnoMateriaCursada(
                    alumno.dni,
                    materia.codigo,
                    LocalDate.of(2021, 10, 12),
                    EstadoMateria.DESAPROBADO,
                    2132
                )
            )
        )

        alumnoService.subirHistoriaAcademica(
            listOf(
                AlumnoMateriaCursada(
                    alumno.dni,
                    materia.codigo,
                    LocalDate.of(2021, 10, 12),
                    EstadoMateria.APROBADO,
                    123
                )
            )
        )
        val materia2 = materiaService.crear("asdasdsa", "BD-205", mutableListOf(), Carrera.PW)

        val anio = Year.now().value
        val semestre = Semestre.actual()
        cuatrimestreService.crear(FormularioCuatrimestre(anio, semestre))
        val comision = comisionService.crear(
            FormularioComision(
                1, materia2.codigo, anio, semestre, 30, 5, listOf(),Modalidad.VIRTUAL_ASINCRONICA
            )
        )

        alumnoService.guardarSolicitudPara(
            alumno.dni, listOf(comision.id!!)
        )

        val cursadasAlumno = alumnoService.obtenerResumenAlumno(alumno.dni).resumenCursadas

        assertThat(cursadasAlumno).hasSize(1)
        assertThat(cursadasAlumno.first().codigoMateria).isEqualTo(materia.codigo)
        assertThat(cursadasAlumno.first().cantidadDeVecesCursada).isEqualTo(1)
        assertThat(cursadasAlumno.first().estado).isEqualTo(EstadoMateria.APROBADO)
    }

    @AfterEach
    fun tearDown() {
        materiaService.borrarTodos()
        alumnoService.borrarTodos()
        cuatrimestreService.borrarTodos()
    }
}