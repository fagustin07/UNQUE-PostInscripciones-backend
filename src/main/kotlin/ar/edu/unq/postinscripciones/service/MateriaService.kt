package ar.edu.unq.postinscripciones.service

import ar.edu.unq.postinscripciones.model.Carrera
import ar.edu.unq.postinscripciones.model.Materia
import ar.edu.unq.postinscripciones.model.exception.ExcepcionUNQUE
import ar.edu.unq.postinscripciones.model.exception.MateriaNoEncontradaExcepcion
import ar.edu.unq.postinscripciones.persistence.MateriaRepository
import ar.edu.unq.postinscripciones.service.dto.FormularioMateria
import ar.edu.unq.postinscripciones.service.dto.MateriaDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class MateriaService {
    @Autowired
    private lateinit var materiaRepository: MateriaRepository


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
    fun todas(): List<MateriaDTO> {
        val materias = materiaRepository.findAll().toList()
        return materias.map { MateriaDTO.desdeModelo(it) }
    }

    @Transactional
    fun actualizarCorrelativas(codigo: String, correlativas: List<String>): MateriaDTO {
        val materia = materiaRepository.findMateriaByCodigo(codigo).orElseThrow{ MateriaNoEncontradaExcepcion() }

        val materiasCorrelativas = correlativas.map { codigoCorrelativa ->
            materiaRepository
                .findMateriaByCodigo(codigoCorrelativa)
                .orElseThrow{ ExcepcionUNQUE("No existe la materia con codigo: $codigoCorrelativa") }
        }

        materia.actualizarCorrelativas(materiasCorrelativas.toMutableList())

        return MateriaDTO.desdeModelo(materiaRepository.save(materia))
    }

    @Transactional
    fun obtener(codigo: String): Materia {
        val materia = materiaRepository.findMateriaByCodigo(codigo).orElseThrow{ MateriaNoEncontradaExcepcion() }
        materia.correlativas.size
        return materia
    }

}
