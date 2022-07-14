package ar.edu.unq.postinscripciones.service

import ar.edu.unq.postinscripciones.model.Carrera
import ar.edu.unq.postinscripciones.model.comision.Comision
import ar.edu.unq.postinscripciones.model.comision.Dia
import ar.edu.unq.postinscripciones.model.comision.Modalidad
import ar.edu.unq.postinscripciones.model.cuatrimestre.Cuatrimestre
import ar.edu.unq.postinscripciones.model.cuatrimestre.Semestre
import ar.edu.unq.postinscripciones.model.exception.ExcepcionUNQUE
import ar.edu.unq.postinscripciones.service.dto.carga.datos.ComisionNueva
import ar.edu.unq.postinscripciones.service.dto.carga.datos.Locacion
import ar.edu.unq.postinscripciones.service.dto.comision.ComisionDTO
import ar.edu.unq.postinscripciones.service.dto.comision.HorarioDTO
import ar.edu.unq.postinscripciones.service.dto.formulario.FormularioComision
import ar.edu.unq.postinscripciones.service.dto.formulario.FormularioCrearAlumno
import ar.edu.unq.postinscripciones.service.dto.formulario.FormularioCuatrimestre
import ar.edu.unq.postinscripciones.service.dto.materia.MateriaDTO
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime

@IntegrationTest
internal class ComisionServiceTest {

    @Autowired
    private lateinit var comisionService: ComisionService

    @Autowired
    private lateinit var cuatrimestreService: CuatrimestreService

    @Autowired
    private lateinit var materiaService: MateriaService

    @Autowired
    private lateinit var alumnoService: AlumnoService

    @Autowired
    private lateinit var dataService: DataService

    private lateinit var bdd: MateriaDTO
    private lateinit var cuatrimestre: Cuatrimestre
    private lateinit var horarios: List<HorarioDTO>
    private lateinit var comision: Comision
    private lateinit var comision2: Comision
    private lateinit var comision3: Comision

    @BeforeEach
    fun setUp() {
        val alumno =
            alumnoService.crear(FormularioCrearAlumno(123312, "", "", "", 1234, Carrera.W, 5.0))
        val alumno2 =
            alumnoService.crear(FormularioCrearAlumno(1233123, "", "", "", 12345, Carrera.W, 5.0))

        bdd = materiaService.crear("Base de datos", "BBD-208", mutableListOf(), Carrera.PW)
        val formularioCuatrimestre = FormularioCuatrimestre(2022, Semestre.S1)
        cuatrimestre = cuatrimestreService.crear(formularioCuatrimestre)

        horarios = listOf(
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
            bdd.codigo,
            2022,
            Semestre.S1,
            35,
            5,
            horarios,
            Modalidad.PRESENCIAL
        )
        val formulario3 = FormularioComision(
            3,
            bdd.codigo,
            2022,
            Semestre.S1,
            35,
            5,
            horarios,
            Modalidad.PRESENCIAL
        )
        comision2 = comisionService.crear(formulario2)
        comision3 = comisionService.crear(formulario3)
        alumnoService.guardarSolicitudPara(alumno.dni, listOf(comision.id!!, comision2.id!!), cuatrimestre)
        alumnoService.guardarSolicitudPara(alumno2.dni, listOf(comision2.id!!), cuatrimestre)

    }

    @Test
    fun `Se puede crear una comision`() {
        assertThat(comision).isNotNull
    }

    @Test
    fun `Se puede pedir todas las comisiones de una materia`() {
        assertThat(comisionService.obtenerComisionesMateria(bdd.codigo).first()).usingRecursiveComparison()
            .isEqualTo(ComisionDTO.desdeModelo(comision))
    }

    @Test
    fun `Se puede obtener una comision especifica`() {
        val comisionObtenida = comisionService.obtener(comision.id!!)

        assertThat(comisionObtenida).usingRecursiveComparison().isEqualTo(ComisionDTO.desdeModelo(comision))
    }

    @Test
    fun `No se puede obtener una comision que no existe`() {
        val idInexistente: Long = 99999
        val exception = assertThrows<ExcepcionUNQUE> { comisionService.obtener(idInexistente) }

        assertThat(exception.message).isEqualTo("La comision con id $idInexistente no existe")
    }

    @Test
    fun `No se pueden obtener las comisiones de una materia que no existe`() {
        val codigoInexistente = "AA-209"
        val exception = assertThrows<ExcepcionUNQUE> { comisionService.obtenerComisionesMateria(codigoInexistente) }

        assertThat(exception.message).isEqualTo("La materia con codigo $codigoInexistente no se encuentra registrada en el sistema")
    }

