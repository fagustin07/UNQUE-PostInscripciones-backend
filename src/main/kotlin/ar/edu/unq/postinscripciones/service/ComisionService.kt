package ar.edu.unq.postinscripciones.service

import ar.edu.unq.postinscripciones.model.Materia
import ar.edu.unq.postinscripciones.model.comision.Comision
import ar.edu.unq.postinscripciones.model.cuatrimestre.Cuatrimestre
import ar.edu.unq.postinscripciones.model.exception.ComisionNoEncontrada
import ar.edu.unq.postinscripciones.model.exception.CuatrimestreNoEncontrado
import ar.edu.unq.postinscripciones.model.exception.MateriaNoEncontrada
import ar.edu.unq.postinscripciones.persistence.ComisionRespository
import ar.edu.unq.postinscripciones.persistence.CuatrimestreRepository
import ar.edu.unq.postinscripciones.persistence.FormularioRepository
import ar.edu.unq.postinscripciones.persistence.MateriaRepository
import ar.edu.unq.postinscripciones.service.dto.carga.datos.ComisionNueva
import ar.edu.unq.postinscripciones.service.dto.carga.datos.Conflicto
import ar.edu.unq.postinscripciones.service.dto.comision.ComisionDTO
import ar.edu.unq.postinscripciones.service.dto.comision.HorarioDTO
import ar.edu.unq.postinscripciones.service.dto.formulario.FormularioComision
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import javax.transaction.Transactional

@Service
class ComisionService {

    @Autowired
    private lateinit var formularioRepository: FormularioRepository

    @Autowired
    private lateinit var comisionRespository: ComisionRespository

    @Autowired
    private lateinit var materiaRepository: MateriaRepository

    @Autowired
    private lateinit var cuatrimestreRepository: CuatrimestreRepository

    @Transactional
    fun subirOferta(
        comisionesACrear: List<ComisionNueva>,
        inicioInscripciones: LocalDateTime? = null,
        finInscripciones: LocalDateTime? = null,
        cuatrimestre: Cuatrimestre? = null
    ): List<Conflicto> {
        val miCuatrimestre: Cuatrimestre = cuatrimestre ?: Cuatrimestre.actualConFechas(inicioInscripciones, finInscripciones)
        val existeCuatrimestre =
            cuatrimestreRepository.findByAnioAndSemestre(miCuatrimestre.anio, miCuatrimestre.semestre)

        val cuatrimestreObtenido = if (existeCuatrimestre.isPresent) {
            this.actualizarCuatrimestre(existeCuatrimestre.get(), inicioInscripciones, finInscripciones)
        } else {
            cuatrimestreRepository.save(miCuatrimestre)
        }

        return crearComisiones(comisionesACrear, cuatrimestreObtenido)
    }

    @Transactional
    fun ofertaDelCuatrimestre(patronNombre: String = "", cuatrimestre: Cuatrimestre = Cuatrimestre.actual()): List<ComisionDTO> {
        val oferta = comisionRespository.findByCuatrimestreAnioAndCuatrimestreSemestreAndMateriaNombreIgnoreCaseContaining(
            cuatrimestre.anio,
            cuatrimestre.semestre,
            patronNombre
        )
        return oferta.map { ComisionDTO.desdeModelo(it) }
    }

    @Transactional
    fun crear(formularioComision: FormularioComision): Comision {
        return guardarComision(formularioComision)
    }

    @Transactional
    fun obtener(id: Long): ComisionDTO {
        val comision = comisionRespository.findById(id).orElseThrow { ComisionNoEncontrada(id) }
        return ComisionDTO.desdeModelo(comision)
    }

    @Transactional
    fun borrarComision(id: Long) {
        try{
            formularioRepository.findByComisionesInscriptoId(id).forEach {
                it.quitarInscripcionDe(id)
                formularioRepository.save(it)
            }
            comisionRespository.deleteById(id)
        } catch (excepcion: RuntimeException) {
            throw ComisionNoEncontrada(id)
        }

    }

    @Transactional
    fun obtenerComisionesMateria(codigoMateria: String, cuatrimestre: Cuatrimestre = Cuatrimestre.actual()): List<ComisionDTO> {
        val cuatrimestreObtenido = cuatrimestreRepository.findByAnioAndSemestre(cuatrimestre.anio, cuatrimestre.semestre)
            .orElseThrow { CuatrimestreNoEncontrado() }
        val materia = materiaRepository.findById(codigoMateria)
            .orElseThrow { MateriaNoEncontrada(codigoMateria) }
        val comisiones = comisionRespository.findAllByMateriaAndCuatrimestreAnioAndCuatrimestreSemestre(materia, cuatrimestreObtenido.anio, cuatrimestreObtenido.semestre)

        return comisiones.map { ComisionDTO.desdeModelo(it) }
    }

    private fun actualizarCuatrimestre(
        cuatrimestre: Cuatrimestre,
        inicioInscripciones: LocalDateTime?,
        finInscripciones: LocalDateTime?
    ): Cuatrimestre {
        cuatrimestre.actualizarFechas(inicioInscripciones, finInscripciones)
        return cuatrimestreRepository.save(cuatrimestre)
    }

    private fun crearComisiones(
        comisionesNuevas: List<ComisionNueva>,
        cuatrimestreObtenido: Cuatrimestre
    ): List<Conflicto> {
        val conflictos: MutableList<Conflicto> = mutableListOf()
        comisionesNuevas.forEach { comisionNueva ->
            val materia = materiaRepository.findByNombreIgnoringCaseOrCodigoIgnoringCase(comisionNueva.actividad, comisionNueva.codigo)
            if(!materia.isPresent) {
                val mensaje = "No existe la materia ${comisionNueva.actividad}"
                conflictos.add(Conflicto(comisionNueva.fila, mensaje))
            } else {
                val existeComision = comisionRespository
                    .findByNumeroAndMateriaAndCuatrimestre(comisionNueva.comision, materia.get(), cuatrimestreObtenido)
                if (existeComision.isPresent) {
                    val mensaje = "Ya existe la comision ${comisionNueva.comision}, materia ${comisionNueva.actividad}"
                    conflictos.add(Conflicto(comisionNueva.fila,  mensaje))
                } else {
                    saveComision(comisionNueva, materia.get(), cuatrimestreObtenido)
                }
            }
        }
        return conflictos
    }

    private fun saveComision(comisionACrear: ComisionNueva, materia: Materia, cuatrimestre: Cuatrimestre): Comision {
        return comisionRespository.save(
            Comision(
                materia,
                comisionACrear.comision,
                cuatrimestre,
                comisionACrear.horarios.map { HorarioDTO.aModelo(it) }.toMutableList(),
                comisionACrear.cuposTotales,
                comisionACrear.sobrecuposTotales,
                comisionACrear.modalidad,
                comisionACrear.locacion
            )
        )
    }

    private fun guardarComision(formularioComision: FormularioComision): Comision {
        val materia = materiaRepository.findById(formularioComision.codigoMateria)
            .orElseThrow { MateriaNoEncontrada(formularioComision.codigoMateria) }
        val cuatrimestre =
            cuatrimestreRepository.findByAnioAndSemestre(formularioComision.anio, formularioComision.semestre).get()
        return comisionRespository.save(
            Comision(
                materia,
                formularioComision.numero,
                cuatrimestre,
                formularioComision.horarios.map { HorarioDTO.aModelo(it) }.toMutableList(),
                formularioComision.cuposTotales,
                formularioComision.sobreCuposTotales,
                formularioComision.modalidad
            )
        )
    }
}
