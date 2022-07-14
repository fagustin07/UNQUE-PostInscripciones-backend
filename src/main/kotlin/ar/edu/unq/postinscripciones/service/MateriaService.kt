package ar.edu.unq.postinscripciones.service

import ar.edu.unq.postinscripciones.model.*
import ar.edu.unq.postinscripciones.model.cuatrimestre.Cuatrimestre
import ar.edu.unq.postinscripciones.model.exception.ConflictoConExistente
import ar.edu.unq.postinscripciones.model.exception.MateriaNoEncontrada
import ar.edu.unq.postinscripciones.model.exception.RecursoNoEncontrado
import ar.edu.unq.postinscripciones.persistence.ComisionRespository
import ar.edu.unq.postinscripciones.persistence.FormularioRepository
import ar.edu.unq.postinscripciones.persistence.MateriaRepository
import ar.edu.unq.postinscripciones.service.dto.formulario.FormularioMateria
import ar.edu.unq.postinscripciones.service.dto.formulario.FormularioModificarMateria
import ar.edu.unq.postinscripciones.service.dto.materia.*
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

    @Transactional
    fun crear(nombre: String, codigo: String, correlativas : List<String>, carrera: Carrera): MateriaDTO {
        val existeConNombreOCodigo = materiaRepository.findByNombreIgnoringCaseOrCodigoIgnoringCase(nombre, codigo)
        if (existeConNombreOCodigo.isPresent) {
            throw ConflictoConExistente("La materia que desea crear con nombre $nombre " +
                    "y codigo $codigo, genera conflicto con la materia: ${existeConNombreOCodigo.get().nombre}, codigo: ${existeConNombreOCodigo.get().codigo}")
        } else {
            val materiasCorrelativas = materiaRepository.findAllByCodigoIn(correlativas)
            val materiaInexistente = correlativas.find { !materiasCorrelativas.map { c -> c.codigo }.contains(it) }
            if (materiaInexistente != null) throw RecursoNoEncontrado("No existe la materia con codigo: $materiaInexistente")
            val materia = materiaRepository.save(Materia(codigo, nombre, materiasCorrelativas.toMutableList()))

            return MateriaDTO.desdeModelo(materia)
        }
    }

    @Transactional
    fun crear(formulariosMaterias: List<FormularioMateria>): MutableList<ConflictoMateria> {
        val conflictos: MutableList<ConflictoMateria> = mutableListOf()
        formulariosMaterias.map { form ->
            val existeConNombreOCodigo = materiaRepository.findByNombreIgnoringCaseOrCodigoIgnoringCase(form.nombre, form.codigo)
            if (existeConNombreOCodigo.isPresent) {
                val mensaje = "Conflicto con la materia ${existeConNombreOCodigo.get().nombre}" +
                        " y codigo ${existeConNombreOCodigo.get().codigo}"
                conflictos.add(ConflictoMateria(form.nombre,form.codigo, mensaje))
            }
            else {
                materiaRepository.save(Materia(form.codigo, form.nombre, mutableListOf()))
            }
        }
        return conflictos
    }

    @Transactional
    fun borrarMateria(codigo: String) {
        eliminarMateria(codigo)
    }

    @Transactional
    fun detalle(codigo: String): MateriaDetalle {
        return MateriaDetalle.desdeModelo(materiaRepository.findMateriaByCodigo(codigo).orElseThrow { MateriaNoEncontrada(codigo) })
    }

    @Transactional
    fun todas(): List<MateriaDTO> {
        val materias = materiaRepository.findAll().toList()
        return materias.map { MateriaDTO.desdeModelo(it) }
    }

    @Transactional
    fun actualizarCorrelativas(materiasConCorrelativas: List<MateriaConCorrelativas>): MutableList<ConflictoCorrelativa> {
        val conflictoCorrelativas = mutableListOf<ConflictoCorrelativa>()
        materiasConCorrelativas.forEach {
            val materiaExistente = materiaRepository.findMateriaByCodigo(it.codigoMateria)
            if (materiaExistente.isPresent) {
                val materia = materiaExistente.get()
                val materiasCorrelativas = mutableListOf<Materia>()
                    it.correlativas.forEach { correlativa ->
                    val existeCorrelativa = materiaRepository.findMateriaByCodigo(correlativa.codigoCorrelativa)
                    if (existeCorrelativa.isPresent) {
                        materiasCorrelativas.add(existeCorrelativa.get())
                    } else {
                        conflictoCorrelativas.add(ConflictoCorrelativa(materia.codigo, correlativa.codigoCorrelativa, "No se encontró la correlativa"))
                    }
                }

                materia.actualizarCorrelativas(materiasCorrelativas.toMutableList())
                materiaRepository.save(materia)
            } else {
                conflictoCorrelativas.add(ConflictoCorrelativa(it.codigoMateria, "", "Materia no encontrada"))
            }
        }

        return conflictoCorrelativas
    }

    @Transactional
    fun obtener(codigo: String): Materia {
        val materia = materiaRepository.findMateriaByCodigo(codigo).orElseThrow{ MateriaNoEncontrada(codigo) }
        materia.correlativas.size
        return materia
    }

    @Transactional
    fun modificar(formularioMateria: FormularioModificarMateria): MateriaDTO {
        val materia = materiaRepository.findMateriaByCodigo(formularioMateria.codigo).orElseThrow {
            MateriaNoEncontrada(formularioMateria.codigo)
        }
        val materiaActualizada = Materia(formularioMateria.codigo, formularioMateria.nombre, materia.correlativas)
        materiaRepository.save(materiaActualizada)

        return MateriaDTO.desdeModelo(materiaRepository.save(materiaActualizada))
    }

    @Transactional
    fun materiasPorSolicitudes(cuatrimestre: Cuatrimestre = Cuatrimestre.actual(), nombre: String = ""): List<MateriaPorSolicitudes> {
        return materiaRepository
            .findByCuatrimestreAnioAndCuatrimestreSemestreOrderByCountSolicitudesPendientes(
                cuatrimestre.anio,
                cuatrimestre.semestre,
                nombre = nombre.uppercase()
            )
            .map { MateriaPorSolicitudes.desdeTupla(it) }
    }

    @Transactional
    fun borrarTodos() {
        materiaRepository.findAll().forEach { eliminarMateria(it.codigo) }
    }

    private fun eliminarMateria(codigo: String) {
        try{
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
        } catch (excepcion: RuntimeException) {
            throw MateriaNoEncontrada(codigo)
        }
    }

}
