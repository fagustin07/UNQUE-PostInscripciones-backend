package ar.edu.unq.postinscripciones.service

import ar.edu.unq.postinscripciones.model.cuatrimestre.Cuatrimestre
import ar.edu.unq.postinscripciones.model.exception.ExcepcionUNQUE
import ar.edu.unq.postinscripciones.persistence.CuatrimestreRepository
import ar.edu.unq.postinscripciones.service.dto.FormularioCuatrimestre
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class CuatrimestreService {

    @Autowired
    private lateinit var cuatrimestreRepository: CuatrimestreRepository

    @Transactional
    fun crear(formularioRegistrarCuatrimestre: FormularioCuatrimestre): Cuatrimestre {
        val existeCuatrimestre = cuatrimestreRepository.findByAnioAndSemestre(
            formularioRegistrarCuatrimestre.anio,
            formularioRegistrarCuatrimestre.semestre
        )
        if (existeCuatrimestre.isPresent) {
            throw ExcepcionUNQUE("Ya existe el cuatrimestre que desea crear.")
        } else {
            return cuatrimestreRepository.save(
                Cuatrimestre(
                    formularioRegistrarCuatrimestre.anio,
                    formularioRegistrarCuatrimestre.semestre
                )
            )
        }
    }

    @Transactional
    fun obtener(cuatrimestre: Cuatrimestre = Cuatrimestre.actual()): Cuatrimestre {
        return cuatrimestreRepository.findByAnioAndSemestre(cuatrimestre.anio, cuatrimestre.semestre)
            .orElseThrow { ExcepcionUNQUE("No se ha encontrado el cuatrimestre") }
    }
}
