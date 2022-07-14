package ar.edu.unq.postinscripciones.service

import ar.edu.unq.postinscripciones.model.Carrera
import ar.edu.unq.postinscripciones.model.CicloLI
import ar.edu.unq.postinscripciones.model.CicloTPI
import ar.edu.unq.postinscripciones.model.EstadoSolicitud
import ar.edu.unq.postinscripciones.model.comision.Comision
import ar.edu.unq.postinscripciones.model.comision.Dia
import ar.edu.unq.postinscripciones.model.comision.Modalidad
import ar.edu.unq.postinscripciones.model.cuatrimestre.Cuatrimestre
import ar.edu.unq.postinscripciones.model.cuatrimestre.Semestre
import ar.edu.unq.postinscripciones.model.exception.ExcepcionUNQUE
import ar.edu.unq.postinscripciones.service.dto.carga.datos.MateriaParaCargar
import ar.edu.unq.postinscripciones.service.dto.carga.datos.Plan
import ar.edu.unq.postinscripciones.service.dto.carga.datos.PlanillaMaterias
import ar.edu.unq.postinscripciones.service.dto.comision.HorarioDTO
import ar.edu.unq.postinscripciones.service.dto.formulario.*
import ar.edu.unq.postinscripciones.service.dto.materia.Correlativa
import ar.edu.unq.postinscripciones.service.dto.materia.MateriaConCorrelativas
import ar.edu.unq.postinscripciones.service.dto.materia.MateriaDTO
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime

@IntegrationTest
internal class MateriaServiceTest {

    @Autowired
    private lateinit var materiaService: MateriaService

    @Autowired
    private lateinit var dataService: DataService

    @Autowired
    private lateinit var comisionService: ComisionService

    @Autowired
    private lateinit var cuatrimestreService: CuatrimestreService

    @Autowired
    private lateinit var cargaMateriaService: CargaMateriaService

    @Autowired
    private lateinit var alumnoService: AlumnoService
    private lateinit var bdd: MateriaDTO
    private lateinit var algo: MateriaDTO
    private lateinit var comision: Comision
    private lateinit var comision2: Comision
    private lateinit var cuatrimestre: Cuatrimestre

    @BeforeEach
    fun setUp() {
        bdd = materiaService.crear("Base de datos", "BD-096", mutableListOf(), Carrera.PW)
        algo = materiaService.crear("Algoritmos", "AA-208", mutableListOf(), Carrera.PW)

        val formularioCuatrimestre = FormularioCuatrimestre(2022, Semestre.S1)
        cuatrimestre = cuatrimestreService.crear(formularioCuatrimestre)

        var horarios = listOf(
            HorarioDTO(Dia.Lun, "18:30", "21:30"),
            HorarioDTO(Dia.Jue, "18:30", "21:30")
        )
        val formulario = FormularioComision(
            1,
            bdd.codigo,
            2022,
            Semestre.S1,
            35,
            5,
            horarios,
            Modalidad.PRESENCIAL
        )
        comision = comisionService.crear(formulario)
        horarios = listOf(
            HorarioDTO(Dia.Lun, "18:30", "21:30"),
            HorarioDTO(Dia.Jue, "18:30", "21:30")
        )
        val formulario2 = FormularioComision(
            2,
            algo.codigo,
            2022,
            Semestre.S1,
            35,
            5,
            horarios,
            Modalidad.PRESENCIAL
        )
        comision2 = comisionService.crear(formulario2)
    }

    @Test
    fun `Se puede crear una materia`() {
        val materia = materiaService.crear("Intro", "IP-102", mutableListOf(), Carrera.PW)
        assertThat(materia).isNotNull
    }

    @Test
    fun `no se puede crear una materia con un nombre existente`() {
        val materia = materiaService.crear("Intro", "IP-102", mutableListOf(), Carrera.PW)
        assertThat(materia).isNotNull
    }

    @Test
    fun `no se puede crear una materia con un codigo o nombre existente`() {
        val materia = materiaService.crear("Intro", "IP-102", mutableListOf(), Carrera.PW)
        val nombreConflictivo = materia.nombre.lowercase()
        val codigoConflictivo = materia.codigo.lowercase()
        val excepcion = assertThrows<ExcepcionUNQUE> {
            materiaService.crear(
                nombreConflictivo,
                codigoConflictivo,
                mutableListOf(),
                Carrera.PW
            )
        }

        assertThat(excepcion.message).isEqualTo(
            "La materia que desea crear con nombre $nombreConflictivo " +
                    "y codigo $codigoConflictivo, " +
                    "genera conflicto con la materia: ${materia.nombre}, codigo: ${materia.codigo}"
        )
    }

