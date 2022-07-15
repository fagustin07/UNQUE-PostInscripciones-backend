package ar.edu.unq.postinscripciones.persistence

import ar.edu.unq.postinscripciones.model.Materia
import ar.edu.unq.postinscripciones.model.comision.Comision
import ar.edu.unq.postinscripciones.model.cuatrimestre.Cuatrimestre
import ar.edu.unq.postinscripciones.model.cuatrimestre.Semestre
import ar.edu.unq.postinscripciones.service.dto.carga.datos.Locacion
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ComisionRespository : CrudRepository<Comision, Long> {
    fun findByCuatrimestreAnioAndCuatrimestreSemestreAndMateriaNombreIgnoreCaseContaining(anio: Int, semestre: Semestre, patronNombre: String): List<Comision>
    fun findByNumeroAndMateriaAndCuatrimestre(
        numeroComision: Int,
        materia: Materia,
        cuatrimestre: Cuatrimestre
    ): Optional<Comision>

    fun findByMateriaCodigo(codigo: String): List<Comision>
    fun deleteByMateriaCodigo(codigo: String)
    fun findByCuatrimestre(cuatrimestreObtenido: Cuatrimestre): List<Comision>

    fun findByNumeroAndMateriaAndCuatrimestreAndLocacion(
        comision: Int,
        materia: Materia,
        cuatrimestre: Cuatrimestre,
        locacion: Locacion
    ): Optional<Comision>

    fun findAllByMateriaAndCuatrimestreAnioAndCuatrimestreSemestreOrderByNumero(
        materia: Materia,
        anio: Int,
        semestre: Semestre
    ): List<Comision>

}