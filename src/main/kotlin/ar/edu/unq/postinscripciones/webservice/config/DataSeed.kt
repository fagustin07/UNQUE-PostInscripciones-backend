package ar.edu.unq.postinscripciones.webservice.config

import ar.edu.unq.postinscripciones.model.*
import ar.edu.unq.postinscripciones.model.comision.Comision
import ar.edu.unq.postinscripciones.model.comision.Dia
import ar.edu.unq.postinscripciones.model.comision.Horario
import ar.edu.unq.postinscripciones.model.cuatrimestre.Cuatrimestre
import ar.edu.unq.postinscripciones.persistence.AlumnoRepository
import ar.edu.unq.postinscripciones.persistence.ComisionRespository
import ar.edu.unq.postinscripciones.persistence.CuatrimestreRepository
import ar.edu.unq.postinscripciones.persistence.MateriaRepository
import ar.edu.unq.postinscripciones.service.AlumnoService
import ar.edu.unq.postinscripciones.service.AutenticacionService
import ar.edu.unq.postinscripciones.service.dto.CreacionDirectivo
import ar.edu.unq.postinscripciones.service.dto.alumno.AlumnoConHistoriaAcademica
import ar.edu.unq.postinscripciones.service.dto.formulario.FormularioCrearAlumno
import ar.edu.unq.postinscripciones.service.dto.materia.MateriaCursadaDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Profile
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalTime

@Profile("!test")
@Component
class DataSeed(
    @Autowired private val materiaRepository: MateriaRepository,
    @Autowired private val cuatrimestreRepository: CuatrimestreRepository,
    @Autowired private val comisionRespository: ComisionRespository,
    @Autowired private val alumnoRepository: AlumnoRepository,
    @Autowired private val alumnoService: AlumnoService,
    @Autowired private val autenticacionService: AutenticacionService
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
            val epyl = Materia("80005", "Elementos de Programación y Lógica", mutableListOf(), Carrera.SIMULTANEIDAD)
            val lea = Materia("80000", "Lectura y Escritura Académica", mutableListOf(), Carrera.SIMULTANEIDAD)
            val matematica = Materia("8003N", "Matemática", mutableListOf(), Carrera.SIMULTANEIDAD)
            val ingles1 = Materia("90000", "Inglés 1", mutableListOf(), Carrera.SIMULTANEIDAD)
            val ingles2 = Materia("90028", "Inglés 2", mutableListOf(), Carrera.SIMULTANEIDAD)
            val ttu = Materia("00752", "Taller de Trabajo Universitario", mutableListOf(), Carrera.SIMULTANEIDAD)
            val tti = Materia("751N", "Taller de Trabajo Intelectual", mutableListOf(), Carrera.SIMULTANEIDAD)

            val intro = Materia("00487", "Introducción a la Programación", mutableListOf(epyl), Carrera.SIMULTANEIDAD)
            val orga = Materia("01032", "Organización de las Computadoras", mutableListOf(epyl), Carrera.SIMULTANEIDAD)
            val mate1 = Materia("01033", "Matemática 1", mutableListOf(matematica), Carrera.SIMULTANEIDAD)
            val objetos1 = Materia("01034", "Programación con Objetos 1", mutableListOf(intro), Carrera.SIMULTANEIDAD)
            val bdd = Materia("01035", "Bases de Datos", mutableListOf(), Carrera.SIMULTANEIDAD)
            val estructura = Materia("01036", "Estructura de Datos", mutableListOf(intro), Carrera.SIMULTANEIDAD)
            val objetos2 =
                Materia("01037", "Programación con Objetos 2", mutableListOf(objetos1), Carrera.SIMULTANEIDAD)
            val redes = Materia("01038", "Redes de Computadoras", mutableListOf(orga), Carrera.SIMULTANEIDAD)
            val sistemasoperativos =
                Materia("01039", "Sistemas Operativos", mutableListOf(intro, orga), Carrera.SIMULTANEIDAD)
            val concurrente =
                Materia("01040", "Programación Concurrente", mutableListOf(estructura), Carrera.SIMULTANEIDAD)
            val mate2 = Materia("01041", "Matemática 2", mutableListOf(mate1), Carrera.SIMULTANEIDAD)
            val elementosdeingeneria =
                Materia("01042", "Elementos de Ingenieria de Software", mutableListOf(objetos2), Carrera.SIMULTANEIDAD)
            val interfaces = Materia(
                "01043",
                "Construcción de Interfaces de Usuario",
                mutableListOf(objetos2),
                Carrera.SIMULTANEIDAD
            )
            val persistencia =
                Materia("01044", "Estrategias de Persistencia", mutableListOf(bdd, objetos2), Carrera.SIMULTANEIDAD)
            val funcional = Materia("01045", "Programación Funcional", mutableListOf(estructura), Carrera.SIMULTANEIDAD)
            val desarrollo = Materia(
                "01046",
                "Desarrollo de Aplicaciones",
                mutableListOf(elementosdeingeneria, persistencia, interfaces),
                Carrera.TPI
            )
            val labo = Materia(
                "01047",
                "Laboratiorio de Sistemas Operativos y Redes",
                mutableListOf(redes, sistemasoperativos),
                Carrera.SIMULTANEIDAD
            )
            val bdd2 = Materia("01048", "Bases de Datos II", mutableListOf(), Carrera.SIMULTANEIDAD)
            val softwareLibre = Materia(
                "01049",
                "Participación y Gestión en Proyectos de Software Libre",
                mutableListOf(),
                Carrera.SIMULTANEIDAD
            )
            val introArquitectura =
                Materia("01050", "Introducción a las Arquitecturas de Software", mutableListOf(), Carrera.SIMULTANEIDAD)
            val objetos3 =
                Materia("01051", "Programación con Objetos 3", mutableListOf(objetos2), Carrera.SIMULTANEIDAD)
            val bioinformatica =
                Materia("01052", "Introducción a la Bioinformática", mutableListOf(), Carrera.SIMULTANEIDAD)
            val politica = Materia(
                "01053",
                "Politicas Públicas en la Sociedad de la Información y la Era Digital",
                mutableListOf(),
                Carrera.SIMULTANEIDAD
            )
            val geografica =
                Materia("01054", "Sistemas de Información Geográfica", mutableListOf(), Carrera.SIMULTANEIDAD)
            val declarativas =
                Materia("01055", "Herramientas declarativas en Programación", mutableListOf(), Carrera.SIMULTANEIDAD)
            val videojuegos =
                Materia("01056", "Introducción al Desarrollo de Videojuegos", mutableListOf(), Carrera.SIMULTANEIDAD)
            val derechos = Materia(
                "01057",
                "Derechos de Autor y Derecho de Copia en la Era Digita",
                mutableListOf(),
                Carrera.SIMULTANEIDAD
            )
            val arduino = Materia(
                "01058",
                "Seminarios: Introducción a la Electrónica y Programación de Controladores con Arduino",
                mutableListOf(),
                Carrera.TPI
            )
            val tecnicas = Materia(
                "01059",
                "Seminarios sobre Herramientas ó Tecnicas Puntuales: Tecnología y Sociedad",
                mutableListOf(),
                Carrera.TPI
            )
            val tip = Materia("01060", "Trabajo de Inserción Profesional", mutableListOf(), Carrera.TPI)

            val analisis = Materia("00054", "Análisis Matemático 1", mutableListOf(mate2), Carrera.LI)
            val mate3 = Materia("00842", "Matemática 3", mutableListOf(analisis), Carrera.LI)
            val proba = Materia("00604", "Probabilidad y Estadisticas", mutableListOf(mate3), Carrera.LI)
            val logica = Materia("01302", "Lógica y Programación", mutableListOf(intro, mate1), Carrera.SIMULTANEIDAD)
            val seguridad = Materia("01303", "Seguridad de la Información", mutableListOf(labo), Carrera.LI)
            val requerimientos = Materia(
                "01308",
                "Ingenieria de Requerimientos",
                mutableListOf(elementosdeingeneria),
                Carrera.SIMULTANEIDAD
            )
            val gestion = Materia(
                "01304",
                "Gestión de Proyectos de Desarrollo de Software",
                mutableListOf(requerimientos),
                Carrera.SIMULTANEIDAD
            )
            val practicaDeDesarrollo = Materia(
                "01305",
                "Prácticas de Desarrollo de Software",
                mutableListOf(elementosdeingeneria, interfaces, persistencia),
                Carrera.SIMULTANEIDAD
            )
            val lfa = Materia("01306", "Lenguajes Formales y Automatas", mutableListOf(logica), Carrera.SIMULTANEIDAD)
            val algoritmos = Materia("01307", "Algoritmos", mutableListOf(funcional), Carrera.LI)

            val teoria = Materia("01309", "Teoría de la Computación", mutableListOf(lfa), Carrera.LI)
            val arquitectura1 = Materia(
                "01310",
                "Arquitectura de Software I",
                mutableListOf(concurrente, seguridad, gestion),
                Carrera.LI
            )
            val distribuidos =
                Materia("01311", "Sistemas Distribuidos", mutableListOf(concurrente, labo), Carrera.SIMULTANEIDAD)
            val caracteristicas = Materia(
                "01312",
                "Caracteristicas de Lenguajes de Programación",
                mutableListOf(logica),
                Carrera.SIMULTANEIDAD
            )
            val arquitectura2 = Materia(
                "01313",
                "Arquitectura de Software II",
                mutableListOf(arquitectura1, distribuidos),
                Carrera.LI
            )
            val arquitecturaDeComputadoras =
                Materia("01314", "Arquitectura de Computadoras", mutableListOf(labo), Carrera.SIMULTANEIDAD)
            val parseo = Materia(
                "01315",
                "Parseo y Generación de Código",
                mutableListOf(lfa, caracteristicas),
                Carrera.LI
            )
            val aspectosLegales =
                Materia("01316", "Aspectos Legales y Sociales", mutableListOf(), Carrera.SIMULTANEIDAD)
            val seminarioFinal = Materia("01317", "Seminario Final", mutableListOf(), Carrera.LI)
            val seminarioCapacitacion = Materia(
                "01719",
                "Seminarios de Capacitación Profesional en Informática (SCPI)",
                mutableListOf(),
                Carrera.LI
            )

            val seguridadTec = Materia("00646", "Seguridad Informática", mutableListOf(), Carrera.TPI)

            val tv = Materia("01328", "Seminario : Televisión Digital", mutableListOf(), Carrera.TPI)
            val streaming =
                Materia("01632", "Seminario : Tecnología de Streaming sobre Internet", mutableListOf(), Carrera.TPI)
            val cloud = Materia(
                "01643",
                "Seminario : Taller de Desarrollos de Servicios Web / Cloud Modernos",
                mutableListOf(),
                Carrera.TPI
            )
            val bajo = Materia("01644", "Seminario : Programación a Bajo Nivel", mutableListOf(), Carrera.TPI)
            val semantica = Materia("01319", "Semántica de Lenguajes de Programación", mutableListOf(), Carrera.TPI)
            val seminarios = Materia("01622", "Seminários", mutableListOf(), Carrera.LI)
            val calidad = Materia("01707", "Calidad del Software", mutableListOf(), Carrera.LI)
            val funcionalAvanzada =
                Materia("01708", "Programación Funcional Avanzada", mutableListOf(), Carrera.LI)
            val progCuantica =
                Materia("01709", "Introducción a la Programación Cuántica", mutableListOf(), Carrera.LI)
            val ciudadana = Materia(
                "01710",
                "Ciencia Ciudadana y Colaboración Abierta y Distribuida",
                mutableListOf(),
                Carrera.LI
            )
            val ludificacion = Materia("01711", "Ludificación", mutableListOf(), Carrera.LI)
            val cdDatos = Materia("01745", "Ciencia de Datos", mutableListOf(), Carrera.LI)

            val bddhorariosc1 = mutableListOf(
                Horario(Dia.MARTES, LocalTime.of(10, 0, 0), LocalTime.of(12, 0, 0)),
                Horario(Dia.JUEVES, LocalTime.of(10, 0, 0), LocalTime.of(12, 0, 0))
            )

            val bddhorariosc2 = mutableListOf(
                Horario(Dia.LUNES, LocalTime.of(10, 0, 0), LocalTime.of(12, 0, 0)),
                Horario(Dia.MIERCOLES, LocalTime.of(10, 0, 0), LocalTime.of(12, 0, 0))
            )

            val matehorarios = mutableListOf(
                Horario(Dia.LUNES, LocalTime.of(10, 30, 0), LocalTime.of(12, 30, 0)),
                Horario(Dia.JUEVES, LocalTime.of(10, 30, 0), LocalTime.of(12, 30, 0))
            )

            val estrhorarios = mutableListOf(
                Horario(Dia.LUNES, LocalTime.of(9, 0, 0), LocalTime.of(12, 0, 0)),
                Horario(Dia.MIERCOLES, LocalTime.of(9, 0, 0), LocalTime.of(12, 0, 0)),
                Horario(Dia.VIERNES, LocalTime.of(9, 0, 0), LocalTime.of(12, 0, 0))
            )

            val introhorariosc1 = mutableListOf(
                Horario(Dia.MARTES, LocalTime.of(18, 0, 0), LocalTime.of(20, 0, 0)),
                Horario(Dia.JUEVES, LocalTime.of(18, 0, 0), LocalTime.of(22, 0, 0))
            )

            val introhorariosc2 = mutableListOf(
                Horario(Dia.LUNES, LocalTime.of(9, 0, 0), LocalTime.of(11, 0, 0)),
                Horario(Dia.JUEVES, LocalTime.of(18, 0, 0), LocalTime.of(22, 0, 0))
            )
            val orgahorariosc1 = mutableListOf(
                Horario(Dia.LUNES, LocalTime.of(9, 0, 0), LocalTime.of(11, 0, 0)),
                Horario(Dia.MIERCOLES, LocalTime.of(10, 0, 0), LocalTime.of(12, 0, 0))
            )
            val ingles1horariosc1 = mutableListOf(
                Horario(Dia.MIERCOLES, LocalTime.of(14, 0, 0), LocalTime.of(18, 0, 0))
            )
            val cuatrimestre = Cuatrimestre.actual()

            val bddc1 = Comision(bdd, 1, cuatrimestre, bddhorariosc1)
            val bddc2 = Comision(bdd, 2, cuatrimestre, bddhorariosc2)
            val matec1 = Comision(mate1, 1, cuatrimestre, matehorarios)
            val estrc1 = Comision(estructura, 1, cuatrimestre, estrhorarios)
            val introc1 = Comision(intro, 1, cuatrimestre, introhorariosc1)
            val introc2 = Comision(intro, 2, cuatrimestre, introhorariosc2)
            val orgac1 = Comision(orga, 1, cuatrimestre, orgahorariosc1)
            val inglesc1 = Comision(ingles1, 1, cuatrimestre, ingles1horariosc1)

            val contrasenia = passwordEncoder.encode("contrasenia")

            val jorge = Alumno(
                12345678,
                "Jorge",
                "Arenales",
                "jorge.arenales20@alu.edu.ar",
                12345,
                contrasenia,
                Carrera.SIMULTANEIDAD,
                8.7

            )
            val bartolo = Alumno(
                12345677,
                "Bartolo",
                "Gutierrez",
                "bartolito@alu.edu.ar",
                45555,
                contrasenia,
                Carrera.SIMULTANEIDAD,
                7.24
            )
            val maria = Alumno(
                12345680,
                "Maria",
                "Jimenez",
                "mjimenez@alu.edu.ar",
                45557,
                contrasenia,
                Carrera.SIMULTANEIDAD,
                8.21
            )
            val roberto = Alumno(
                12345679,
                "Roberto",
                "Sanchez",
                "rsanchez@alu.edu.ar",
                45556,
                contrasenia,
                Carrera.TPI,
                6.10
            )

            val firulais = Alumno(
                12345681,
                "Firulais",
                "Tercero",
                "ftercero@alu.edu.ar",
                45559,
                contrasenia,
                Carrera.TPI,
                5.34
            )

            val sofia = Alumno(
                12345682,
                "sofia",
                "Sofia",
                "ssofia@alu.edu.ar",
                45560,
                contrasenia,
                Carrera.TPI,
                9.15
            )
            cuatrimestreRepository.save(cuatrimestre)
            materiaRepository.saveAll(listOf(epyl, lea, ttu, tti, matematica, ingles1, ingles2, bdd, intro, orga,mate1, estructura, objetos1, objetos2, redes
                                            , sistemasoperativos, concurrente, mate2, elementosdeingeneria, interfaces, persistencia, funcional, desarrollo, labo, bdd2
                                            , softwareLibre, introArquitectura, objetos3, bioinformatica, politica, geografica, declarativas, videojuegos, derechos, arduino
                                            , tecnicas, tip, analisis, mate3, proba, logica, seguridad, requerimientos, gestion, practicaDeDesarrollo, lfa, algoritmos
                                            , teoria, arquitectura1, distribuidos, caracteristicas, arquitectura2, arquitecturaDeComputadoras, parseo, aspectosLegales, seminarioFinal, seminarioCapacitacion
                                            , seguridadTec, tv, streaming, cloud, bajo, semantica, seminarios, calidad, funcionalAvanzada, progCuantica, ciudadana, ludificacion, cdDatos))
            comisionRespository.saveAll(listOf(bddc1, bddc2, matec1, estrc1,introc1, introc2, orgac1, inglesc1))
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
                        "unque.nocontestar@gmail.com", 5555, Carrera.SIMULTANEIDAD, 4.21
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