    @Test
    fun `se puede crear una materia con una correlativa`() {
        val materia = materiaService.crear("Orga", "ORGA-101", mutableListOf("BD-096"), Carrera.PW)
        assertThat(materia.correlativas.first()).isEqualTo(bdd.nombre)
    }

    @Test
    fun `no se puede crear una materia con una correlativa inexistente`() {
        val excepcion = assertThrows<ExcepcionUNQUE> {
            materiaService.crear(
                "Orga",
                "ORGA-101",
                mutableListOf("EPYL-103"),
                Carrera.PW
            )
        }

        assertThat(excepcion.message).isEqualTo("No existe la materia con codigo: EPYL-103")
    }

    @Test
    fun `Se puede crear una lista de materias`() {
        val intro = FormularioMateria("00487", "Introducción a la Programación", Carrera.PW)
        val orga = FormularioMateria("01032", "Organización de las Computadoras", Carrera.PW)
        materiaService.crear(listOf(intro, orga))

        assertThat(materiaService.todas().map { it.nombre }).containsAll(listOf(intro.nombre, orga.nombre))
    }

    @Test
    fun `no se puede crear una lista de materias que ya existen`() {
        val listaConflictiva = materiaService.crear(listOf(FormularioMateria("Base de datos", "BD-096", Carrera.PW)))

        assertThat(listaConflictiva.first().mensaje).isEqualTo("Conflicto con la materia ${bdd.nombre} y codigo ${bdd.codigo}")
    }

    @Test
    fun `se puede crear una lista de materias con una correlativa`() {
        val orga = FormularioMateria("01032", "Organización de las Computadoras", Carrera.PW)
        materiaService.crear(listOf(orga))
        val materiasParaActualizar = listOf(MateriaConCorrelativas(orga.codigo, listOf(Correlativa(bdd.codigo))))
        materiaService.actualizarCorrelativas(materiasParaActualizar)

        val materia = materiaService.obtener(orga.codigo)
        assertThat(materia.correlativas.first().nombre).isEqualTo(bdd.nombre)
    }

    @Test
    fun `no se puede las correlativas de una materia con un codigo inexistente`() {
        val orga = FormularioMateria("01032", "Organización de las Computadoras", Carrera.PW)
        materiaService.crear(listOf(orga))

        val resultado = materiaService
            .actualizarCorrelativas(
                listOf(
                    MateriaConCorrelativas(orga.codigo, listOf(Correlativa("NO EXISTE")))
                )
            )

        assertThat(resultado.first().mensaje).isEqualTo("No se encontró la correlativa")
    }

    @Test
    fun `Se pueden obtener todas las materias registradas`() {
        val materiasEsperadas = arrayListOf(bdd, algo)

        assertThat(materiaService.todas()).usingRecursiveComparison().isEqualTo(materiasEsperadas)
    }

    @Test
    fun `Se puede obtener una materia especifica`() {
        val materiaEncontrada = materiaService.obtener(bdd.codigo)

        assertThat(materiaEncontrada.nombre).isEqualTo(bdd.nombre)
    }

    @Test
    fun `No se puede obtener una materia que no existe`() {
        val codigoInexistente = "AA-207"
        val exception = assertThrows<ExcepcionUNQUE> { materiaService.obtener(codigoInexistente) }

        assertThat(exception.message).isEqualTo("La materia con codigo $codigoInexistente no se encuentra registrada en el sistema")
    }

    @Test
    fun `Se puede actualizar las materias correlativas de una materia`() {
        val materia = materiaService.crear("Orga", "ORGA-101", mutableListOf("BD-096"), Carrera.PW)
        val materiasParaActualizar = listOf(MateriaConCorrelativas(materia.codigo, listOf(Correlativa(algo.codigo))))
        materiaService.actualizarCorrelativas(materiasParaActualizar)

        val materiaActualizada = materiaService.obtener(materia.codigo)

        assertThat(materiaActualizada.correlativas.first().nombre).isEqualTo(algo.nombre)
        assertThat(materiaActualizada.correlativas).hasSize(1)
    }

