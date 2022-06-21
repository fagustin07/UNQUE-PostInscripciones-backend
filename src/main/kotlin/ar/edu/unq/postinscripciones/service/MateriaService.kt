package ar.edu.unq.postinscripciones.service

import ar.edu.unq.postinscripciones.model.Carrera
import ar.edu.unq.postinscripciones.model.Materia
import ar.edu.unq.postinscripciones.model.cuatrimestre.Cuatrimestre
import ar.edu.unq.postinscripciones.model.exception.ExcepcionUNQUE
import ar.edu.unq.postinscripciones.model.exception.MateriaNoEncontradaExcepcion
import ar.edu.unq.postinscripciones.persistence.*
import ar.edu.unq.postinscripciones.service.dto.materia.MateriaPorSolicitudes
import ar.edu.unq.postinscripciones.service.dto.formulario.FormularioMateria
import ar.edu.unq.postinscripciones.service.dto.formulario.FormularioModificarMateria
import ar.edu.unq.postinscripciones.service.dto.materia.MateriaDTO
import ar.edu.unq.postinscripciones.service.dto.materia.MateriaConCorrelativas
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class MateriaService {
    @Autowired
    private lateinit var formularioRepository: FormularioRepository

    @Autowired
    private lateinit var materiaRepository: MateriaRepository

    @Autowired
    private lateinit var comisionRespository: ComisionRespository

    @Autowired
    private lateinit var alumnoRepository: AlumnoRepository

    @Transactional
    fun crear(nombre: String, codigo: String, correlativas : List<String>, carrera: Carrera): MateriaDTO {
        val existeConNombreOCodigo = materiaRepository.findByNombreIgnoringCaseOrCodigoIgnoringCase(nombre, codigo)
        if (existeConNombreOCodigo.isPresent) {
            throw ExcepcionUNQUE("La materia que desea crear con nombre $nombre " +
                    "y codigo $codigo, genera conflicto con la materia: ${existeConNombreOCodigo.get().nombre}, codigo: ${existeConNombreOCodigo.get().codigo}")
        } else {
            val materiasCorrelativas = materiaRepository.findAllByCodigoIn(correlativas)
            val materiaInexistente = correlativas.find { !materiasCorrelativas.map { c -> c.codigo }.contains(it) }
            if (materiaInexistente != null) throw ExcepcionUNQUE("No existe la materia con codigo: $materiaInexistente")
            val materia = materiaRepository.save(Materia(codigo, nombre, materiasCorrelativas.toMutableList(), carrera))

            return MateriaDTO.desdeModelo(materia)
        }
    }

    @Transactional
    fun crear(formulariosMaterias: List<FormularioMateria>): List<MateriaDTO> {
        val materias = formulariosMaterias.map { form ->
            val existeConNombreOCodigo = materiaRepository.findByNombreIgnoringCaseOrCodigoIgnoringCase(form.nombre, form.codigo)
            if (existeConNombreOCodigo.isPresent) {
                throw ExcepcionUNQUE(
                    "La materia que desea crear con nombre ${form.nombre} " +
                            "y codigo ${form.codigo}, genera conflicto con la materia: ${existeConNombreOCodigo.get().nombre}, codigo: ${existeConNombreOCodigo.get().codigo}"
                )
            }

            Materia(form.codigo, form.nombre, mutableListOf(), form.carrera)
        }

        val materiasCreadas = materiaRepository.saveAll(materias)
        return materiasCreadas.map { MateriaDTO.desdeModelo(it) }
    }

    @Transactional
    fun borrarMateria(codigo: String) {
        eliminarMateria(codigo)
    }

    @Transactional
    fun todas(): List<MateriaDTO> {
        val materias = materiaRepository.findAll().toList()
        return materias.map { MateriaDTO.desdeModelo(it) }
    }

    @Transactional
    fun actualizarCorrelativas(materiasConCorrelativas: List<MateriaConCorrelativas>): List<MateriaDTO> {
        return materiasConCorrelativas.map {
            val materia = materiaRepository.findByNombreIgnoringCase(it.nombre).orElseThrow{ MateriaNoEncontradaExcepcion() }

            val materiasCorrelativas = it.correlativas.map { correlativa ->
                materiaRepository
                    .findByNombreIgnoringCase(correlativa.nombre)
                    .orElseThrow{ ExcepcionUNQUE("No existe la materia con nombre: ${correlativa.nombre}") }
            }

            materia.actualizarCorrelativas(materiasCorrelativas.toMutableList())

            MateriaDTO.desdeModelo(materiaRepository.save(materia))
        }
    }

    @Transactional
    fun obtener(codigo: String): Materia {
        val materia = materiaRepository.findMateriaByCodigo(codigo).orElseThrow{ MateriaNoEncontradaExcepcion() }
        materia.correlativas.size
        return materia
    }

    @Transactional
    fun modificar(formularioMateria: FormularioModificarMateria): MateriaDTO {
        val materia = materiaRepository.findMateriaByCodigo(formularioMateria.codigo).orElseThrow {
            ExcepcionUNQUE("No existe la materia con codigo: ${formularioMateria.codigo}")
        }
        val materiaActualizada = Materia(formularioMateria.codigo, formularioMateria.nombre, materia.correlativas, formularioMateria.carrera)
        materiaRepository.save(materiaActualizada)

        return MateriaDTO.desdeModelo(materiaRepository.save(materiaActualizada))
    }

    @Transactional
    fun materiasPorSolicitudes(cuatrimestre: Cuatrimestre = Cuatrimestre.actual()): List<MateriaPorSolicitudes> {
        return materiaRepository
            .findByCuatrimestreAnioAndCuatrimestreSemestreOrderByCountSolicitudesPendientes(
                cuatrimestre.anio,
                cuatrimestre.semestre
            )
            .map { MateriaPorSolicitudes.desdeTupla(it) }
    }

    @Transactional
    fun borrarTodos() {
        materiaRepository.findAll().forEach { eliminarMateria(it.codigo) }
    }

    private fun eliminarMateria(codigo: String) {
        comisionRespository.findByMateriaCodigo(codigo).forEach { comision ->
            formularioRepository.findByComisionesInscriptoId(comision.id!!).forEach {
                it.quitarInscripcionDe(comision.id!!)
                formularioRepository.save(it)
            }
        }
        comisionRespository.deleteByMateriaCodigo(codigo)
        val materias: List<Materia> = materiaRepository.findByCorrelativasCodigo(codigo)
        materias.forEach {
            it.quitarCorrelativa(codigo)
            materiaRepository.save(it)
        }
        materiaRepository.deleteById(codigo)
    }

}