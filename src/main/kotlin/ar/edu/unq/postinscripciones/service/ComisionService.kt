package ar.edu.unq.postinscripciones.service

import ar.edu.unq.postinscripciones.model.Materia
import ar.edu.unq.postinscripciones.model.comision.Comision
import ar.edu.unq.postinscripciones.model.cuatrimestre.Cuatrimestre
import ar.edu.unq.postinscripciones.model.exception.ExcepcionUNQUE
import ar.edu.unq.postinscripciones.model.exception.MateriaNoEncontradaExcepcion
import ar.edu.unq.postinscripciones.persistence.ComisionRespository
import ar.edu.unq.postinscripciones.persistence.CuatrimestreRepository
import ar.edu.unq.postinscripciones.persistence.MateriaRepository
import ar.edu.unq.postinscripciones.service.dto.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import javax.transaction.Transactional

@Service
class ComisionService {

    @Autowired
    private lateinit var comisionRespository: ComisionRespository

    @Autowired
    private lateinit var materiaRepository: MateriaRepository

    @Autowired
    private lateinit var cuatrimestreRepository: CuatrimestreRepository

    @Transactional
    fun actualizarOfertaAcademica(
        comisionesACrear: List<ComisionACrear>,
        inicioInscripciones: LocalDateTime? = null,
        finInscripciones: LocalDateTime? = null,
        cuatrimestre: Cuatrimestre? = null
    ): List<ConflictoComision> {
        val miCuatrimestre: Cuatrimestre = cuatrimestre ?: Cuatrimestre.actualConFechas(inicioInscripciones, finInscripciones)
        val existeCuatrimestre =
            cuatrimestreRepository.findByAnioAndSemestre(miCuatrimestre.anio, miCuatrimestre.semestre)

        val cuatrimestreObtenido = if (existeCuatrimestre.isPresent) {
            this.actualizarCuatrimestre(existeCuatrimestre.get(), inicioInscripciones, finInscripciones)
        } else {
            cuatrimestreRepository.save(miCuatrimestre)
        }

        return guardarComisionesBuscandoConflictos(comisionesACrear, cuatrimestreObtenido)
    }

    @Transactional
    fun ofertaDelCuatrimestre(cuatrimestre: Cuatrimestre = Cuatrimestre.actual()): List<ComisionDTO> {
        val oferta = comisionRespository.findByCuatrimestreAnioAndCuatrimestreSemestre(
            cuatrimestre.anio,
            cuatrimestre.semestre
        )
        chequearSiHayOferta(oferta, cuatrimestre)
        return oferta.map { ComisionDTO.desdeModelo(it) }
    }

    @Transactional
    fun comisionesPorSolicitudes(cuatrimestre: Cuatrimestre = Cuatrimestre.actual()): List<ComisionPorSolicitudes> {
        return comisionRespository
            .findByCuatrimestreAnioAndCuatrimestreSemestreOrderByCountSolicitudes(
                cuatrimestre.anio,
                cuatrimestre.semestre
            )
            .map { ComisionPorSolicitudes.desdeTupla(it) }
    }

    @Transactional
    fun crear(formularioComision: FormularioComision): Comision {
        return guardarComision(formularioComision)
    }

    @Transactional
    fun obtener(id: Long): ComisionDTO {
        val comision = comisionRespository.findById(id).orElseThrow { ExcepcionUNQUE("No se encuentra la comision") }
        return ComisionDTO.desdeModelo(comision)
    }

    @Transactional
    fun obtenerComisionesMateria(codigoMateria: String, cuatrimestre: Cuatrimestre = Cuatrimestre.actual()): List<ComisionDTO> {
        val cuatrimestreObtenido = cuatrimestreRepository.findByAnioAndSemestre(cuatrimestre.anio, cuatrimestre.semestre)
            .orElseThrow { ExcepcionUNQUE("No existe el cuatrimestre") }
        val materia = materiaRepository.findById(codigoMateria)
            .orElseThrow { ExcepcionUNQUE("No se encuentra la materia") }
        val comisiones = comisionRespository.findAllByMateriaAndCuatrimestreAnioAndCuatrimestreSemestre(materia, cuatrimestreObtenido.anio, cuatrimestreObtenido.semestre)

        return comisiones.map { ComisionDTO.desdeModelo(it) }
    }

    private fun guardarComisionesBuscandoConflictos(
        comisionesACrear: List<ComisionACrear>,
        cuatrimestre: Cuatrimestre,
    ): List<ConflictoComision> {
        val comisionesConflictivas = mutableListOf<ConflictoComision>()
        comisionesACrear.forEach { comisionACrear ->
            val materia = materiaRepository.findMateriaByCodigo(comisionACrear.codigoMateria)
                .orElseThrow { MateriaNoEncontradaExcepcion() }
            val existeComision = comisionRespository
                .findByNumeroAndMateriaAndCuatrimestre(comisionACrear.numeroComision, materia, cuatrimestre)
            if (existeComision.isPresent) {
                comisionesConflictivas.add(
                    ConflictoComision(
                        ComisionDTO.desdeModelo(existeComision.get()),
                        comisionACrear
                    )
                )
            } else {
                guardarComision(comisionACrear, materia, cuatrimestre)
            }
        }
        return comisionesConflictivas
    }

    @Transactional
    fun modificarHorarios(comisionId: Long, nuevosHorarios: List<HorarioDTO>): ComisionDTO {
        val comision = comisionRespository
                .findById(comisionId)
                .orElseThrow { ExcepcionUNQUE("No se encontro la comision") }

        comision.modificarHorarios(nuevosHorarios.map { HorarioDTO.aModelo(it) })
        return ComisionDTO.desdeModelo(comisionRespository.save(comision))
    }

    private fun actualizarCuatrimestre(
        cuatrimestre: Cuatrimestre,
        inicioInscripciones: LocalDateTime?,
        finInscripciones: LocalDateTime?
    ): Cuatrimestre {
        cuatrimestre.actualizarFechas(inicioInscripciones, finInscripciones)
        return cuatrimestreRepository.save(cuatrimestre)
    }

    private fun chequearSiHayOferta(
        oferta: List<Comision>,
        cuatrimestre: Cuatrimestre
    ) {
        if (oferta.isEmpty()) throw ExcepcionUNQUE("No hay oferta registrada para el cuatrimestre ${cuatrimestre.anio}, ${cuatrimestre.semestre}")
    }

    private fun guardarComision(
        comisionACrear: ComisionACrear,
        materia: Materia,
        cuatrimestre: Cuatrimestre
    ): Comision {
        return comisionRespository.save(
            Comision(
                materia,
                comisionACrear.numeroComision,
                cuatrimestre,
                comisionACrear.horarios.map { HorarioDTO.aModelo(it) },
                comisionACrear.cuposTotales,
                comisionACrear.sobrecuposTotales
            )
        )
    }

    private fun guardarComision(formularioComision: FormularioComision): Comision {
        val materia = materiaRepository.findById(formularioComision.codigoMateria)
            .orElseThrow { MateriaNoEncontradaExcepcion() }
        val cuatrimestre =
            cuatrimestreRepository.findByAnioAndSemestre(formularioComision.anio, formularioComision.semestre).get()
        return comisionRespository.save(
            Comision(
                materia,
                formularioComision.numero,
                cuatrimestre,
                formularioComision.horarios.map { HorarioDTO.aModelo(it) },
                formularioComision.cuposTotales,
                formularioComision.sobreCuposTotales,
                formularioComision.modalidad
            )
        )
    }
}