    @Test
    fun `Obtener comisiones ordenadas por cantidad de solicitudes pendientes`() {
        val alumno1 =
            alumnoService.crear(FormularioCrearAlumno(4235, "", "", "", 12341, Carrera.W, 0.0))
        val alumno2 =
            alumnoService.crear(FormularioCrearAlumno(42355, "", "", "", 12331, Carrera.W, 0.0))

        val formulario =
            alumnoService.guardarSolicitudPara(alumno1.dni, listOf(comision.id!!, comision2.id!!), cuatrimestre)
        alumnoService.guardarSolicitudPara(alumno2.dni, listOf(comision.id!!), cuatrimestre)

        comisionService.subirOferta(
            listOf(),
            LocalDateTime.now().minusDays(1),
            LocalDateTime.now().minusDays(1),
            cuatrimestre
        )
        alumnoService.cambiarEstadoSolicitud(
            formulario.solicitudes.first().id,
            EstadoSolicitud.APROBADO,
            formulario.id
        )
        alumnoService.cambiarEstadoSolicitud(
            formulario.solicitudes.last().id,
            EstadoSolicitud.APROBADO,
            formulario.id
        )
        val materiasObtenidas = materiaService.materiasPorSolicitudes(cuatrimestre, "")

        assertThat(materiasObtenidas.maxOf { it.cantidadSolicitudesPendientes }).isEqualTo(materiasObtenidas.first().cantidadSolicitudesPendientes)
        assertThat(materiasObtenidas.minOf { it.cantidadSolicitudesPendientes }).isEqualTo(materiasObtenidas.last().cantidadSolicitudesPendientes)
        assertThat(materiasObtenidas.first().cantidadSolicitudesPendientes).isEqualTo(1)
        assertThat(materiasObtenidas.first().codigo).isEqualTo(comision.materia.codigo)
        assertThat(materiasObtenidas.last().cantidadSolicitudesPendientes).isEqualTo(0)
    }

    @Test
    fun `se puede modificar una materia ya creada`() {
        val formulario = FormularioModificarMateria("Algoritmoss", algo.codigo, Carrera.W)
        val nuevaMateria = materiaService.modificar(formulario)

        val materiaPersistida = materiaService.obtener(algo.codigo)

        assertThat(nuevaMateria).usingRecursiveComparison().isNotEqualTo(algo)
        assertThat(nuevaMateria.nombre).isEqualTo(materiaPersistida.nombre)
        assertThat(nuevaMateria.carrera).isEqualTo(materiaPersistida.carrera())
    }

    @Test
    fun `se carga una materia para el plan de licenciatura y con requisitos de ciclo`() {
        val codigo = "111"
        cargaMateriaService.cargarMaterias(
            PlanillaMaterias(
                Plan.LI,
                listOf(
                    MateriaParaCargar(
                        CicloTPI.NO_PERTENECE,
                        CicloLI.NBW,
                        codigo,
                        8,
                        "Objetos I",
                        listOf(bdd.codigo),
                        ci = 30,
                        nbw = 70,
                        cb = 101,
                        fila = 123
                    )
                )
            )
        )

        val materia = materiaService.obtener(codigo)
        val correlativas = materia.correlativas
        val requisitos = materia.requisitosCiclo

        assertThat(correlativas).hasSize(1)
        assertThat(correlativas.all { it.codigo == bdd.codigo }).isTrue
        assertThat(requisitos).hasSize(3)
        assertThat(requisitos.any { it.cicloLI == CicloLI.CI && it.creditos == 30 }).isTrue
        assertThat(requisitos.any { it.cicloLI == CicloLI.NBW && it.creditos == 70 }).isTrue
        assertThat(requisitos.any { it.cicloLI == CicloLI.CB && it.creditos == 101 }).isTrue
    }