    @Test
    fun `se puede guardar una oferta academica con un inicio y un fin para registrar formularios`() {
        val bdd = materiaService.crear("Bases de Datos", "BD", mutableListOf(), Carrera.PW)
        val miCuatrimestre = cuatrimestreService.crear(FormularioCuatrimestre(2023, Semestre.S1))
        val inicioInscripciones = LocalDateTime.of(2023, 3, 1, 12, 30)
        val finInscripciones = LocalDateTime.of(2023, 3, 16, 12, 30)

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
            ),
            inicioInscripciones,
            finInscripciones,
            miCuatrimestre
        )

        val cuatrimestreLuegoDeActualizar = cuatrimestreService.obtener(miCuatrimestre)

        assertThat(cuatrimestreLuegoDeActualizar.inicioInscripciones).isAfter(miCuatrimestre.inicioInscripciones)
        assertThat(cuatrimestreLuegoDeActualizar.finInscripciones).isAfter(miCuatrimestre.finInscripciones)
        assertThat(cuatrimestreLuegoDeActualizar.inicioInscripciones).isEqualTo(inicioInscripciones)
        assertThat(cuatrimestreLuegoDeActualizar.finInscripciones).isEqualTo(finInscripciones)
    }

    @Test
    fun `se puede actualizar solo una fecha para aceptar formularios del cuatrimestre `() {
        val miCuatrimestre = cuatrimestreService.crear(FormularioCuatrimestre(2023, Semestre.S1))
        val finInscripciones = LocalDateTime.of(2023, 3, 16, 12, 30)

        comisionService.subirOferta(
            listOf(),
            null,
            finInscripciones,
            miCuatrimestre
        )

        val cuatrimestreLuegoDeActualizar = cuatrimestreService.obtener(miCuatrimestre)

        assertThat(cuatrimestreLuegoDeActualizar.inicioInscripciones).isEqualTo(miCuatrimestre.inicioInscripciones)
        assertThat(cuatrimestreLuegoDeActualizar.finInscripciones).isNotEqualTo(miCuatrimestre.finInscripciones)
        assertThat(cuatrimestreLuegoDeActualizar.finInscripciones).isEqualTo(finInscripciones)
    }

    @Test
    fun `se puede actualizar la fecha para aceptar formularios del cuatrimestre actual`() {
        val inicioInscripciones = LocalDateTime.of(2023, 3, 1, 12, 30)
        val finInscripciones = LocalDateTime.of(2023, 3, 16, 12, 30)

        comisionService.subirOferta(listOf(), inicioInscripciones, finInscripciones)

        val cuatrimestreActuaActualizado = cuatrimestreService.obtener()
        assertThat(cuatrimestreActuaActualizado.inicioInscripciones).isNotEqualTo(cuatrimestre.inicioInscripciones)
        assertThat(cuatrimestreActuaActualizado.inicioInscripciones).isEqualTo(inicioInscripciones)
        assertThat(cuatrimestreActuaActualizado.finInscripciones).isNotEqualTo(cuatrimestre.finInscripciones)
        assertThat(cuatrimestreActuaActualizado.finInscripciones).isEqualTo(finInscripciones)
    }

    @Test
    fun `obtener la oferta del cuatrimestre actual segun un patron de nombre de materia`() {
        val algoritmos = materiaService.crear("Algoritmos", "ALG-200", mutableListOf(), Carrera.W)
        val formulario = FormularioComision(
            1,
            algoritmos.codigo,
            2022,
            Semestre.S1,
            30,
            8,
            horarios,
            Modalidad.PRESENCIAL
        )
        val comisionExcluida = comisionService.crear(formulario)

        val ofertaObtenida = comisionService.ofertaDelCuatrimestre("DAT")

        assertThat(ofertaObtenida).contains(ComisionDTO.desdeModelo(comision), ComisionDTO.desdeModelo(comision3), ComisionDTO.desdeModelo(comision2))
        assertThat(ofertaObtenida).doesNotContain(ComisionDTO.desdeModelo(comisionExcluida))
    }

    @Test
    fun `Se puede modificar la cantidad de cupos y sobrecupos totales de una comision persisitida`() {
        val cambioDeCuposYSobrecupos =  CambioDeCuposYSobrecupos(comision.id!!, 40, 2)
        val comision = comisionService.cambiarCantidadDeCuposYSobreCupos(cambioDeCuposYSobrecupos)

        assertThat(comision.cuposTotales).isEqualTo(40)
        assertThat(comision.sobreCuposTotales).isEqualTo(2)
    }

    @AfterEach
    fun tearDown() {
        dataService.clearDataSet()
    }

}