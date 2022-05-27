package ar.edu.unq.postinscripciones.service.dto

import ar.edu.unq.postinscripciones.model.comision.Dia
import ar.edu.unq.postinscripciones.model.comision.Horario
import io.swagger.annotations.ApiModelProperty
import java.time.LocalTime

data class HorarioDTO(
    @ApiModelProperty(example = "LUNES", required = true)
    val dia: Dia,
    @ApiModelProperty(example = "18:00", required = true)
    val inicio: String,
    @ApiModelProperty(example = "21:00", required = true)
    val fin: String
) {
    companion object {
        fun aModelo(horarioDTO: HorarioDTO): Horario {
            return Horario(horarioDTO.dia, LocalTime.parse(horarioDTO.inicio), LocalTime.parse(horarioDTO.fin))
        }

        fun desdeModelo(horario: Horario): HorarioDTO {
            return HorarioDTO(horario.dia, horario.inicio.toString(), horario.fin.toString())
        }
    }
}