    @Test
    fun `se carga una materia para el plan de tpi 2010 y con requisitos de ciclo`() {
        val codigo = "111"
        cargaMateriaService.cargarMaterias(
            PlanillaMaterias(
                Plan.TPI2010,
                listOf(
                    MateriaParaCargar(
                        CicloTPI.OR,
                        CicloLI.NO_PERTENECE,
                        codigo,
                        8,
                        "TIP",
                        listOf(),
                        co = 30,
                        ca = 70,
                        fila = 123
                    )
                )
            )
        )

        val materia = materiaService.obtener(codigo)
        val correlativas = materia.correlativas
        val requisitos = materia.requisitosCiclo

        assertThat(correlativas).hasSize(0)
        assertThat(requisitos).hasSize(2)
        assertThat(requisitos.all { it.esTPI2010 }).isTrue
        assertThat(requisitos.any { it.cicloTPI == CicloTPI.CO && it.creditos == 30 }).isTrue
        assertThat(requisitos.any { it.cicloTPI == CicloTPI.CA && it.creditos == 70 }).isTrue
    }

    @Test
    fun `se carga una materia para el plan de tpi 2015 y con requisitos de ciclo`() {
        val codigo = "111"
        cargaMateriaService.cargarMaterias(
            PlanillaMaterias(
                Plan.TPI2015,
                listOf(
                    MateriaParaCargar(
                        CicloTPI.OR,
                        CicloLI.NO_PERTENECE,
                        codigo,
                        8,
                        "TIP",
                        listOf(),
                        ci = 30,
                        cc = 21,
                        co = 101,
                        ca = 70,
                        fila = 123
                    )
                )
            )
        )

        val materia = materiaService.obtener(codigo)
        val requisitos = materia.requisitosCiclo

        assertThat(requisitos).hasSize(4)
        assertThat(requisitos.all { it.esTPI2010 }).isFalse
        assertThat(requisitos.any { it.cicloTPI == CicloTPI.CI && it.creditos == 30 }).isTrue
        assertThat(requisitos.any { it.cicloTPI == CicloTPI.CO && it.creditos == 101 }).isTrue
        assertThat(requisitos.any { it.cicloTPI == CicloTPI.CA && it.creditos == 70 }).isTrue
        assertThat(requisitos.any { it.cicloTPI == CicloTPI.CC && it.creditos == 21 }).isTrue
    }


    @Test
    fun `se carga una materia y si existe se notifica que se actualizaran sus datos`() {
        val codigo = "111"
        cargaMateriaService.cargarMaterias(
            PlanillaMaterias(
                Plan.LI,
                listOf(
                    MateriaParaCargar(
                        CicloTPI.NO_PERTENECE,
                        CicloLI.NBW,
                        codigo,
                        8,
                        "Objetos I",
                        listOf(bdd.codigo),
                        ci = 30,
                        nbw = 70,
                        cb = 101,
                        fila = 123
                    )
                )
            )
        )

        val conflictos = cargaMateriaService.cargarMaterias(
            PlanillaMaterias(
                Plan.TPI2015,
                listOf(
                    MateriaParaCargar(
                        CicloTPI.CO,
                        CicloLI.NO_PERTENECE,
                        codigo,
                        8,
                        "Objetos I",
                        listOf(bdd.codigo),
                        ci = 30,
                        fila = 98
                    )
                )
            )
        )

        val materia = materiaService.obtener(codigo)

        assertThat(materia.carrera()).isEqualTo(Carrera.PW)
        assertThat(conflictos).hasSize(1)
        assertThat(conflictos.first().fila).isEqualTo(98)
        assertThat(conflictos.first().mensaje).isEqualTo("La materia $codigo existe y se actualizarán sus datos")
    }

    @Test
    fun `se carga una materia y alguna de sus correlativas no existe se notifica`() {
        val codigo = "111"
        val conflictos = cargaMateriaService.cargarMaterias(
            PlanillaMaterias(
                Plan.TPI2015,
                listOf(
                    MateriaParaCargar(
                        CicloTPI.CO,
                        CicloLI.NO_PERTENECE,
                        codigo,
                        8,
                        "Objetos I",
                        listOf("NOEXISTE"),
                        ci = 30,
                        fila = 103
                    )
                )
            )
        )

        val materia = materiaService.obtener(codigo)

        assertThat(materia.correlativas).hasSize(0)
        assertThat(conflictos).hasSize(1)
        assertThat(conflictos.first().fila).isEqualTo(103)
        assertThat(conflictos.first().mensaje).isEqualTo("Hay materias correlativas que se quisieron cargar y no existen")
    }

    @AfterEach
    fun tearDown() {
        dataService.clearDataSet()
    }
}