package ar.edu.unq.postinscripciones.webservice.config

import ar.edu.unq.postinscripciones.model.*
import ar.edu.unq.postinscripciones.model.comision.Comision
import ar.edu.unq.postinscripciones.model.comision.Dia
import ar.edu.unq.postinscripciones.model.comision.Horario
import ar.edu.unq.postinscripciones.model.cuatrimestre.Cuatrimestre
import ar.edu.unq.postinscripciones.model.cuatrimestre.Semestre
import ar.edu.unq.postinscripciones.persistence.AlumnoRepository
import ar.edu.unq.postinscripciones.persistence.ComisionRespository
import ar.edu.unq.postinscripciones.persistence.CuatrimestreRepository
import ar.edu.unq.postinscripciones.persistence.MateriaRepository
import ar.edu.unq.postinscripciones.service.AlumnoService
import ar.edu.unq.postinscripciones.service.AutenticacionService
import ar.edu.unq.postinscripciones.service.ComisionService
import ar.edu.unq.postinscripciones.service.dto.CreacionDirectivo
import ar.edu.unq.postinscripciones.service.dto.alumno.AlumnoConHistoriaAcademica
import ar.edu.unq.postinscripciones.service.dto.carga.datos.Locacion
import ar.edu.unq.postinscripciones.service.dto.formulario.FormularioCrearAlumno
import ar.edu.unq.postinscripciones.service.dto.materia.MateriaCursadaDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Profile
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Profile("!test")
@Component
class DataSeed(
    @Autowired private val materiaRepository: MateriaRepository,
    @Autowired private val cuatrimestreRepository: CuatrimestreRepository,
    @Autowired private val comisionRespository: ComisionRespository,
    @Autowired private val alumnoRepository: AlumnoRepository,
    @Autowired private val alumnoService: AlumnoService,
    @Autowired private val autenticacionService: AutenticacionService,
    @Autowired private val comisionService: ComisionService
) : CommandLineRunner {

    @Value("\${admin.password}")
    private lateinit var adminPassword: String

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Throws(Exception::class)
    override fun run(vararg args: String?) {
        loadData()
    }

    private fun loadData() {

        if (emptyData()) {
            val epyl = Materia("80005", "Elementos de Programación y Lógica", mutableListOf(), tpi2015 = CicloTPI.CI)
            val lea = Materia("80000", "Lectura y Escritura Académica", mutableListOf(), tpi2015 = CicloTPI.CI)
            val matematica = Materia("80003", "Matematica", mutableListOf(), tpi2015 = CicloTPI.CI)
            val ingles1 = Materia("90000", "Inglés 1", mutableListOf())
            val ingles2 = Materia("90028", "Inglés 2", mutableListOf())
            val ttu = Materia("752", "Taller de Trabajo Universitario", mutableListOf())
            val tti = Materia("751", "Taller de Trabajo Intelectual", mutableListOf())

            val intro = Materia("487", "Introducción a la Programación", mutableListOf(epyl), tpi2015 = CicloTPI.CO, tpi2010 = CicloTPI.CO, li = CicloLI.NBW)
            val orga = Materia("1032", "Organización de las Computadoras", mutableListOf(epyl))
            val mate1 = Materia("1033", "Matemática 1", mutableListOf(matematica))
            val objetos1 = Materia("1034", "Programación con Objetos 1", mutableListOf(intro))
            val bdd = Materia("1035", "Bases de Datos", mutableListOf(), tpi2015 = CicloTPI.CO, tpi2010 = CicloTPI.CO, li = CicloLI.NBW)
            val estructura = Materia("1036", "Estructura de Datos", mutableListOf(intro), tpi2015 = CicloTPI.CO, tpi2010 = CicloTPI.CO, li = CicloLI.NBW)
            val objetos2 =
                Materia("1037", "Programación con Objetos 2", mutableListOf(objetos1))
            val redes = Materia("1038", "Redes de Computadoras", mutableListOf(orga))
            val sistemasoperativos =
                Materia("1039", "Sistemas Operativos", mutableListOf(intro, orga))
            val concurrente =
                Materia("1040", "Programación Concurrente", mutableListOf(estructura))
            val mate2 = Materia("1041", "Matemática 2", mutableListOf(mate1))
            val elementosdeingeneria =
                Materia("1042", "Elementos de Ingenieria de Software", mutableListOf(objetos2))
            val interfaces = Materia(
                "1043",
                "Construcción de Interfaces de Usuario",
                mutableListOf(objetos2),
            )
            val persistencia =
                Materia("1044", "Estrategias de Persistencia", mutableListOf(bdd, objetos2))
            val funcional = Materia("1045", "Programación Funcional", mutableListOf(estructura))
            val desarrollo = Materia(
                "1046",
                "Desarrollo de Aplicaciones",
                mutableListOf(elementosdeingeneria, persistencia, interfaces)
            )
            val labo = Materia(
                "1047",
                "Laboratiorio de Sistemas Operativos y Redes",
                mutableListOf(redes, sistemasoperativos),
            )
            val bdd2 = Materia("1048", "Bases de Datos II", mutableListOf())
            val softwareLibre = Materia(
                "1049",
                "Participación y Gestión en Proyectos de Software Libre",
                mutableListOf(),
            )
            val introArquitectura =
                Materia("1050", "Introducción a las Arquitecturas de Software", mutableListOf())
            val objetos3 =
                Materia("1051", "Programación con Objetos 3", mutableListOf(objetos2))
            val bioinformatica =
                Materia("1052", "Introducción a la Bioinformática", mutableListOf())
            val politica = Materia(
                "1053",
                "Politicas Públicas en la Sociedad de la Información y la Era Digital",
                mutableListOf(),
            )
            val geografica =
                Materia("1054", "Sistemas de Información Geográfica", mutableListOf())
            val declarativas =
                Materia("1055", "Herramientas declarativas en Programación", mutableListOf())
            val videojuegos =
                Materia("1056", "Introducción al Desarrollo de Videojuegos", mutableListOf())
            val derechos = Materia(
                "1057",
                "Derechos de Autor y Derecho de Copia en la Era Digita",
                mutableListOf(),
            )
            val arduino = Materia(
                "1058",
                "Seminarios: Introducción a la Electrónica y Programación de Controladores con Arduino",
                mutableListOf()
            )
            val tecnicas = Materia(
                "1059",
                "Seminarios sobre Herramientas ó Tecnicas Puntuales: Tecnología y Sociedad",
                mutableListOf()
            )
            val tip = Materia("1060", "Trabajo de Inserción Profesional", mutableListOf())

            val analisis = Materia("54", "Análisis Matemático 1", mutableListOf(mate2))
            val mate3 = Materia("842", "Matemática 3", mutableListOf(analisis))
            val proba = Materia("604", "Probabilidad y Estadisticas", mutableListOf(mate3))
            val logica = Materia("1302", "Lógica y Programación", mutableListOf(intro, mate1))
            val seguridad = Materia("1303", "Seguridad de la Información", mutableListOf(labo))
            val requerimientos = Materia(
                "1308",
                "Ingenieria de Requerimientos",
                mutableListOf(elementosdeingeneria),
            )
            val gestion = Materia(
                "1304",
                "Gestión de Proyectos de Desarrollo de Software",
                mutableListOf(requerimientos),
            )
            val practicaDeDesarrollo = Materia(
                "1305",
                "Prácticas de Desarrollo de Software",
                mutableListOf(elementosdeingeneria, interfaces, persistencia),
            )
            val lfa = Materia("1306", "Lenguajes Formales y Automatas", mutableListOf(logica))
            val algoritmos = Materia("1307", "Algoritmos", mutableListOf(funcional))

            val teoria = Materia("1309", "Teoría de la Computación", mutableListOf(lfa))
            val arquitectura1 = Materia(
                "1310",
                "Arquitectura de Software I",
                mutableListOf(concurrente, seguridad, gestion)
            )
            val distribuidos =
                Materia("1311", "Sistemas Distribuidos", mutableListOf(concurrente, labo))
            val caracteristicas = Materia(
                "1312",
                "Caracteristicas de Lenguajes de Programación",
                mutableListOf(logica),
            )
            val arquitectura2 = Materia(
                "1313",
                "Arquitectura de Software II",
                mutableListOf(arquitectura1, distribuidos)
            )
            val arquitecturaDeComputadoras =
                Materia("1314", "Arquitectura de Computadoras", mutableListOf(labo))
            val parseo = Materia(
                "1315",
                "Parseo y Generación de Código",
                mutableListOf(lfa, caracteristicas)
            )
            val aspectosLegales =
                Materia("1316", "Aspectos Legales y Sociales", mutableListOf())
            val seminarioFinal = Materia("1317", "Seminario Final", mutableListOf())
            val seminarioCapacitacion = Materia(
                "1719",
                "Seminarios de Capacitación Profesional en Informática (SCPI)",
                mutableListOf()
            )

            val seguridadTec = Materia("646", "Seguridad Informática", mutableListOf())

            val tv = Materia("1328", "Seminario : Televisión Digital", mutableListOf())
            val streaming =
                Materia("1632", "Seminario : Tecnología de Streaming sobre Internet", mutableListOf())
            val cloud = Materia(
                "1643",
                "Seminario : Taller de Desarrollos de Servicios Web / Cloud Modernos",
                mutableListOf()
            )
            val bajo = Materia("1644", "Seminario : Programación a Bajo Nivel", mutableListOf())
            val semantica = Materia("1319", "Semántica de Lenguajes de Programación", mutableListOf())
            val seminarios = Materia("1622", "Seminarios", mutableListOf())
            val calidad = Materia("1707", "Calidad del Software", mutableListOf())
            val funcionalAvanzada =
                Materia("1708", "Programación Funcional Avanzada", mutableListOf())
            val progCuantica =
                Materia("1709", "Introducción a la Programación Cuántica", mutableListOf())
            val ciudadana = Materia(
                "1710",
                "Ciencia Ciudadana y Colaboración Abierta y Distribuida",
                mutableListOf()
            )
            val ludificacion = Materia("1711", "Ludificación", mutableListOf())
            val cdDatos = Materia("1745", "Ciencia de Datos", mutableListOf())

            val bddhorariosc1 = mutableListOf(
                Horario(Dia.Mar, LocalTime.of(10, 0, 0), LocalTime.of(12, 0, 0)),
                Horario(Dia.Jue, LocalTime.of(10, 0, 0), LocalTime.of(12, 0, 0))
            )

            val bddhorariosc2 = mutableListOf(
                Horario(Dia.Lun, LocalTime.of(10, 0, 0), LocalTime.of(12, 0, 0)),
                Horario(Dia.Mie, LocalTime.of(10, 0, 0), LocalTime.of(12, 0, 0))
            )

            val matehorarios = mutableListOf(
                Horario(Dia.Lun, LocalTime.of(10, 30, 0), LocalTime.of(12, 30, 0)),
                Horario(Dia.Jue, LocalTime.of(10, 30, 0), LocalTime.of(12, 30, 0))
            )

            val estrhorarios = mutableListOf(
                Horario(Dia.Lun, LocalTime.of(9, 0, 0), LocalTime.of(12, 0, 0)),
                Horario(Dia.Mie, LocalTime.of(9, 0, 0), LocalTime.of(12, 0, 0)),
                Horario(Dia.Vie, LocalTime.of(9, 0, 0), LocalTime.of(12, 0, 0))
            )

            val introhorariosc1 = mutableListOf(
                Horario(Dia.Mar, LocalTime.of(18, 0, 0), LocalTime.of(20, 0, 0)),
                Horario(Dia.Jue, LocalTime.of(18, 0, 0), LocalTime.of(22, 0, 0))
            )

            val introhorariosc2 = mutableListOf(
                Horario(Dia.Lun, LocalTime.of(9, 0, 0), LocalTime.of(11, 0, 0)),
                Horario(Dia.Jue, LocalTime.of(18, 0, 0), LocalTime.of(22, 0, 0))
            )
            val orgahorariosc1 = mutableListOf(
                Horario(Dia.Lun, LocalTime.of(9, 0, 0), LocalTime.of(11, 0, 0)),
                Horario(Dia.Mie, LocalTime.of(10, 0, 0), LocalTime.of(12, 0, 0))
            )
            val ingles1horariosc1 = mutableListOf(
                Horario(Dia.Mie, LocalTime.of(14, 0, 0), LocalTime.of(18, 0, 0))
            )
            val cuatrimestre = Cuatrimestre.actual()
            val cuatrimestre2019S1 = Cuatrimestre(anio = 2019, semestre = Semestre.S1)
            val cuatrimestre2019S2 = Cuatrimestre(anio = 2019, semestre = Semestre.S2)

            val bddc1 = Comision(bdd, 1, cuatrimestre, bddhorariosc1)
            val bddc2 = Comision(bdd, 2, cuatrimestre, bddhorariosc2)
            val matec1 = Comision(mate1, 1, cuatrimestre, matehorarios)
            val estrc1 = Comision(estructura, 1, cuatrimestre, estrhorarios)
            val introc1 = Comision(intro, 1, cuatrimestre, introhorariosc1)
            val introc2 = Comision(intro, 2, cuatrimestre, introhorariosc2)
            val orgac1 = Comision(orga, 1, cuatrimestre, orgahorariosc1)
            val inglesc1 = Comision(ingles1, 1, cuatrimestre, ingles1horariosc1, locacion = Locacion.General_Belgrano)

            val bddc12019S1 = Comision(bdd, 1, cuatrimestre2019S1, bddhorariosc1)
            val matec12019S1 = Comision(mate1, 1, cuatrimestre2019S1, matehorarios)
            val estrc12019S2 = Comision(estructura, 1, cuatrimestre2019S2, estrhorarios)
            val introc12019S2 = Comision(intro, 1, cuatrimestre2019S2, introhorariosc1)
            val contrasenia = passwordEncoder.encode("contrasenia")

            val jorge = Alumno(
                12345678,
                "Jorge",
                "Arenales",
                "jorge.arenales20@alu.edu.ar",
                contrasenia,
                Carrera.PW,
                calidad = Calidad.Activo,
                regular = Regular.S,
                estadoInscripcion = EstadoInscripcion.Aceptado
            )
            val bartolo = Alumno(
                12345677,
                "Bartolo",
                "Gutierrez",
                "bartolito@alu.edu.ar",
                contrasenia,
                Carrera.PW,
                calidad = Calidad.Activo,
                regular = Regular.S,
                estadoInscripcion = EstadoInscripcion.Aceptado
            )
            val maria = Alumno(
                12345680,
                "Maria",
                "Jimenez",
                "mjimenez@alu.edu.ar",
                contrasenia,
                Carrera.PW,
                calidad = Calidad.Activo,
                regular = Regular.S,
                estadoInscripcion = EstadoInscripcion.Aceptado
            )
            val roberto = Alumno(
                12345679,
                "Roberto",
                "Sanchez",
                "rsanchez@alu.edu.ar",
                contrasenia,
                Carrera.P,
                calidad = Calidad.Activo,
                regular = Regular.S,
                estadoInscripcion = EstadoInscripcion.Aceptado
            )

            val firulais = Alumno(
                12345681,
                "Firulais",
                "Tercero",
                "ftercero@alu.edu.ar",
                contrasenia,
                Carrera.P,
                locacion = Locacion.General_Belgrano,
                calidad = Calidad.Activo,
                regular = Regular.S,
                estadoInscripcion = EstadoInscripcion.Aceptado
            )

            val sofia = Alumno(
                12345682,
                "sofia",
                "Sofia",
                "ssofia@alu.edu.ar",
                contrasenia,
                Carrera.P,
                calidad = Calidad.Activo,
                regular = Regular.N,
                estadoInscripcion = EstadoInscripcion.Aceptado
            )
            cuatrimestreRepository.save(cuatrimestre)
            cuatrimestreRepository.save(cuatrimestre2019S1)
            cuatrimestreRepository.save(cuatrimestre2019S2)
            materiaRepository.saveAll(listOf(epyl, lea, ttu, tti, matematica, ingles1, ingles2, bdd, intro, orga,mate1, estructura, objetos1, objetos2, redes
                                            , sistemasoperativos, concurrente, mate2, elementosdeingeneria, interfaces, persistencia, funcional, desarrollo, labo, bdd2
                                            , softwareLibre, introArquitectura, objetos3, bioinformatica, politica, geografica, declarativas, videojuegos, derechos, arduino
                                            , tecnicas, tip, analisis, mate3, proba, logica, seguridad, requerimientos, gestion, practicaDeDesarrollo, lfa, algoritmos
                                            , teoria, arquitectura1, distribuidos, caracteristicas, arquitectura2, arquitecturaDeComputadoras, parseo, aspectosLegales, seminarioFinal, seminarioCapacitacion
                                            , seguridadTec, tv, streaming, cloud, bajo, semantica, seminarios, calidad, funcionalAvanzada, progCuantica, ciudadana, ludificacion, cdDatos))
            comisionRespository.saveAll(listOf(bddc1, bddc2, matec1, estrc1,introc1, introc2, orgac1, inglesc1, bddc12019S1, estrc12019S2, introc12019S2, matec12019S1))
            jorge.actualizarHistoriaAcademica(listOf(
                    MateriaCursada(intro, EstadoMateria.APROBADO, LocalDate.of(2021, 12, 20)),
                    MateriaCursada(matematica, EstadoMateria.APROBADO, LocalDate.of(2021, 12, 20)),
                    MateriaCursada(epyl, EstadoMateria.APROBADO, LocalDate.of(2021, 12, 20)),
                    MateriaCursada(lea, EstadoMateria.DESAPROBADO, LocalDate.of(2021, 12, 20)),
                    MateriaCursada(bdd, EstadoMateria.DESAPROBADO, LocalDate.of(2021, 12, 20)),
                    MateriaCursada(bdd, EstadoMateria.DESAPROBADO)
                )
            )
            roberto.actualizarHistoriaAcademica(
                listOf(
                    MateriaCursada(matematica, EstadoMateria.APROBADO),
                    MateriaCursada(epyl, EstadoMateria.APROBADO),
                    MateriaCursada(lea, EstadoMateria.APROBADO)
                )
            )
            bartolo.actualizarHistoriaAcademica(
                listOf(
                    MateriaCursada(matematica, EstadoMateria.APROBADO),
                    MateriaCursada(epyl, EstadoMateria.APROBADO),
                    MateriaCursada(lea, EstadoMateria.APROBADO)
                )
            )
            maria.actualizarHistoriaAcademica(
                listOf(
                    MateriaCursada(matematica, EstadoMateria.APROBADO),
                    MateriaCursada(epyl, EstadoMateria.APROBADO)
                )
            )
            firulais.actualizarHistoriaAcademica(
                listOf(
                    MateriaCursada(matematica, EstadoMateria.APROBADO),
                    MateriaCursada(epyl, EstadoMateria.APROBADO),
                    MateriaCursada(lea, EstadoMateria.APROBADO)
                )
            )
            sofia.actualizarHistoriaAcademica(
                listOf(
                    MateriaCursada(matematica, EstadoMateria.APROBADO),
                    MateriaCursada(epyl, EstadoMateria.APROBADO),
                    MateriaCursada(lea, EstadoMateria.APROBADO)
                )
            )
            alumnoRepository.saveAll(listOf(jorge, bartolo, roberto, maria, firulais, sofia))
            alumnoService.guardarSolicitudPara(
                bartolo.dni,
                listOf(bddc1.id!!, bddc2.id!!),
                cuatrimestre,
                comisionesInscriptoIds = listOf(introc1.id!!)
            )
            alumnoService.guardarSolicitudPara(jorge.dni, listOf(estrc1.id!!), cuatrimestre, comisionesInscriptoIds = listOf(bddc1.id!!))
            alumnoService.guardarSolicitudPara(roberto.dni, listOf(introc1.id!!, introc2.id!!), cuatrimestre)
            alumnoService.guardarSolicitudPara(maria.dni, listOf(bddc1.id!!), cuatrimestre)

            alumnoService.registrarAlumnos(
                listOf(
                    FormularioCrearAlumno(
                        45678900,
                        "Jaime",
                        "Lopes",
                        "unque.nocontestar@gmail.com", 5555, Carrera.PW, 4.21
                    )
                )
            )
            val fecha = LocalDate.now().minusMonths(3)
            val historiaAcademica = listOf(
                MateriaCursadaDTO(matematica.codigo, EstadoMateria.DESAPROBADO, fecha),
                MateriaCursadaDTO(matematica.codigo, EstadoMateria.DESAPROBADO, fecha.minusDays(104)),
                MateriaCursadaDTO(epyl.codigo, EstadoMateria.DESAPROBADO, fecha.minusDays(67)),
                MateriaCursadaDTO(epyl.codigo, EstadoMateria.APROBADO, fecha),
                MateriaCursadaDTO(lea.codigo, EstadoMateria.APROBADO, fecha)
            )

            val comisiones2019S1 = comisionRespository.findByCuatrimestre(cuatrimestre2019S1)
            alumnoService.guardarSolicitudPara(jorge.dni, comisiones2019S1.map { it.id!! }, cuatrimestre2019S1)
            alumnoService.guardarSolicitudPara(maria.dni, comisiones2019S1.map { it.id!! }, cuatrimestre2019S1)

            comisionService.subirOferta(
                finInscripciones = LocalDateTime.of(2019,4,13,10,0),
                inicioInscripciones = LocalDateTime.of(2019,2,13,10,0),
                cuatrimestre = cuatrimestre2019S1, comisionesACrear = listOf()
            )

            comisionService.subirOferta(
                finInscripciones = LocalDateTime.of(2019,8,13,10,0),
                inicioInscripciones = LocalDateTime.of(2019,6,13,10,0),
                cuatrimestre = cuatrimestre2019S2, comisionesACrear = listOf()
            )

            alumnoService.cerrarFormularios(cuatrimestre = cuatrimestre2019S1)
            alumnoService.cerrarFormularios(cuatrimestre = cuatrimestre2019S2)
            alumnoService.actualizarHistoriaAcademica(listOf(
                AlumnoConHistoriaAcademica(45678900, historiaAcademica)
            ))

            autenticacionService.crearDirectivo(CreacionDirectivo("gabi@unque.edu.ar", "Gabi A", adminPassword))
            val cantMaterias = materiaRepository.count()
            val cantComisiones = comisionRespository.count()
            val cantAlumnos = alumnoRepository.count()

            println()
            println("##########################")
            println("Datos creados exitosamente")
            println("##########################")
            println()
            println("Total de materias: $cantMaterias")
            println("Total de comisiones: $cantComisiones")
            println("Total de alumnos: $cantAlumnos")
        }
    }

    private fun emptyData(): Boolean {
        return materiaRepository.count().toInt() == 0
    }
}